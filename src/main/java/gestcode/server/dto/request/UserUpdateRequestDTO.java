package gestcode.server.dto.request;

import gestcode.server.model.enums.Role;
import gestcode.server.model.enums.UserStatus;

/**
 * Objecte de Transferència de Dades (DTO) per a la petició d'actualització d'usuari.
 *
 * @author Jordi Verdalet Carrera
 */
public class UserUpdateRequestDTO {

    private String firstName;
    private String lastName1;
    private String lastName2;
    
    // Admins only (validated in service/controller)
    private UserStatus status;
    private Role role;
    private Boolean enabled;

    /**
     * Constructor buit per defecte.
     *
     * @author Jordi Verdalet Carrera
     */
    public UserUpdateRequestDTO() {}

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
     * Obté si l'usuari està habilitat.
     *
     * @return Verdader si està habilitat, fals altrament.
     * @author Jordi Verdalet Carrera
     */
    public Boolean getEnabled() {
        return enabled;
    }

    /**
     * Estableix si l'usuari està habilitat.
     *
     * @param enabled Estatus d'habilitació de l'usuari.
     * @author Jordi Verdalet Carrera
     */
    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }
}
