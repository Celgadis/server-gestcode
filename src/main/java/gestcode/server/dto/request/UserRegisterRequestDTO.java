package gestcode.server.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Objecte de Transferència de Dades (DTO) per a la petició de registre d'usuari.
 *
 * @author Jordi Verdalet Carrera
 */
public class UserRegisterRequestDTO {

    @NotBlank(message = "El nom d'usuari no pot estar buit")
    @Size(min = 4, max = 50, message = "El nom d'usuari ha de tenir entre 4 i 50 caràcters")
    private String username;

    @NotBlank(message = "La contrasenya no pot estar buida")
    @Size(min = 6, max = 100, message = "La contrasenya ha de tenir almenys 6 caràcters")
    private String password;

    @NotBlank(message = "El nom no pot estar buit")
    private String firstName;

    @NotBlank(message = "El primer cognom no pot estar buit")
    private String lastName1;

    private String lastName2;

    @NotBlank(message = "El correu electrònic no pot estar buit")
    @Email(message = "Format de correu electrònic invàlid")
    private String email;

    /**
     * Constructor buit per defecte.
     *
     * @author Jordi Verdalet Carrera
     */
    public UserRegisterRequestDTO() {}

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
     * Obté la contrasenya.
     *
     * @return La contrasenya.
     * @author Jordi Verdalet Carrera
     */
    public String getPassword() {
        return password;
    }

    /**
     * Estableix la contrasenya.
     *
     * @param password La contrasenya.
     * @author Jordi Verdalet Carrera
     */
    public void setPassword(String password) {
        this.password = password;
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
}
