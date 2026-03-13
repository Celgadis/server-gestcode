package gestcode.server.service;

import gestcode.server.dto.request.UserRegisterRequestDTO;
import gestcode.server.dto.request.UserUpdateRequestDTO;
import gestcode.server.dto.response.UserListResponseDTO;
import gestcode.server.dto.response.UserProfileResponseDTO;
import org.springframework.data.domain.Pageable;

public interface UserService {
    UserProfileResponseDTO registerUser(UserRegisterRequestDTO request);
    UserProfileResponseDTO getUserById(Long id);
    UserProfileResponseDTO updateUser(Long id, UserUpdateRequestDTO request);
    UserListResponseDTO listUsers(String keyword, Pageable pageable);
}
