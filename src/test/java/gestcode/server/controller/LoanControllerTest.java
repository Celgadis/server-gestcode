package gestcode.server.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import gestcode.server.dto.request.LoanRequestDTO;
import gestcode.server.dto.response.LoanListResponseDTO;
import gestcode.server.dto.response.LoanResponseDTO;
import gestcode.server.model.entity.User;
import gestcode.server.model.enums.LoanStatus;
import gestcode.server.repository.UserRepository;
import gestcode.server.service.LoanService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class LoanControllerTest {

    private MockMvc mockMvc;

    @Mock
    private LoanService loanService;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private LoanController loanController;

    private ObjectMapper objectMapper = new ObjectMapper().registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule());
    private User mockUser;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(loanController)
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .build();

        mockUser = new User();
        mockUser.setId(1L);
        mockUser.setUsername("testuser");
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    private void mockCurrentUser() {
        // Mock SecurityContextHolder per als endpoints que requereixen l'usuari actual
        SecurityContext securityContext = mock(SecurityContext.class);
        Authentication authentication = mock(Authentication.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("testuser");
        SecurityContextHolder.setContext(securityContext);

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(mockUser));
    }

    @Test
    void createLoan_Success() throws Exception {
        mockCurrentUser();

        LoanRequestDTO request = new LoanRequestDTO();
        request.setBookId(1L);

        LoanResponseDTO response = new LoanResponseDTO(
                1L, 1L, "testuser", 1L, "Llibre Test",
                LocalDateTime.now(), LocalDateTime.now().plusDays(7), null, LoanStatus.ACTIU
        );

        when(loanService.createLoan(any(LoanRequestDTO.class), eq(mockUser))).thenReturn(response);

        mockMvc.perform(post("/api/loans")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.status").value("ACTIU"));
    }

    @Test
    void returnLoan_Success() throws Exception {
        mockCurrentUser();

        LoanResponseDTO response = new LoanResponseDTO(
                1L, 1L, "testuser", 1L, "Llibre Test",
                LocalDateTime.now(), LocalDateTime.now().plusDays(7), LocalDateTime.now(), LoanStatus.RETORNAT
        );

        when(loanService.returnLoan(eq(1L), eq(mockUser))).thenReturn(response);

        mockMvc.perform(put("/api/loans/1/return"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.status").value("RETORNAT"));
    }

    @Test
    void getAllLoans_Success() throws Exception {
        LoanListResponseDTO response = new LoanListResponseDTO();
        response.setContent(Collections.emptyList());
        response.setPageNo(0);
        response.setPageSize(10);
        response.setTotalElements(0L);
        response.setTotalPages(0);
        response.setLast(true);

        when(loanService.getAllLoans(any(), any(), any(), eq(0), eq(10), eq("loanDate"), eq("desc"))).thenReturn(response);

        mockMvc.perform(get("/api/loans")
                .param("pageNo", "0")
                .param("pageSize", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.pageNo").value(0));
    }

    @Test
    void getMyLoans_Success() throws Exception {
        mockCurrentUser();

        LoanListResponseDTO response = new LoanListResponseDTO();
        response.setContent(Collections.emptyList());

        when(loanService.getMyLoans(eq(mockUser), eq(0), eq(10), eq("loanDate"), eq("desc"))).thenReturn(response);

        mockMvc.perform(get("/api/loans/my-loans")
                .param("pageNo", "0")
                .param("pageSize", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }

    @Test
    void getMyOverdueLoans_Success() throws Exception {
        mockCurrentUser();

        LoanListResponseDTO response = new LoanListResponseDTO();
        response.setContent(Collections.emptyList());

        when(loanService.getMyLoansByStatuses(eq(mockUser), eq(List.of(LoanStatus.FORA_DE_LIMIT)), eq(0), eq(10), eq("loanDate"), eq("desc"))).thenReturn(response);

        mockMvc.perform(get("/api/loans/my-loans/overdue")
                .param("pageNo", "0")
                .param("pageSize", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }

    @Test
    void getMyNearDueLoans_Success() throws Exception {
        mockCurrentUser();

        LoanListResponseDTO response = new LoanListResponseDTO();
        response.setContent(Collections.emptyList());

        when(loanService.getMyLoansByStatuses(eq(mockUser), eq(List.of(LoanStatus.PROXIM_A_CADUCAR)), eq(0), eq(10), eq("loanDate"), eq("desc"))).thenReturn(response);

        mockMvc.perform(get("/api/loans/my-loans/near-due")
                .param("pageNo", "0")
                .param("pageSize", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }

    @Test
    void getAllOverdueLoans_Success() throws Exception {
        LoanListResponseDTO response = new LoanListResponseDTO();
        response.setContent(Collections.emptyList());

        when(loanService.getAllLoansByStatuses(eq(List.of(LoanStatus.FORA_DE_LIMIT)), eq(0), eq(10), eq("loanDate"), eq("desc"))).thenReturn(response);

        mockMvc.perform(get("/api/loans/all-overdue")
                .param("pageNo", "0")
                .param("pageSize", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }

    @Test
    void getAllNearDueLoans_Success() throws Exception {
        LoanListResponseDTO response = new LoanListResponseDTO();
        response.setContent(Collections.emptyList());

        when(loanService.getAllLoansByStatuses(eq(List.of(LoanStatus.PROXIM_A_CADUCAR)), eq(0), eq(10), eq("loanDate"), eq("desc"))).thenReturn(response);

        mockMvc.perform(get("/api/loans/all-near-due")
                .param("pageNo", "0")
                .param("pageSize", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }
}
