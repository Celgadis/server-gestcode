package gestcode.server.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;
import gestcode.server.dto.request.BookCreateRequestDTO;
import gestcode.server.dto.request.CommentRequestDTO;
import gestcode.server.dto.request.LoginRequestDTO;
import gestcode.server.dto.request.RatingRequestDTO;
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
public class SocialIntegrationTest {

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
        JwtResponseDTO jwtResponse = objectMapper.readValue(response.body(), JwtResponseDTO.class);
        return jwtResponse.getToken();
    }

    private Long createTestBook(String adminToken) throws Exception {
        BookCreateRequestDTO createReq = new BookCreateRequestDTO();
        createReq.setTitle("Llibre Social " + UUID.randomUUID().toString().substring(0, 5));
        createReq.setAuthor("Social Test");
        createReq.setIsbn(UUID.randomUUID().toString().substring(0, 13));
        createReq.setQuantity(1);
        createReq.setLanguage("ca");
        createReq.setYear(2023);
        createReq.setPages(100);
        createReq.setGenre("Ficció");
        createReq.setDescription("Descripció");

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + port + "/api/books"))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + adminToken)
                .POST(HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(createReq)))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        BookResponseDTO book = objectMapper.readValue(response.body(), BookResponseDTO.class);
        return book.getId();
    }

    private void deleteTestBook(Long bookId, String adminToken) throws Exception {
        HttpRequest deleteRequest = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + port + "/api/books/" + bookId))
                .header("Authorization", "Bearer " + adminToken)
                .DELETE()
                .build();
        httpClient.send(deleteRequest, HttpResponse.BodyHandlers.ofString());
    }

    @Test
    void userCanRateAndCommentBook() throws Exception {
        String adminToken = getAuthToken("admin", "admin1234");
        String userToken = getAuthToken("test1", "test1234");

        // 1. Crear llibre per provar
        Long bookId = createTestBook(adminToken);

        try {
            // 2. Afegir puntuació com a usuari (exemple: 5.0)
            RatingRequestDTO ratingReq = new RatingRequestDTO();
            ratingReq.setBookId(bookId);
            ratingReq.setRating(5.0);

            HttpRequest rateRequest = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:" + port + "/api/ratings"))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + userToken)
                    .POST(HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(ratingReq)))
                    .build();
            
            HttpResponse<String> rateResponse = httpClient.send(rateRequest, HttpResponse.BodyHandlers.ofString());
            assertEquals(HttpStatus.OK.value(), rateResponse.statusCode());

            // 3. Afegir comentari
            CommentRequestDTO commentReq = new CommentRequestDTO();
            commentReq.setBookId(bookId);
            commentReq.setContent("Llibre excel·lent, molt recomanable.");

            HttpRequest commentRequest = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:" + port + "/api/comments"))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + userToken)
                    .POST(HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(commentReq)))
                    .build();

            HttpResponse<String> commentResponse = httpClient.send(commentRequest, HttpResponse.BodyHandlers.ofString());
            assertEquals(HttpStatus.OK.value(), commentResponse.statusCode());

            // 4. Comprovar el llibre amb detalls i la puntuació mitjana
            HttpRequest getBookRequest = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:" + port + "/api/books/" + bookId + "?includeComments=true"))
                    .header("Authorization", "Bearer " + userToken)
                    .GET()
                    .build();

            HttpResponse<String> getBookResponse = httpClient.send(getBookRequest, HttpResponse.BodyHandlers.ofString());
            JsonNode bookDetails = objectMapper.readTree(getBookResponse.body());
            
            assertEquals(5.0, bookDetails.get("rating").asDouble());
            assertEquals(5.0, bookDetails.get("myRating").asDouble());
            assertNotNull(bookDetails.get("comments"));
            assertTrue(bookDetails.get("comments").get("totalElements").asInt() > 0);

        } finally {
            // 5. Neteja independentment del resultat
            deleteTestBook(bookId, adminToken);
        }
    }
}
