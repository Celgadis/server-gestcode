package gestcode.server.dto.response;

/**
 * Objecte de Transferència de Dades (DTO) per retornar missatges d'èxit
 * amb l'identificador de l'entitat afectada.
 *
 * @author Jordi Verdalet Carrera
 */
public class MessageResponseDTO {

    private Long id;
    private String message;

    /**
     * Constructor per defecte.
     */
    public MessageResponseDTO() {
    }

    /**
     * Constructor amb paràmetres.
     *
     * @param id      L'identificador de l'entitat.
     * @param message El missatge de confirmació.
     */
    public MessageResponseDTO(Long id, String message) {
        this.id = id;
        this.message = message;
    }

    /**
     * Obté l'identificador.
     *
     * @return L'identificador.
     */
    public Long getId() {
        return id;
    }

    /**
     * Estableix l'identificador.
     *
     * @param id L'identificador.
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Obté el missatge.
     *
     * @return El missatge.
     */
    public String getMessage() {
        return message;
    }

    /**
     * Estableix el missatge.
     *
     * @param message El missatge.
     */
    public void setMessage(String message) {
        this.message = message;
    }
}
