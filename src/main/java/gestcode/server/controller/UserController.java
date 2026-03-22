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

@RestController
@RequestMapping("/api/users")
@Tag(name = "Gestió d'Usuaris", description = "Endpoints CRUD per a Usuaris, requereix Autenticació")
@SecurityRequirement(name = "bearerAuth")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtenir usuari per ID", description = "Recupera els detalls d'un usuari existent per la seva ID")
    @ApiResponse(responseCode = "200", description = "Usuari trobat")
    @ApiResponse(responseCode = "404", description = "Usuari no trobat")
    public ResponseEntity<UserProfileResponseDTO> getUserById(@PathVariable Long id) {
        UserProfileResponseDTO response = userService.getUserById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/me")
    @Operation(summary = "Obtenir usuari actual", description = "Recupera els detalls de l'usuari autenticat")
    @ApiResponse(responseCode = "200", description = "Usuari trobat")
    @ApiResponse(responseCode = "404", description = "Usuari no trobat")
    public ResponseEntity<UserProfileResponseDTO> getMe() {
        UserProfileResponseDTO response = userService.getMe();
        return ResponseEntity.ok(response);
    }

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
