package gestcode.server.controller;

import gestcode.server.dto.request.UserUpdateRequestDTO;
import gestcode.server.dto.response.UserListResponseDTO;
import gestcode.server.dto.response.UserProfileResponseDTO;
import gestcode.server.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador REST per a la gestió d'usuaris (CRUD).
 * Tots els endpoints d'aquest controlador requereixen autenticació.
 *
 * @author Jordi Verdalet Carrera
 */
@RestController
@RequestMapping("/api/users")
@Tag(name = "Gestió d'Usuaris", description = "Endpoints CRUD per a Usuaris, requereix Autenticació")
@SecurityRequirement(name = "bearerAuth")
public class UserController {

    private final UserService userService;

    /**
     * Constructor del controlador d'usuaris.
     *
     * @param userService Servei d'usuaris.
     * @author Jordi Verdalet Carrera
     */
    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Endpoint per obtenir les dades d'un usuari segons el seu ID. nomes per
     * administradors o per el mateix usuari del id.
     *
     * @param id Identificador de l'usuari.
     * @return Resposta amb el perfil de l'usuari trobat i codi HTTP 200.
     * @author Jordi Verdalet Carrera
     */
    @GetMapping("/{id}")
    @Operation(summary = "Obtenir usuari per ID", description = "Recupera els detalls d'un usuari existent per la seva ID")
    @ApiResponse(responseCode = "200", description = "Usuari trobat")
    @ApiResponse(responseCode = "404", description = "Usuari no trobat")
    public ResponseEntity<UserProfileResponseDTO> getUserById(@PathVariable Long id) {
        UserProfileResponseDTO response = userService.getUserById(id);
        return ResponseEntity.ok(response);
    }

    /**
     * Endpoint per obtenir les dades del propi usuari autenticat.
     *
     * @return Resposta amb el perfil de l'usuari i codi HTTP 200.
     * @author Jordi Verdalet Carrera
     */
    @GetMapping("/me")
    @Operation(summary = "Obtenir usuari actual", description = "Recupera els detalls de l'usuari autenticat")
    @ApiResponse(responseCode = "200", description = "Usuari trobat")
    @ApiResponse(responseCode = "404", description = "Usuari no trobat")
    public ResponseEntity<UserProfileResponseDTO> getMe() {
        UserProfileResponseDTO response = userService.getMe();
        return ResponseEntity.ok(response);
    }

    /**
     * Endpoint per llistar tots els usuaris. Només accessible per usuaris amb rol
     * ADMIN.
     *
     * @param keyword Paraula clau opcional per filtrar usuaris.
     * @param page    Número de pàgina.
     * @param size    Mida de la pàgina.
     * @return Resposta amb la llista paginada d'usuaris i codi HTTP 200.
     * @author Jordi Verdalet Carrera
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Llistar usuaris", description = "Recupera una llista paginada d'usuaris, filtratge opcional per paraula clau")
    @ApiResponse(responseCode = "200", description = "Operació exitosa")
    public ResponseEntity<UserListResponseDTO> listUsers(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        UserListResponseDTO response = userService.listUsers(keyword, pageable);
        return ResponseEntity.ok(response);
    }

    /**
     * Endpoint per actualitzar les dades d'un usuari. nomes per administradors o
     * per el mateix usuari del id.
     *
     * @param id      Identificador de l'usuari a actualitzar.
     * @param request Noves dades de l'usuari.
     * @return Resposta amb el perfil de l'usuari actualitzat i codi HTTP 200.
     * @author Jordi Verdalet Carrera
     */
    @PutMapping("/{id}")
    @Operation(summary = "Actualitzar usuari", description = "Actualitza els detalls d'un usuari existent")
    @ApiResponse(responseCode = "200", description = "Usuari actualitzat amb èxit")
    @ApiResponse(responseCode = "404", description = "Usuari no trobat")
    public ResponseEntity<UserProfileResponseDTO> updateUser(
            @PathVariable Long id,
            @RequestBody UserUpdateRequestDTO request) {
        UserProfileResponseDTO response = userService.updateUser(id, request);
        return ResponseEntity.ok(response);
    }
}
