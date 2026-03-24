package gestcode.server.controller;

import gestcode.server.dto.request.UserRegisterRequestDTO;
import gestcode.server.dto.response.UserProfileResponseDTO;
import gestcode.server.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import gestcode.server.dto.request.LoginRequestDTO;
import gestcode.server.dto.response.JwtResponseDTO;
import gestcode.server.security.JwtUtils;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * Controlador REST per a la gestió de l'autenticació i el registre d'usuaris.
 *
 * @author Jordi Verdalet Carrera
 */
@RestController
@RequestMapping("/api/auth")
@Tag(name = "Autenticació", description = "Endpoints per a l'autenticació i registre d'usuaris")
public class AuthController {

    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;

    /**
     * Constructor del controlador d'autenticació.
     *
     * @param userService           Servei d'usuaris.
     * @param authenticationManager Gestor d'autenticació de Spring Security.
     * @param jwtUtils              Utilitat per a la gestió de tokens JWT.
     * @author Jordi Verdalet Carrera
     */
    public AuthController(UserService userService, AuthenticationManager authenticationManager, JwtUtils jwtUtils) {
        this.userService = userService;
        this.authenticationManager = authenticationManager;
        this.jwtUtils = jwtUtils;
    }

    /**
     * Endpoint per registrar un usuari.
     *
     * @param request Dades de registre de l'usuari.
     * @return Resposta amb les dades de l'usuari creat i codi HTTP 201.
     * @author Jordi Verdalet Carrera
     */
    @PostMapping("/register")
    @Operation(summary = "Registrar un nou usuari", description = "Registra un nou usuari i retorna el seu perfil. L'usuari es crea amb l'estat PENDING_ACTIVATION.")
    @ApiResponse(responseCode = "201", description = "Usuari registrat amb èxit")
    @ApiResponse(responseCode = "400", description = "Dades de petició invàlides o l'usuari/correu electrònic ja existeix")
    public ResponseEntity<UserProfileResponseDTO> registerUser(@Valid @RequestBody UserRegisterRequestDTO request) {
        UserProfileResponseDTO response = userService.registerUser(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    /**
     * Endpoint per iniciar sessió.
     *
     * @param request Dades d'inici de sessió (usuari/email i contrasenya). es pot
     *                utilitzar el nom d'usuari o el correu electrònic
     *                indistintament.
     * @return Resposta amb el token JWT i codi HTTP 200.
     * @author Jordi Verdalet Carrera
     */
    @PostMapping("/login")
    @Operation(summary = "Inici de sessió d'usuari", description = "Autentica un usuari i retorna un token JWT")
    @ApiResponse(responseCode = "200", description = "Autenticat amb èxit")
    @ApiResponse(responseCode = "401", description = "Credencials invàlides")
    public ResponseEntity<JwtResponseDTO> loginUser(@Valid @RequestBody LoginRequestDTO request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsernameOrEmail(), request.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String jwt = jwtUtils.generateToken(userDetails);

        return ResponseEntity.ok(new JwtResponseDTO(jwt));
    }
}
