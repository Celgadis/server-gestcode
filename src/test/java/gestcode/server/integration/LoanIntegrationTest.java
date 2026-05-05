package gestcode.server.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import gestcode.server.dto.request.BookCreateRequestDTO;
import gestcode.server.dto.request.LoanRequestDTO;
import gestcode.server.dto.request.LoginRequestDTO;
import gestcode.server.dto.response.BookResponseDTO;
import gestcode.server.dto.response.JwtResponseDTO;
import gestcode.server.dto.response.LoanResponseDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.http.HttpStatus;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class LoanIntegrationTest {

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
        assertEquals(HttpStatus.OK.value(), response.statusCode(), "Error at login");

        JwtResponseDTO jwtResponse = objectMapper.readValue(response.body(), JwtResponseDTO.class);
        return jwtResponse.getToken();
    }

    private BookResponseDTO createTestBook(String adminToken) throws Exception {
        BookCreateRequestDTO createReq = new BookCreateRequestDTO();
        createReq.setTitle("Llibre per a Préstec Integració");
        createReq.setAuthor("Autor Préstec");
        createReq.setIsbn(UUID.randomUUID().toString().substring(0, 13));
        createReq.setQuantity(5);
        createReq.setLanguage("ca");
        createReq.setYear(2023);
        createReq.setPages(150);
        createReq.setGenre("Ficció");
        createReq.setDescription("Descripció de test de préstec");

        Map<String, String> formData = new LinkedHashMap<>();
        formData.put("isbn", createReq.getIsbn());
        formData.put("title", createReq.getTitle());
        formData.put("author", createReq.getAuthor());
        formData.put("year", createReq.getYear().toString());
        formData.put("genre", createReq.getGenre());
        formData.put("pages", createReq.getPages().toString());
        formData.put("language", createReq.getLanguage());
        formData.put("description", createReq.getDescription());
        formData.put("quantity", createReq.getQuantity().toString());

        String boundary = "---" + UUID.randomUUID().toString();
        StringBuilder body = new StringBuilder();
        for (Map.Entry<String, String> entry : formData.entrySet()) {
            body.append("--").append(boundary).append("\r\n");
            body.append("Content-Disposition: form-data; name=\"").append(entry.getKey()).append("\"\r\n\r\n");
            body.append(entry.getValue()).append("\r\n");
        }
        body.append("--").append(boundary).append("--\r\n");

        HttpRequest createRequest = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + port + "/api/books"))
                .header("Content-Type", "multipart/form-data; boundary=" + boundary)
                .header("Authorization", "Bearer " + adminToken)
                .POST(HttpRequest.BodyPublishers.ofByteArray(body.toString().getBytes(StandardCharsets.UTF_8)))
                .build();

        HttpResponse<String> response = httpClient.send(createRequest, HttpResponse.BodyHandlers.ofString());
        assertEquals(HttpStatus.CREATED.value(), response.statusCode(), "Error creating test book");

        return objectMapper.readValue(response.body(), BookResponseDTO.class);
    }

    @Test
    void userCanCreateAndReturnLoan() throws Exception {
        String adminToken = getAuthToken("admin", "admin1234");
        String userToken = getAuthToken("test1", "test1234");

        // 1. Admin crea un llibre
        BookResponseDTO book = createTestBook(adminToken);

        // 2. Usuari sol·licita un préstec
        LoanRequestDTO loanRequest = new LoanRequestDTO();
        loanRequest.setBookId(book.getId());

        HttpRequest createLoanReq = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + port + "/api/loans"))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + userToken)
                .POST(HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(loanRequest)))
                .build();

        HttpResponse<String> createLoanRes = httpClient.send(createLoanReq, HttpResponse.BodyHandlers.ofString());
        assertEquals(HttpStatus.CREATED.value(), createLoanRes.statusCode());

        LoanResponseDTO loan = objectMapper.readValue(createLoanRes.body(), LoanResponseDTO.class);
        assertNotNull(loan.getId());
        assertEquals("ACTIU", loan.getStatus().name());

        // 3. Usuari retorna el préstec
        HttpRequest returnLoanReq = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + port + "/api/loans/" + loan.getId() + "/return"))
                .header("Authorization", "Bearer " + userToken)
                .PUT(HttpRequest.BodyPublishers.noBody())
                .build();

        HttpResponse<String> returnLoanRes = httpClient.send(returnLoanReq, HttpResponse.BodyHandlers.ofString());
        assertEquals(HttpStatus.OK.value(), returnLoanRes.statusCode());

        LoanResponseDTO returnedLoan = objectMapper.readValue(returnLoanRes.body(), LoanResponseDTO.class);
        assertEquals("RETORNAT", returnedLoan.getStatus().name());
    }

    @Test
    void userCannotListAllLoans() throws Exception {
        String userToken = getAuthToken("test1", "test1234");

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + port + "/api/loans"))
                .header("Authorization", "Bearer " + userToken)
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(HttpStatus.FORBIDDEN.value(), response.statusCode());
    }

    @Test
    void adminCanListAllLoans() throws Exception {
        String adminToken = getAuthToken("admin", "admin1234");

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + port + "/api/loans"))
                .header("Authorization", "Bearer " + adminToken)
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(HttpStatus.OK.value(), response.statusCode());
        assertTrue(response.body().contains("content"));
    }

    @Test
    void userCanListTheirOwnLoans() throws Exception {
        String userToken = getAuthToken("test1", "test1234");

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + port + "/api/loans/my-loans"))
                .header("Authorization", "Bearer " + userToken)
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(HttpStatus.OK.value(), response.statusCode());
        assertTrue(response.body().contains("content"));
    }
}
