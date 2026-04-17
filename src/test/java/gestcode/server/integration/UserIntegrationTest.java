package gestcode.server.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import gestcode.server.dto.request.LoginRequestDTO;
import gestcode.server.dto.response.JwtResponseDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.http.HttpStatus;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class UserIntegrationTest {

    @Value("${local.server.port}")
    private int port;

    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule());

    private final HttpClient httpClient = HttpClient.newHttpClient();

    private String getAuthToken(String username, String password) throws Exception {
        LoginRequestDTO loginRequest = new LoginRequestDTO(username, password);
        String requestBody = objectMapper.writeValueAsString(loginRequest);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + port + "/api/auth/login"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        
        assertEquals(HttpStatus.OK.value(), response.statusCode(), "L'inici de sessió hauria de retornar 200 OK");
        
        JwtResponseDTO jwtResponse = objectMapper.readValue(response.body(), JwtResponseDTO.class);
        assertNotNull(jwtResponse.getToken(), "Hauria de retornar un Token JWT");
        return jwtResponse.getToken();
    }

    @Test
    void testAdminFlux_CanListAllUsers() throws Exception {
        // 1. Obtenir token de l'usuari ADMIN
        String token = getAuthToken("admin", "admin1234");
        
        // 2. Preparar la petició GET a /api/users
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + port + "/api/users"))
                .header("Authorization", "Bearer " + token)
                .GET()
                .build();
        
        // 3. Executar la crida passant la xarxa
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
                
        // 4. L'administrador ha de tenir accés
        assertEquals(HttpStatus.OK.value(), response.statusCode(), "L'admin ha de poder llistar usuaris");
        assertNotNull(response.body(), "Hauria de rebre el JSON amb els usuaris");
        assertTrue(response.body().contains("admin"), "L'admin hauria d'aparèixer a la llista");
    }

    @Test
    void testUserFlux_CannotListAllUsers() throws Exception {
        // 1. Obtenir token d'un usuari normal
        String token = getAuthToken("test1", "test1234");
        
        // 2. Preparar capçaleres
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + port + "/api/users"))
                .header("Authorization", "Bearer " + token)
                .GET()
                .build();
        
        // 3. Intentar llistar usuaris
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
                
        // 4. L'usuari no hi té accés
        assertTrue(response.statusCode() >= 400 && response.statusCode() < 500,
                "Un usuari normal hauria de rebre error 4xx a rutes admin");
    }

    @Test
    void testSecondUser_CanLogin() throws Exception {
        // Comprovem que test2 també entra des d'un procés extern
        String token = getAuthToken("test2", "test1234");
        assertNotNull(token, "L'usuari test2 s'ha pogut autenticar satisfactòriament");
    }
}
