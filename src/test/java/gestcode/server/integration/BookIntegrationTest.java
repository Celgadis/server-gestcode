package gestcode.server.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import gestcode.server.dto.request.BookCreateRequestDTO;
import gestcode.server.dto.request.BookUpdateRequestDTO;
import gestcode.server.dto.request.LoginRequestDTO;
import gestcode.server.dto.response.BookResponseDTO;
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
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class BookIntegrationTest {

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
        assertEquals(HttpStatus.OK.value(), response.statusCode());

        JwtResponseDTO jwtResponse = objectMapper.readValue(response.body(), JwtResponseDTO.class);
        return jwtResponse.getToken();
    }

    @Test
    void adminCanManageBooks() throws Exception {
        // 1. Obtenir token ADMIN
        String token = getAuthToken("admin", "admin1234");
        String uniqueIsbn = UUID.randomUUID().toString().substring(0, 13); // ISBN única

        // 2. Crear llibre (POST)
        BookCreateRequestDTO createReq = new BookCreateRequestDTO();
        createReq.setTitle("Llibre d'Integració");
        createReq.setAuthor("Autor Test");
        createReq.setIsbn(uniqueIsbn);
        createReq.setQuantity(5);
        createReq.setLanguage("ca");
        createReq.setYear(2023);
        createReq.setPages(100);
        createReq.setGenre("Ficció");
        createReq.setDescription("Descripció de test");

        HttpRequest createRequest = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + port + "/api/books"))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + token)
                .POST(HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(createReq)))
                .build();

        HttpResponse<String> createResponse = httpClient.send(createRequest, HttpResponse.BodyHandlers.ofString());
        assertEquals(HttpStatus.CREATED.value(), createResponse.statusCode());
        
        BookResponseDTO createdBook = objectMapper.readValue(createResponse.body(), BookResponseDTO.class);
        assertNotNull(createdBook.getId());

        // 3. Modificar llibre (PUT)
        BookUpdateRequestDTO updateReq = new BookUpdateRequestDTO();
        updateReq.setIsbn(uniqueIsbn);
        updateReq.setTitle("Llibre Modificat");
        updateReq.setAuthor("Autor Test");
        updateReq.setYear(2023);
        updateReq.setGenre("Ficció");
        updateReq.setPages(100);
        updateReq.setQuantity(10);
        updateReq.setLanguage("ca");
        updateReq.setDescription("Descripció modificada");

        HttpRequest updateRequest = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + port + "/api/books/" + createdBook.getId()))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + token)
                .PUT(HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(updateReq)))
                .build();

        HttpResponse<String> updateResponse = httpClient.send(updateRequest, HttpResponse.BodyHandlers.ofString());
        assertEquals(HttpStatus.OK.value(), updateResponse.statusCode());

        BookResponseDTO updatedBook = objectMapper.readValue(updateResponse.body(), BookResponseDTO.class);
        assertEquals("Llibre Modificat", updatedBook.getTitle());
        assertEquals(10, updatedBook.getQuantity());

        // 4. Esborrar llibre neteja final (DELETE) - neteja
        HttpRequest deleteRequest = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + port + "/api/books/" + createdBook.getId()))
                .header("Authorization", "Bearer " + token)
                .DELETE()
                .build();
        
        HttpResponse<String> deleteResponse = httpClient.send(deleteRequest, HttpResponse.BodyHandlers.ofString());
        assertEquals(HttpStatus.OK.value(), deleteResponse.statusCode());
    }

    @Test
    void userCannotCreateBooks() throws Exception {
        String token = getAuthToken("test1", "test1234");
        
        BookCreateRequestDTO createReq = new BookCreateRequestDTO();
        createReq.setTitle("No puc fer això");
        createReq.setAuthor("Usuari");
        createReq.setIsbn("9999999999999");
        createReq.setQuantity(1);
        createReq.setLanguage("ca");
        createReq.setYear(2023);
        createReq.setPages(100);
        createReq.setGenre("Ficció");
        createReq.setDescription("Descripció");

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + port + "/api/books"))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + token)
                .POST(HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(createReq)))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(HttpStatus.FORBIDDEN.value(), response.statusCode());
    }

    @Test
    void anyUserCanListBooks() throws Exception {
        String token = getAuthToken("test1", "test1234");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + port + "/api/books"))
                .header("Authorization", "Bearer " + token)
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(HttpStatus.OK.value(), response.statusCode());
        assertTrue(response.body().contains("content"));
    }
}
