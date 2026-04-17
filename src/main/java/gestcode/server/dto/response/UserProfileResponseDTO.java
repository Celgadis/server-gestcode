package gestcode.server.dto.response;

import gestcode.server.model.enums.Role;
import gestcode.server.model.enums.UserStatus;
import java.time.LocalDateTime;

/**
 * Objecte de Transferència de Dades (DTO) per retornar el perfil d'un usuari.
 *
 * @author Jordi Verdalet Carrera
 */
public class UserProfileResponseDTO {

    private Long id;
    private String username;
    private String firstName;
    private String lastName1;
    private String lastName2;
    private String email;
    private UserStatus status;
    private boolean enabled;
    private Role role;
    private LocalDateTime createdAt;

    /**
     * Constructor per defecte.
     *
     * @author Jordi Verdalet Carrera
     */
    public UserProfileResponseDTO() {}

    /**
     * Obté l'identificador de l'usuari.
     *
     * @return L'identificador.
     * @author Jordi Verdalet Carrera
     */
    public Long getId() {
        return id;
    }

    /**
     * Estableix l'identificador de l'usuari.
     *
     * @param id L'identificador.
     * @author Jordi Verdalet Carrera
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Obté el nom d'usuari.
     *
     * @return El nom d'usuari.
     * @author Jordi Verdalet Carrera
     */
    public String getUsername() {
        return username;
    }

    /**
     * Estableix el nom d'usuari.
     *
     * @param username El nom d'usuari.
     * @author Jordi Verdalet Carrera
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Obté el nom.
     *
     * @return El nom.
     * @author Jordi Verdalet Carrera
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * Estableix el nom.
     *
     * @param firstName El nom.
     * @author Jordi Verdalet Carrera
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     * Obté el primer cognom.
     *
     * @return El primer cognom.
     * @author Jordi Verdalet Carrera
     */
    public String getLastName1() {
        return lastName1;
    }

    /**
     * Estableix el primer cognom.
     *
     * @param lastName1 El primer cognom.
     * @author Jordi Verdalet Carrera
     */
    public void setLastName1(String lastName1) {
        this.lastName1 = lastName1;
    }

    /**
     * Obté el segon cognom.
     *
     * @return El segon cognom.
     * @author Jordi Verdalet Carrera
     */
    public String getLastName2() {
        return lastName2;
    }

    /**
     * Estableix el segon cognom.
     *
     * @param lastName2 El segon cognom.
     * @author Jordi Verdalet Carrera
     */
    public void setLastName2(String lastName2) {
        this.lastName2 = lastName2;
    }

    /**
     * Obté el correu electrònic.
     *
     * @return El correu electrònic.
     * @author Jordi Verdalet Carrera
     */
    public String getEmail() {
        return email;
    }

    /**
     * Estableix el correu electrònic.
     *
     * @param email El correu electrònic.
     * @author Jordi Verdalet Carrera
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Obté l'estat de l'usuari.
     *
     * @return L'estat de l'usuari.
     * @author Jordi Verdalet Carrera
     */
    public UserStatus getStatus() {
        return status;
    }

    /**
     * Estableix l'estat de l'usuari.
     *
     * @param status L'estat de l'usuari.
     * @author Jordi Verdalet Carrera
     */
    public void setStatus(UserStatus status) {
        this.status = status;
    }

    /**
     * Indica si l'usuari està habilitat.
     *
     * @return Verdader si està habilitat.
     * @author Jordi Verdalet Carrera
     */
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * Estableix si l'usuari està habilitat.
     *
     * @param enabled Estatus d'habilitació.
     * @author Jordi Verdalet Carrera
     */
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    /**
     * Obté el rol de l'usuari.
     *
     * @return El rol de l'usuari.
     * @author Jordi Verdalet Carrera
     */
    public Role getRole() {
        return role;
    }

    /**
     * Estableix el rol de l'usuari.
     *
     * @param role El rol de l'usuari.
     * @author Jordi Verdalet Carrera
     */
    public void setRole(Role role) {
        this.role = role;
    }

    /**
     * Obté la data de creació.
     *
     * @return La data de creació.
     * @author Jordi Verdalet Carrera
     */
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    /**
     * Estableix la data de creació.
     *
     * @param createdAt La data de creació.
     * @author Jordi Verdalet Carrera
     */
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
