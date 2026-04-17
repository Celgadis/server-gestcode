package gestcode.server.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import gestcode.server.dto.request.RatingRequestDTO;
import gestcode.server.dto.response.RatingResponseDTO;
import gestcode.server.service.UserBookRatingService;
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
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class UserBookRatingControllerTest {

    private MockMvc mockMvc;

    @Mock
    private UserBookRatingService ratingService;

    @InjectMocks
    private UserBookRatingController ratingController;

    private ObjectMapper objectMapper = new ObjectMapper().registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule());

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(ratingController)
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .build();
    }

    @Test
    void upsertRating_Success() throws Exception {
        RatingRequestDTO request = new RatingRequestDTO();
        request.setBookId(1L);
        request.setRating(5.0);

        RatingResponseDTO response = new RatingResponseDTO();
        response.setId(1L);
        response.setRating(5.0);

        when(ratingService.upsertRating(any(RatingRequestDTO.class), eq("testuser"))).thenReturn(response);

        Authentication auth = new UsernamePasswordAuthenticationToken("testuser", "password");

        mockMvc.perform(post("/api/ratings")
                .principal(auth)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.rating").value(5.0));
    }

    @Test
    void getMyRatings_Success() throws Exception {
        RatingResponseDTO response = new RatingResponseDTO();
        response.setId(1L);

        Page<RatingResponseDTO> page = new PageImpl<>(Collections.singletonList(response), org.springframework.data.domain.PageRequest.of(0, 10), 1);

        when(ratingService.getMyRatings(eq("testuser"), any(Pageable.class))).thenReturn(page);

        Authentication auth = new UsernamePasswordAuthenticationToken("testuser", "password");

        mockMvc.perform(get("/api/ratings/my")
                .principal(auth))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1));
    }

    @Test
    void setRatingStatus_Success() throws Exception {
        RatingResponseDTO response = new RatingResponseDTO();
        response.setId(1L);
        response.setDisabled(true);

        when(ratingService.setRatingDisabled(eq(1L), anyBoolean())).thenReturn(response);

        mockMvc.perform(patch("/api/ratings/1/status")
                .param("disabled", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.disabled").value(true));
    }
}
