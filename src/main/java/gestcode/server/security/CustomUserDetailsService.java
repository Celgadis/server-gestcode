package gestcode.server.security;

import gestcode.server.model.entity.User;
import gestcode.server.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Servei personalitzat per carregar l'usuari durant el procés d'autenticació
 * de Spring Security.
 *
 * @author Jordi Verdalet Carrera
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    /**
     * Constructor del servei.
     *
     * @param userRepository El repositori d'usuaris.
     * @author Jordi Verdalet Carrera
     */
    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Carrega l'usuari per nom d'usuari o correu electrònic.
     *
     * @param usernameOrEmail El nom d'usuari o email usat per iniciar sessió.
     * @return L'usuari adaptat a UserDetails.
     * @throws UsernameNotFoundException Si l'usuari no és trobat.
     * @author Jordi Verdalet Carrera
     */
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
