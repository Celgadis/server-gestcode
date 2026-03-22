package gestcode.server.security;

import gestcode.server.model.entity.User;
import gestcode.server.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String usernameOrEmail) throws UsernameNotFoundException {
        // Find user by either username or email
        // We'll need to update UserRepository to have findByUsernameOrEmail
        // For now, let's try findByUsername then findByEmail
        User user = null;
        if (usernameOrEmail.contains("@")) {
            user = userRepository.findByEmail(usernameOrEmail)
                    .orElseThrow(() -> new UsernameNotFoundException("Usuari no trobat amb el correu electrònic: " + usernameOrEmail));
        } else {
            user = userRepository.findByUsername(usernameOrEmail)
                    .orElseThrow(() -> new UsernameNotFoundException("Usuari no trobat amb el nom d'usuari: " + usernameOrEmail));
        }

        return new CustomUserDetails(user);
    }
}
