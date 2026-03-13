package gestcode.server.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import gestcode.server.dto.request.UserUpdateRequestDTO;
import gestcode.server.dto.response.UserListResponseDTO;
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
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class UserControllerTest {

    private MockMvc mockMvc;

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    private ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
    }

    @Test
    void getUserById_Success() throws Exception {
        UserProfileResponseDTO profile = new UserProfileResponseDTO();
        profile.setId(1L);
        profile.setUsername("testuser");
        profile.setFirstName("Test");
        profile.setRole(Role.USER);

        when(userService.getUserById(1L)).thenReturn(profile);

        mockMvc.perform(get("/api/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.username").value("testuser"))
                .andExpect(jsonPath("$.firstName").value("Test"))
                .andExpect(jsonPath("$.role").value("USER"));
    }

    @Test
    void listUsers_Success() throws Exception {
        UserProfileResponseDTO profile = new UserProfileResponseDTO();
        profile.setId(1L);
        profile.setUsername("testuser");

        UserListResponseDTO listResponse = new UserListResponseDTO();
        listResponse.setContent(List.of(profile));
        listResponse.setTotalElements(1);
        listResponse.setTotalPages(1);

        when(userService.listUsers(any(), any(Pageable.class))).thenReturn(listResponse);

        mockMvc.perform(get("/api/users")
                .param("page", "0")
                .param("size", "10")
                .param("keyword", "test"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.content[0].username").value("testuser"));
    }

    @Test
    void updateUser_Success() throws Exception {
        UserUpdateRequestDTO request = new UserUpdateRequestDTO();
        request.setFirstName("UpdatedName");

        UserProfileResponseDTO profile = new UserProfileResponseDTO();
        profile.setId(1L);
        profile.setFirstName("UpdatedName");
        profile.setStatus(UserStatus.ACTIVE);

        when(userService.updateUser(eq(1L), any(UserUpdateRequestDTO.class))).thenReturn(profile);

        mockMvc.perform(put("/api/users/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("UpdatedName"))
                .andExpect(jsonPath("$.status").value("ACTIVE"));
    }
}
