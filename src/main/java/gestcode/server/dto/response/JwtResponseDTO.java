package gestcode.server.dto.response;

/**
 * Objecte de Transferència de Dades (DTO) per a la resposta de login amb JWT.
 *
 * @author Jordi Verdalet Carrera
 */
public class JwtResponseDTO {

    private String token;
    private String type = "Bearer";

    /**
     * Constructor per defecte.
     *
     * @author Jordi Verdalet Carrera
     */
    public JwtResponseDTO() {
    }

    /**
     * Constructor del DTO.
     *
     * @param token Token JWT generat.
     * @author Jordi Verdalet Carrera
     */
    public JwtResponseDTO(String token) {
        this.token = token;
    }

    /**
     * Obté el token JWT.
     *
     * @return El token JWT.
     * @author Jordi Verdalet Carrera
     */
    public String getToken() {
        return token;
    }

    /**
     * Estableix el token JWT.
     *
     * @param token El token JWT.
     * @author Jordi Verdalet Carrera
     */
    public void setToken(String token) {
        this.token = token;
    }

    /**
     * Obté el tipus de token.
     *
     * @return El tipus de token (per defecte "Bearer").
     * @author Jordi Verdalet Carrera
     */
    public String getType() {
        return type;
    }

    /**
     * Estableix el tipus de token.
     *
     * @param type El tipus de token.
     * @author Jordi Verdalet Carrera
     */
    public void setType(String type) {
        this.type = type;
    }
}
