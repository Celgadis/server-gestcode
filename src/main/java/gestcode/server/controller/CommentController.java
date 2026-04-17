package gestcode.server.controller;

import gestcode.server.dto.request.CommentRequestDTO;
import gestcode.server.dto.response.CommentResponseDTO;
import gestcode.server.service.CommentService;
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
 * Controlador de l'API per a la gestió de comentaris d'usuaris sobre llibres.
 * Proporciona endpoints per crear/actualitzar comentaris (usuaris autenticats),
 * consultar comentaris propis i administrar els comentaris del sistema
 * (admins).
 *
 * @author Jordi Verdalet Carrera
 */
@RestController
@RequestMapping("/api/comments")
@Tag(name = "Comentaris", description = "Endpoints de gestió de comentaris d'usuaris sobre llibres")
public class CommentController {

    private final CommentService commentService;

    /**
     * Constructor que injecta el servei de comentaris.
     *
     * @param commentService El servei de comentaris.
     * @author Jordi Verdalet Carrera
     */
    @Autowired
    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    /**
     * Endpoint per crear o actualitzar el comentari de l'usuari autenticat sobre un
     * llibre
     * (operació upsert). Si l'usuari ja havia comentat el llibre, s'actualitza el
     * comentari.
     * Si no, es crea un de nou. L'usuari s'identifica pel token JWT.
     *
     * @param dto            Les dades del comentari (bookId i comment).
     * @param authentication L'autenticació de Spring Security injectada
     *                       automàticament.
     * @return El comentari creat o actualitzat.
     * @author Jordi Verdalet Carrera
     */
    @Operation(summary = "Crear o actualitzar comentari", description = "Crea o actualitza el comentari de l'usuari autenticat per a un llibre (upsert). "
            + "L'usuari s'extreu del token JWT. (Requereix USER o ADMIN)")
    @ApiResponse(responseCode = "200", description = "Comentari creat o actualitzat correctament")
    @ApiResponse(responseCode = "400", description = "Dades invàlides")
    @ApiResponse(responseCode = "404", description = "Llibre no trobat")
    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<CommentResponseDTO> upsertComment(
            @Valid @RequestBody CommentRequestDTO dto,
            Authentication authentication) {
        CommentResponseDTO response = commentService.upsertComment(dto, authentication.getName());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Endpoint per obtenir tots els comentaris de l'usuari autenticat, paginades.
     *
     * @param authentication L'autenticació de Spring Security injectada
     *                       automàticament.
     * @param pageable       Les dades de paginació i ordenació.
     * @return Pàgina de comentaris de l'usuari autenticat.
     * @author Jordi Verdalet Carrera
     */
    @Operation(summary = "Els meus comentaris", description = "Retorna tots els comentaris de l'usuari autenticat. Suporta paginació (page, size, sort). (Requereix USER o ADMIN)")
    @ApiResponse(responseCode = "200", description = "Llistat obtingut correctament")
    @GetMapping("/my")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Page<CommentResponseDTO>> getMyComments(
            Authentication authentication,
            @ParameterObject Pageable pageable) {
        Page<CommentResponseDTO> response = commentService.getMyComments(authentication.getName(), pageable);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Endpoint per obtenir tots els comentaris del sistema, amb filtres opcionals
     * per usuari i/o llibre. Només accessible per administradors.
     *
     * @param userId   Filtre opcional per l'identificador de l'usuari.
     * @param bookId   Filtre opcional per l'identificador del llibre.
     * @param pageable Les dades de paginació i ordenació.
     * @return Pàgina de comentaris filtrats.
     * @author Jordi Verdalet Carrera
     */
    @Operation(summary = "Llistar tots els comentaris", description = "Retorna tots els comentaris del sistema amb filtres opcionals. Suporta paginació (page, size, sort). (Requereix ADMIN)")
    @ApiResponse(responseCode = "200", description = "Llistat obtingut correctament")
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<CommentResponseDTO>> getAllComments(
            @Parameter(description = "Filtre per ID d'usuari") @RequestParam(required = false) Long userId,
            @Parameter(description = "Filtre per ID de llibre") @RequestParam(required = false) Long bookId,
            @ParameterObject Pageable pageable) {
        Page<CommentResponseDTO> response = commentService.getAllComments(userId, bookId, pageable);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Endpoint per obtenir tots els comentaris d'un llibre concret.
     * Accessible per usuaris autenticats.
     *
     * @param bookId   L'identificador del llibre.
     * @param pageable Les dades de paginació i ordenació.
     * @return Pàgina de comentaris del llibre.
     * @author Jordi Verdalet Carrera
     */
    @Operation(summary = "Comentaris d'un llibre", description = "Retorna tots els comentaris d'un llibre concret. Suporta paginació (page, size, sort). (Requereix estar autenticat)")
    @ApiResponse(responseCode = "200", description = "Llistat obtingut correctament")
    @ApiResponse(responseCode = "404", description = "Llibre no trobat")
    @GetMapping("/book/{bookId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Page<CommentResponseDTO>> getCommentsByBook(
            @PathVariable Long bookId,
            @ParameterObject Pageable pageable) {
        Page<CommentResponseDTO> response = commentService.getCommentsByBook(bookId, pageable);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Endpoint per obtenir tots els comentaris d'un usuari concret.
     * Accessible per administradors.
     *
     * @param userId   L'identificador de l'usuari.
     * @param pageable Les dades de paginació i ordenació.
     * @return Pàgina de comentaris de l'usuari.
     * @author Jordi Verdalet Carrera
     */
    @Operation(summary = "Comentaris d'un usuari", description = "Retorna tots els comentaris d'un usuari concret. Suporta paginació (page, size, sort). (Requereix ADMIN)")
    @ApiResponse(responseCode = "200", description = "Llistat obtingut correctament")
    @ApiResponse(responseCode = "404", description = "Usuari no trobat")
    @GetMapping("/user/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<CommentResponseDTO>> getCommentsByUser(
            @PathVariable Long userId,
            @ParameterObject Pageable pageable) {
        Page<CommentResponseDTO> response = commentService.getCommentsByUser(userId, pageable);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Endpoint per esborrar un comentari segons el seu identificador.
     * Accessible per administradors.
     *
     * @param id L'identificador del comentari.
     * @return El missatge d'èxit de l'operació.
     * @author Jordi Verdalet Carrera
     */
    @Operation(summary = "Esborrar comentari", description = "Elimina un comentari mitjançant el seu identificador. (Requereix ADMIN)")
    @ApiResponse(responseCode = "200", description = "Comentari esborrat correctament")
    @ApiResponse(responseCode = "404", description = "Comentari no trobat")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<gestcode.server.dto.response.MessageResponseDTO> deleteComment(
            @PathVariable Long id) {
        commentService.deleteComment(id);
        return ResponseEntity
                .ok(new gestcode.server.dto.response.MessageResponseDTO(id, "Comentari esborrat correctament"));
    }

    /**
     * Endpoint per esborrar un comentari d'un usuari sobre un llibre concret.
     * Accessible per administradors.
     *
     * @param userId L'identificador de l'usuari.
     * @param bookId L'identificador del llibre.
     * @return El missatge d'èxit de l'operació amb l'ID del comentari esborrat.
     * @author Jordi Verdalet Carrera
     */
    @Operation(summary = "Esborrar comentari d'un usuari i llibre", description = "Elimina el comentari d'un usuari per a un llibre concret. (Requereix ADMIN)")
    @ApiResponse(responseCode = "200", description = "Comentari esborrat correctament")
    @ApiResponse(responseCode = "404", description = "Comentari no trobat")
    @DeleteMapping("/user/{userId}/book/{bookId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<gestcode.server.dto.response.MessageResponseDTO> deleteCommentByUserAndBook(
            @PathVariable Long userId,
            @PathVariable Long bookId) {
        Long deletedId = commentService.deleteCommentByUserAndBook(userId, bookId);
        return ResponseEntity
                .ok(new gestcode.server.dto.response.MessageResponseDTO(deletedId, "Comentari esborrat correctament"));
    }

}
