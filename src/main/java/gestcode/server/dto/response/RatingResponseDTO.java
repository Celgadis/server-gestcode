package gestcode.server.dto.response;

import java.time.LocalDateTime;

/**
 * DTO (Data Transfer Object) de resposta d'una puntuació d'usuari sobre un llibre.
 * Inclou les dades de la puntuació i informació resumida de l'usuari i del llibre
 * per evitar exposar les entitats JPA directament.
 *
 * @author Jordi Verdalet Carrera
 */
public class RatingResponseDTO {

    private Long id;
    private Long userId;
    private String username;
    private Long bookId;
    private String bookTitle;
    private Double rating;
    private boolean disabled;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    /**
     * Constructor per defecte.
     *
     * @author Jordi Verdalet Carrera
     */
    public RatingResponseDTO() {
    }

    /**
     * Constructor amb tots els camps.
     *
     * @param id        L'identificador de la puntuació.
     * @param userId    L'identificador de l'usuari.
     * @param username  El nom d'usuari.
     * @param bookId    L'identificador del llibre.
     * @param bookTitle El títol del llibre.
     * @param rating    El valor de la puntuació.
     * @param disabled  Si la puntuació està desactivada.
     * @param createdAt La data de creació.
     * @param updatedAt La data d'actualització.
     * @author Jordi Verdalet Carrera
     */
    public RatingResponseDTO(Long id, Long userId, String username, Long bookId, String bookTitle,
                             Double rating, boolean disabled, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.userId = userId;
        this.username = username;
        this.bookId = bookId;
        this.bookTitle = bookTitle;
        this.rating = rating;
        this.disabled = disabled;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    /**
     * Obté l'identificador de la puntuació.
     *
     * @return L'identificador.
     * @author Jordi Verdalet Carrera
     */
    public Long getId() {
        return id;
    }

    /**
     * Estableix l'identificador de la puntuació.
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
     * Obté el valor de la puntuació.
     *
     * @return La puntuació (0.0 - 5.0).
     * @author Jordi Verdalet Carrera
     */
    public Double getRating() {
        return rating;
    }

    /**
     * Estableix el valor de la puntuació.
     *
     * @param rating La puntuació (0.0 - 5.0).
     * @author Jordi Verdalet Carrera
     */
    public void setRating(Double rating) {
        this.rating = rating;
    }

    /**
     * Indica si la puntuació és desactivada.
     *
     * @return Cert si la puntuació és desactivada.
     * @author Jordi Verdalet Carrera
     */
    public boolean isDisabled() {
        return disabled;
    }

    /**
     * Estableix si la puntuació és desactivada.
     *
     * @param disabled Cert per desactivar.
     * @author Jordi Verdalet Carrera
     */
    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }

    /**
     * Obté la data de creació de la puntuació.
     *
     * @return La data de creació.
     * @author Jordi Verdalet Carrera
     */
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    /**
     * Estableix la data de creació de la puntuació.
     *
     * @param createdAt La data de creació.
     * @author Jordi Verdalet Carrera
     */
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    /**
     * Obté la data de l'última actualització de la puntuació.
     *
     * @return La data d'actualització.
     * @author Jordi Verdalet Carrera
     */
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    /**
     * Estableix la data de l'última actualització de la puntuació.
     *
     * @param updatedAt La data d'actualització.
     * @author Jordi Verdalet Carrera
     */
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
