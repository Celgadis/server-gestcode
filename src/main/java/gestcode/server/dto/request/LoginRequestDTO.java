package gestcode.server.dto.request;

import jakarta.validation.constraints.NotBlank;

/**
 * Objecte de Transferència de Dades (DTO) per a la petició d'inici de sessió.
 *
 * @author Jordi Verdalet Carrera
 */
public class LoginRequestDTO {

    @NotBlank(message = "El nom d'usuari o correu electrònic és obligatori")
    private String usernameOrEmail;

    @NotBlank(message = "La contrasenya és obligatòria")
    private String password;

    /**
     * Constructor buit per defecte.
     *
     * @author Jordi Verdalet Carrera
     */
    public LoginRequestDTO() {
    }

    /**
     * Constructor amb paràmetres.
     *
     * @param usernameOrEmail Nom d'usuari o correu electrònic.
     * @param password Contrasenya.
     * @author Jordi Verdalet Carrera
     */
    public LoginRequestDTO(String usernameOrEmail, String password) {
        this.usernameOrEmail = usernameOrEmail;
        this.password = password;
    }

    /**
     * Obté el nom d'usuari o correu electrònic.
     *
     * @return El nom d'usuari o correu electrònic.
     * @author Jordi Verdalet Carrera
     */
    public String getUsernameOrEmail() {
        return usernameOrEmail;
    }

    /**
     * Estableix el nom d'usuari o correu electrònic.
     *
     * @param usernameOrEmail Nom d'usuari o correu electrònic.
     * @author Jordi Verdalet Carrera
     */
    public void setUsernameOrEmail(String usernameOrEmail) {
        this.usernameOrEmail = usernameOrEmail;
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
}
