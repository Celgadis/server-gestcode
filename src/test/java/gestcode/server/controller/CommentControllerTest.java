package gestcode.server.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import gestcode.server.dto.request.CommentRequestDTO;
import gestcode.server.dto.response.CommentResponseDTO;
import gestcode.server.service.CommentService;
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
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class CommentControllerTest {

    private MockMvc mockMvc;

    @Mock
    private CommentService commentService;

    @InjectMocks
    private CommentController commentController;

    private ObjectMapper objectMapper = new ObjectMapper().registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule());

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(commentController)
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .build();
    }

    @Test
    void upsertComment_Success() throws Exception {
        CommentRequestDTO request = new CommentRequestDTO();
        request.setBookId(1L);
        request.setContent("M'ha agradat molt");

        CommentResponseDTO response = new CommentResponseDTO();
        response.setId(1L);
        response.setBookId(1L);
        response.setContent("M'ha agradat molt");

        when(commentService.upsertComment(any(CommentRequestDTO.class), eq("testuser"))).thenReturn(response);

        Authentication auth = new UsernamePasswordAuthenticationToken("testuser", "password");

        mockMvc.perform(post("/api/comments")
                .principal(auth)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.content").value("M'ha agradat molt"));
    }

    @Test
    void getMyComments_Success() throws Exception {
        CommentResponseDTO response = new CommentResponseDTO();
        response.setId(1L);
        response.setContent("El meu comentari");

        Page<CommentResponseDTO> page = new PageImpl<>(Collections.singletonList(response), org.springframework.data.domain.PageRequest.of(0, 10), 1);

        when(commentService.getMyComments(eq("testuser"), any(Pageable.class))).thenReturn(page);

        Authentication auth = new UsernamePasswordAuthenticationToken("testuser", "password");

        mockMvc.perform(get("/api/comments/my")
                .principal(auth))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1));
    }

    @Test
    void getAllComments_Success() throws Exception {
        CommentResponseDTO response = new CommentResponseDTO();
        response.setId(1L);

        Page<CommentResponseDTO> page = new PageImpl<>(Collections.singletonList(response), org.springframework.data.domain.PageRequest.of(0, 10), 1);

        when(commentService.getAllComments(any(), any(), any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/api/comments"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1));
    }

    @Test
    void getCommentsByBook_Success() throws Exception {
        CommentResponseDTO response = new CommentResponseDTO();
        response.setId(1L);

        Page<CommentResponseDTO> page = new PageImpl<>(Collections.singletonList(response), org.springframework.data.domain.PageRequest.of(0, 10), 1);

        when(commentService.getCommentsByBook(eq(1L), any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/api/comments/book/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1));
    }
}
