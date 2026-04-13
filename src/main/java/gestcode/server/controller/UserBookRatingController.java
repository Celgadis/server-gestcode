package gestcode.server.controller;

import gestcode.server.dto.request.RatingRequestDTO;
import gestcode.server.dto.response.RatingResponseDTO;
import gestcode.server.service.UserBookRatingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador de l'API per a la gestió de puntuacions d'usuaris sobre llibres.
 * Proporciona endpoints per crear/actualitzar puntuacions (usuaris autenticats),
 * consultar puntuacions pròpies i administrar les puntuacions del sistema (admins).
 *
 * @author Jordi Verdalet Carrera
 */
@RestController
@RequestMapping("/api/ratings")
@Tag(name = "Puntuacions", description = "Endpoints de gestió de puntuacions d'usuaris sobre llibres")
public class UserBookRatingController {

    private final UserBookRatingService ratingService;

    /**
     * Constructor que injecta el servei de puntuacions.
     *
     * @param ratingService El servei de puntuacions.
     * @author Jordi Verdalet Carrera
     */
    @Autowired
    public UserBookRatingController(UserBookRatingService ratingService) {
        this.ratingService = ratingService;
    }

    /**
     * Endpoint per crear o actualitzar la puntuació de l'usuari autenticat sobre un llibre
     * (operació upsert). Si l'usuari ja havia puntuat el llibre, s'actualitza la nota.
     * Si no, es crea una de nova. L'usuari s'identifica pel token JWT.
     *
     * @param dto            Les dades de la puntuació (bookId i rating).
     * @param authentication L'autenticació de Spring Security injectada automàticament.
     * @return La puntuació creada o actualitzada.
     * @author Jordi Verdalet Carrera
     */
    @Operation(
        summary = "Crear o actualitzar puntuació",
        description = "Crea o actualitza la puntuació de l'usuari autenticat per a un llibre (upsert). "
                    + "L'usuari s'extreu del token JWT. (Requereix USER o ADMIN)"
    )
    @ApiResponse(responseCode = "200", description = "Puntuació creada o actualitzada correctament")
    @ApiResponse(responseCode = "400", description = "Dades invàlides")
    @ApiResponse(responseCode = "404", description = "Llibre no trobat")
    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<RatingResponseDTO> upsertRating(
            @Valid @RequestBody RatingRequestDTO dto,
            Authentication authentication) {
        RatingResponseDTO response = ratingService.upsertRating(dto, authentication.getName());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Endpoint per obtenir totes les puntuacions de l'usuari autenticat, paginades.
     *
     * @param authentication L'autenticació de Spring Security injectada automàticament.
     * @param pageable       Les dades de paginació i ordenació.
     * @return Pàgina de puntuacions de l'usuari autenticat.
     * @author Jordi Verdalet Carrera
     */
    @Operation(
        summary = "Les meves puntuacions",
        description = "Retorna totes les puntuacions de l'usuari autenticat. Suporta paginació (page, size, sort). (Requereix USER o ADMIN)"
    )
    @ApiResponse(responseCode = "200", description = "Llistat obtingut correctament")
    @GetMapping("/my")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Page<RatingResponseDTO>> getMyRatings(
            Authentication authentication,
            @ParameterObject Pageable pageable) {
        Page<RatingResponseDTO> response = ratingService.getMyRatings(authentication.getName(), pageable);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Endpoint per obtenir totes les puntuacions del sistema, com filtres opcionals
     * per usuari i/o llibre. Només accessible per administradors.
     *
     * @param userId   Filtre opcional per l'identificador de l'usuari.
     * @param bookId   Filtre opcional per l'identificador del llibre.
     * @param pageable Les dades de paginació i ordenació.
     * @return Pàgina de puntuacions filtrades.
     * @author Jordi Verdalet Carrera
     */
    @Operation(
        summary = "Llistar totes les puntuacions",
        description = "Retorna totes les puntuacions del sistema amb filtres opcionals. Suporta paginació (page, size, sort). (Requereix ADMIN)"
    )
    @ApiResponse(responseCode = "200", description = "Llistat obtingut correctament")
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<RatingResponseDTO>> getAllRatings(
            @Parameter(description = "Filtre per ID d'usuari") @RequestParam(required = false) Long userId,
            @Parameter(description = "Filtre per ID de llibre") @RequestParam(required = false) Long bookId,
            @ParameterObject Pageable pageable) {
        Page<RatingResponseDTO> response = ratingService.getAllRatings(userId, bookId, pageable);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Endpoint per obtenir totes les puntuacions d'un llibre concret.
     * Accessible per administradors.
     *
     * @param bookId   L'identificador del llibre.
     * @param pageable Les dades de paginació i ordenació.
     * @return Pàgina de puntuacions del llibre.
     * @author Jordi Verdalet Carrera
     */
    @Operation(
        summary = "Puntuacions d'un llibre",
        description = "Retorna totes les puntuacions d'un llibre concret. Suporta paginació (page, size, sort). (Requereix ADMIN)"
    )
    @ApiResponse(responseCode = "200", description = "Llistat obtingut correctament")
    @ApiResponse(responseCode = "404", description = "Llibre no trobat")
    @GetMapping("/book/{bookId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<RatingResponseDTO>> getRatingsByBook(
            @PathVariable Long bookId,
            @ParameterObject Pageable pageable) {
        Page<RatingResponseDTO> response = ratingService.getRatingsByBook(bookId, pageable);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Endpoint per obtenir totes les puntuacions d'un usuari concret.
     * Accessible per administradors.
     *
     * @param userId   L'identificador de l'usuari.
     * @param pageable Les dades de paginació i ordenació.
     * @return Pàgina de puntuacions de l'usuari.
     * @author Jordi Verdalet Carrera
     */
    @Operation(
        summary = "Puntuacions d'un usuari",
        description = "Retorna totes les puntuacions d'un usuari concret. Suporta paginació (page, size, sort). (Requereix ADMIN)"
    )
    @ApiResponse(responseCode = "200", description = "Llistat obtingut correctament")
    @ApiResponse(responseCode = "404", description = "Usuari no trobat")
    @GetMapping("/user/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<RatingResponseDTO>> getRatingsByUser(
            @PathVariable Long userId,
            @ParameterObject Pageable pageable) {
        Page<RatingResponseDTO> response = ratingService.getRatingsByUser(userId, pageable);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Endpoint per activar o desactivar una puntuació concreta. En canviar l'estat,
     * es recalcula automàticament la mitjana del llibre. Només accessible per admins.
     *
     * @param ratingId L'identificador de la puntuació.
     * @param disabled Cert ({@code true}) per desactivar, fals ({@code false}) per activar.
     * @return La puntuació actualitzada.
     * @author Jordi Verdalet Carrera
     */
    @Operation(
        summary = "Activar o desactivar puntuació",
        description = "Canvia l'estat d'una puntuació (activada/desactivada) i recalcula la "
                    + "mitjana del llibre. (Requereix ADMIN)"
    )
    @ApiResponse(responseCode = "200", description = "Estat actualitzat correctament")
    @ApiResponse(responseCode = "404", description = "Puntuació no trobada")
    @PatchMapping("/{ratingId}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<RatingResponseDTO> setRatingStatus(
            @PathVariable Long ratingId,
            @Parameter(description = "true per desactivar, false per activar")
            @RequestParam boolean disabled) {
        RatingResponseDTO response = ratingService.setRatingDisabled(ratingId, disabled);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
