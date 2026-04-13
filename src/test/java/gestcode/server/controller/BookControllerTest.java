package gestcode.server.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import gestcode.server.dto.request.BookCreateRequestDTO;
import gestcode.server.dto.request.BookUpdateRequestDTO;
import gestcode.server.dto.response.BookResponseDTO;
import gestcode.server.service.BookService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class BookControllerTest {

    private MockMvc mockMvc;

    @Mock
    private BookService bookService;

    @InjectMocks
    private BookController bookController;

    private ObjectMapper objectMapper = new ObjectMapper().registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule());

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(bookController)
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .build();
    }

    @Test
    void createBook_Success() throws Exception {
        BookCreateRequestDTO request = new BookCreateRequestDTO();
        request.setTitle("Llibre de Test");
        request.setAuthor("Autor Test");
        request.setIsbn("1234567890123");
        request.setQuantity(1);
        request.setLanguage("CA");
        request.setYear(2023);
        request.setPages(100);
        request.setGenre("Ficció");
        request.setDescription("Desc");

        BookResponseDTO response = new BookResponseDTO();
        response.setId(1L);
        response.setTitle("Llibre de Test");
        response.setAuthor("Autor Test");

        when(bookService.createBook(any(BookCreateRequestDTO.class))).thenReturn(response);

        mockMvc.perform(post("/api/books")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Llibre de Test"));
    }

    @Test
    void getBookById_Success() throws Exception {
        BookResponseDTO response = new BookResponseDTO();
        response.setId(1L);
        response.setTitle("Llibre 1");

        when(bookService.getBookById(eq(1L), any(), eq(false), any(Pageable.class))).thenReturn(response);

        mockMvc.perform(get("/api/books/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Llibre 1"));
    }

    @Test
    void getBooks_Success() throws Exception {
        BookResponseDTO response = new BookResponseDTO();
        response.setId(1L);
        response.setTitle("Llibre 1");

        Page<BookResponseDTO> page = new PageImpl<>(Collections.singletonList(response), org.springframework.data.domain.PageRequest.of(0, 10), 1);

        when(bookService.getBooks(any(), any(), any(), any(), any(), any(), any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/api/books")
                .param("page", "0")
                .param("size", "10"))
                .andDo(org.springframework.test.web.servlet.result.MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1));
    }

    @Test
    void updateBook_Success() throws Exception {
        BookUpdateRequestDTO request = new BookUpdateRequestDTO();
        request.setIsbn("1234567890123");
        request.setTitle("Llibre actualitzat");
        request.setAuthor("Autor");
        request.setYear(2023);
        request.setGenre("G");
        request.setPages(10);
        request.setLanguage("ca");
        request.setDescription("Desc");
        request.setQuantity(5);

        BookResponseDTO response = new BookResponseDTO();
        response.setId(1L);
        response.setTitle("Llibre actualitzat");

        when(bookService.updateBook(eq(1L), any(BookUpdateRequestDTO.class))).thenReturn(response);

        mockMvc.perform(put("/api/books/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Llibre actualitzat"));
    }

    @Test
    void deleteBook_Success() throws Exception {
        mockMvc.perform(delete("/api/books/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.message").value("Llibre esborrat correctament"));
    }
}
