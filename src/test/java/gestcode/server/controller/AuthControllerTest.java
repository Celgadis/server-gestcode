package gestcode.server.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import gestcode.server.dto.request.UserRegisterRequestDTO;
import gestcode.server.dto.response.UserProfileResponseDTO;
import gestcode.server.model.enums.Role;
import gestcode.server.model.enums.UserStatus;
import gestcode.server.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.security.authentication.AuthenticationManager;
import gestcode.server.security.JwtUtils;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class AuthControllerTest {

    private MockMvc mockMvc;

    @Mock
    private UserService userService;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtUtils jwtUtils;

    @InjectMocks
    private AuthController authController;

    private ObjectMapper objectMapper = new ObjectMapper().registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule());

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(authController).build();
    }

    @Test
    void registerUser_Success() throws Exception {
        // 1. Preparar dades de la petició de registre
        UserRegisterRequestDTO request = new UserRegisterRequestDTO();
        request.setUsername("testuser");
        request.setPassword("password123");
        request.setFirstName("Test");
        request.setLastName1("User");
        request.setEmail("test@example.com");

        // 2. Preparar el resultat esperat del servei
        UserProfileResponseDTO response = new UserProfileResponseDTO();
        response.setId(1L);
        response.setUsername("testuser");
        response.setEmail("test@example.com");
        response.setStatus(UserStatus.PENDING_ACTIVATION);
        response.setEnabled(false);
        response.setRole(Role.USER);

        when(userService.registerUser(any(UserRegisterRequestDTO.class))).thenReturn(response);

        // 3. Executar la petició POST a /api/auth/register
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                // 4. Comprovar la resposta i les dades retornades
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.username").value("testuser"))
                .andExpect(jsonPath("$.status").value("PENDING_ACTIVATION"))
                .andExpect(jsonPath("$.enabled").value(false))
                .andExpect(jsonPath("$.role").value("USER"));
    }

    @Test
    void registerUser_ValidationFailure() throws Exception {
        // 1. Preparar dades de petició invàlides (buides)
        UserRegisterRequestDTO request = new UserRegisterRequestDTO();

        // 2. Executar la petició POST i comprovar que retorna Bad Request
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}
