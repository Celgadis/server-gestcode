package gestcode.server.dto.response;

import java.time.LocalDateTime;

/**
 * DTO (Data Transfer Object) de resposta del comentari de l'usuari sobre un
 * llibre.
 * Inclou les dades del comentari i informació resumida de l'usuari i del llibre
 * per evitar exposar les entitats JPA directament.
 *
 * @author Jordi Verdalet Carrera
 */
public class CommentResponseDTO {

    private Long id;
    private Long userId;
    private String username;
    private Long bookId;
    private String bookTitle;
    private String content;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    /**
     * Constructor per defecte.
     *
     * @author Jordi Verdalet Carrera
     */
    public CommentResponseDTO() {
    }

    /**
     * Constructor amb tots els camps.
     *
     * @param id        L'identificador del comentari.
     * @param userId    L'identificador de l'usuari.
     * @param username  El nom d'usuari.
     * @param bookId    L'identificador del llibre.
     * @param bookTitle El títol del llibre.
     * @param content   El comentari.
     * @param createdAt La data de creació.
     * @param updatedAt La data d'actualització.
     * @author Jordi Verdalet Carrera
     */
    public CommentResponseDTO(Long id, Long userId, String username, Long bookId, String bookTitle,
            String content, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.userId = userId;
        this.username = username;
        this.bookId = bookId;
        this.bookTitle = bookTitle;
        this.content = content;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    /**
     * Obté l'identificador del comentari.
     *
     * @return L'identificador.
     * @author Jordi Verdalet Carrera
     */
    public Long getId() {
        return id;
    }

    /**
     * Estableix l'identificador del comentari.
     *
     * @param id L'identificador.
     * @author Jordi Verdalet Carrera
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Obté l'identificador de l'usuari.
     *
     * @return L'identificador de l'usuari.
     * @author Jordi Verdalet Carrera
     */
    public Long getUserId() {
        return userId;
    }

    /**
     * Estableix l'identificador de l'usuari.
     *
     * @param userId L'identificador de l'usuari.
     * @author Jordi Verdalet Carrera
     */
    public void setUserId(Long userId) {
        this.userId = userId;
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
     * Obté l'identificador del llibre.
     *
     * @return L'identificador del llibre.
     * @author Jordi Verdalet Carrera
     */
    public Long getBookId() {
        return bookId;
    }

    /**
     * Estableix l'identificador del llibre.
     *
     * @param bookId L'identificador del llibre.
     * @author Jordi Verdalet Carrera
     */
    public void setBookId(Long bookId) {
        this.bookId = bookId;
    }

    /**
     * Obté el títol del llibre.
     *
     * @return El títol del llibre.
     * @author Jordi Verdalet Carrera
     */
    public String getBookTitle() {
        return bookTitle;
    }

    /**
     * Estableix el títol del llibre.
     *
     * @param bookTitle El títol del llibre.
     * @author Jordi Verdalet Carrera
     */
    public void setBookTitle(String bookTitle) {
        this.bookTitle = bookTitle;
    }

    /**
     * Obté el text del comentari.
     *
     * @return El text del comentari.
     * @author Jordi Verdalet Carrera
     */
    public String getContent() {
        return content;
    }

    /**
     * Estableix el text del comentari.
     *
     * @param content El text del comentari.
     * @author Jordi Verdalet Carrera
     */
    public void setContent(String content) {
        this.content = content;
    }

    /**
     * Obté la data de creació del comentari.
     *
     * @return La data de creació.
     * @author Jordi Verdalet Carrera
     */
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    /**
     * Estableix la data de creació del comentari.
     *
     * @param createdAt La data de creació.
     * @author Jordi Verdalet Carrera
     */
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    /**
     * Obté la data de l'última actualització del comentari.
     *
     * @return La data d'actualització.
     * @author Jordi Verdalet Carrera
     */
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    /**
     * Estableix la data de l'última actualització del comentari.
     *
     * @param updatedAt La data d'actualització.
     * @author Jordi Verdalet Carrera
     */
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
