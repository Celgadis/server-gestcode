package gestcode.server.service.impl;

import gestcode.server.dto.request.UserRegisterRequestDTO;
import gestcode.server.dto.response.UserProfileResponseDTO;
import gestcode.server.model.entity.User;
import gestcode.server.model.enums.Role;
import gestcode.server.model.enums.UserStatus;
import gestcode.server.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    private UserRegisterRequestDTO registerRequest;

    @BeforeEach
    void setUp() {
        registerRequest = new UserRegisterRequestDTO();
        registerRequest.setUsername("testuser");
        registerRequest.setPassword("password123");
        registerRequest.setFirstName("Test");
        registerRequest.setLastName1("User");
        registerRequest.setEmail("test@example.com");
    }

    @Test
    void registerUser_Success() {
        when(userRepository.existsByUsername("testuser")).thenReturn(false);
        when(userRepository.existsByEmail("test@example.com")).thenReturn(false);

        User savedUser = new User();
        savedUser.setId(1L);
        savedUser.setUsername("testuser");
        savedUser.setEmail("test@example.com");
        savedUser.setFirstName("Test");
        savedUser.setLastName1("User");
        savedUser.setStatus(UserStatus.ACTIVE);
        savedUser.setEnabled(true);
        savedUser.setRole(Role.USER);

        when(passwordEncoder.encode(any())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        UserProfileResponseDTO response = userService.registerUser(registerRequest);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("testuser", response.getUsername());
        assertEquals(UserStatus.ACTIVE, response.getStatus());
        assertTrue(response.isEnabled());
        assertEquals(Role.USER, response.getRole());

        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void registerUser_UsernameExists() {
        when(userRepository.existsByUsername("testuser")).thenReturn(true);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, 
            () -> userService.registerUser(registerRequest));

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        assertTrue(exception.getReason().contains("El nom d'usuari ja existeix"));

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void registerUser_EmailExists() {
        when(userRepository.existsByUsername("testuser")).thenReturn(false);
        when(userRepository.existsByEmail("test@example.com")).thenReturn(true);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, 
            () -> userService.registerUser(registerRequest));

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        assertTrue(exception.getReason().contains("El correu electrònic ja existeix"));

        verify(userRepository, never()).save(any(User.class));
    }
}
