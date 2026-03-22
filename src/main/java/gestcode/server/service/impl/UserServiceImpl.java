package gestcode.server.service.impl;

import gestcode.server.dto.request.UserRegisterRequestDTO;
import gestcode.server.dto.request.UserUpdateRequestDTO;
import gestcode.server.dto.response.UserListResponseDTO;
import gestcode.server.dto.response.UserProfileResponseDTO;
import gestcode.server.model.entity.User;
import gestcode.server.model.enums.Role;
import gestcode.server.model.enums.UserStatus;
import gestcode.server.repository.UserRepository;
import gestcode.server.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

import gestcode.server.security.CustomUserDetails;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public UserProfileResponseDTO registerUser(UserRegisterRequestDTO request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El nom d'usuari ja existeix");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El correu electrònic ja existeix");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setFirstName(request.getFirstName());
        user.setLastName1(request.getLastName1());
        user.setLastName2(request.getLastName2());
        user.setEmail(request.getEmail());

        user.setRole(Role.USER);
        // per defecte el usuari esta actiu i habilitat
        user.setStatus(UserStatus.ACTIVE);
        user.setEnabled(true);

        User savedUser = userRepository.save(user);
        return mapToProfileResponse(savedUser);
    }

    @Override
    @Transactional(readOnly = true)
    public UserProfileResponseDTO getUserById(Long id) {
        verifyUserAccess(id, "Només pots veure el teu propi perfil");
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuari no trobat"));
        return mapToProfileResponse(user);
    }

    @Override
    @Transactional(readOnly = true)
    public UserProfileResponseDTO getMe() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long userId = ((CustomUserDetails) authentication.getPrincipal()).getUser().getId();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuari no trobat"));
        return mapToProfileResponse(user);
    }

    @Override
    @Transactional
    public UserProfileResponseDTO updateUser(Long id, UserUpdateRequestDTO request) {
        verifyUserAccess(id, "Només pots editar el teu propi perfil");

        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuari no trobat"));
        //es van modificant els camps rebuts.
        if (request.getFirstName() != null)
            user.setFirstName(request.getFirstName());
        if (request.getLastName1() != null)
            user.setLastName1(request.getLastName1());
        if (request.getLastName2() != null)
            user.setLastName2(request.getLastName2());

        if (request.getStatus() != null || request.getRole() != null || request.getEnabled() != null) {
            if (!isCurrentUserAdmin()) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                        "Només els administradors poden canviar rols, l'estat o l'accés");
            }
            if (request.getStatus() != null)
                user.setStatus(request.getStatus());
            if (request.getRole() != null)
                user.setRole(request.getRole());
            if (request.getEnabled() != null)
                user.setEnabled(request.getEnabled());
        }

        User updatedUser = userRepository.save(user);
        return mapToProfileResponse(updatedUser);
    }

    @Override
    @Transactional(readOnly = true)
    public UserListResponseDTO listUsers(String keyword, Pageable pageable) {
        Specification<User> spec = (root, query, cb) -> cb.conjunction();

        if (keyword != null && !keyword.trim().isEmpty()) {
            String lowerKeyword = "%" + keyword.toLowerCase() + "%";
            spec = spec.and((root, query, cb) -> cb.or(
                    cb.like(cb.lower(root.get("username")), lowerKeyword),
                    cb.like(cb.lower(root.get("email")), lowerKeyword),
                    cb.like(cb.lower(root.get("firstName")), lowerKeyword),
                    cb.like(cb.lower(root.get("lastName1")), lowerKeyword)));
        }

        Page<User> userPage = userRepository.findAll(spec, pageable);

        List<UserProfileResponseDTO> content = userPage.getContent().stream()
                .map(this::mapToProfileResponse)
                .collect(Collectors.toList());

        UserListResponseDTO response = new UserListResponseDTO();
        response.setContent(content);
        response.setPageNo(userPage.getNumber());
        response.setPageSize(userPage.getSize());
        response.setTotalElements(userPage.getTotalElements());
        response.setTotalPages(userPage.getTotalPages());
        response.setLast(userPage.isLast());

        return response;
    }

    private UserProfileResponseDTO mapToProfileResponse(User user) {
        UserProfileResponseDTO dto = new UserProfileResponseDTO();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setFirstName(user.getFirstName());
        dto.setLastName1(user.getLastName1());
        dto.setLastName2(user.getLastName2());
        dto.setEmail(user.getEmail());
        dto.setStatus(user.getStatus());
        dto.setEnabled(user.isEnabled());
        dto.setRole(user.getRole());
        dto.setCreatedAt(user.getCreatedAt());
        return dto;
    }

    private boolean isCurrentUserAdmin() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null && authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
    }

    private void verifyUserAccess(Long targetUserId, String errorMessage) {
        if (isCurrentUserAdmin()) {
            return; // els admins poden accedir a qualsevol usuari
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long currentUserId = null;
        if (authentication != null && authentication.getPrincipal() instanceof CustomUserDetails) {
            currentUserId = ((CustomUserDetails) authentication.getPrincipal()).getUser().getId();
        }

        if (currentUserId == null || !currentUserId.equals(targetUserId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, errorMessage);
        }
    }
}
