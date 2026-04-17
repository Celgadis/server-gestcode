package gestcode.server.security;

import gestcode.server.model.entity.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

/**
 * Implementació personalitzada de UserDetails per adaptar l'entitat User
 * a Spring Security.
 *
 * @author Jordi Verdalet Carrera
 */
public class CustomUserDetails implements UserDetails {

    private final User user;

    /**
     * Constructor que encapsula l'entitat Usuari.
     *
     * @param user L'entitat User.
     * @author Jordi Verdalet Carrera
     */
    public CustomUserDetails(User user) {
        this.user = user;
    }

    /**
     * Obté l'entitat Usuari associada.
     *
     * @return L'usuari.
     * @author Jordi Verdalet Carrera
     */
    public User getUser() {
        return user;
    }

    /**
     * Obté els permisos (rols) concedits a l'usuari.
     *
     * @return Llista d'autoritats (per defecte el rol amb prefix ROLE_).
     * @author Jordi Verdalet Carrera
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()));
    }

    /**
     * Obté la contrasenya de l'usuari.
     *
     * @return La contrasenya.
     * @author Jordi Verdalet Carrera
     */
    @Override
    public String getPassword() {
        return user.getPassword();
    }

    /**
     * Obté el nom d'usuari.
     *
     * @return El nom d'usuari.
     * @author Jordi Verdalet Carrera
     */
    @Override
    public String getUsername() {
        return user.getUsername();
    }

    /**
     * Indica si el compte està expirat.
     *
     * @return Cert sempre (no gestionat actualment).
     * @author Jordi Verdalet Carrera
     */
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    /**
     * Indica si el compte està bloquejat.
     *
     * @return Cert sempre (no gestionat actualment).
     * @author Jordi Verdalet Carrera
     */
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    /**
     * Indica si les credencials han expirat.
     *
     * @return Cert sempre (no gestionat actualment).
     * @author Jordi Verdalet Carrera
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    /**
     * Indica si l'usuari està habilitat i actiu per iniciar sessió.
     *
     * @return Cert si està habilitat, fals altrament.
     * @author Jordi Verdalet Carrera
     */
    @Override
    public boolean isEnabled() {
        return user.isEnabled();
    }
}
