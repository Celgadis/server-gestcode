package gestcode.server.dto.request;

import jakarta.validation.constraints.NotNull;

/**
 * DTO (Data Transfer Object) per crear o actualitzar el comentari d'un usuari
 * sobre un llibre (operació upsert).
 * L'usuari que comenta s'extreu sempre del token JWT de la petició,
 * mai del cos de la petició, per garantir la seguretat.
 *
 * @author Jordi Verdalet Carrera
 */
public class CommentRequestDTO {

    @NotNull(message = "L'identificador del llibre és obligatori")
    private Long bookId;

    @NotNull(message = "El comentari és obligatori")
    private String content;

    /**
     * Constructor per defecte.
     *
     * @author Jordi Verdalet Carrera
     */
    public CommentRequestDTO() {
    }

    /**
     * Constructor amb tots els camps.
     *
     * @param bookId  L'identificador del llibre a puntuar.
     * @param content El comentari.
     * @author Jordi Verdalet Carrera
     */
    public CommentRequestDTO(Long bookId, String content) {
        this.bookId = bookId;
        this.content = content;
    }

    /**
     * Obté l'identificador del llibre a comentar.
     *
     * @return L'identificador del llibre.
     * @author Jordi Verdalet Carrera
     */
    public Long getBookId() {
        return bookId;
    }

    /**
     * Estableix l'identificador del llibre a comentar.
     *
     * @param bookId L'identificador del llibre.
     * @author Jordi Verdalet Carrera
     */
    public void setBookId(Long bookId) {
        this.bookId = bookId;
    }

    /**
     * Obté el text del comentari.
     *
     * @return El comentari.
     * @author Jordi Verdalet Carrera
     */
    public String getContent() {
        return content;
    }

    /**
     * Estableix el text del comentari.
     *
     * @param content El comentari.
     * @author Jordi Verdalet Carrera
     */
    public void setContent(String content) {
        this.content = content;
    }
}
