package gestcode.server.dto.request;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

/**
 * DTO (Data Transfer Object) per crear o actualitzar la puntuació d'un usuari
 * sobre un llibre (operació upsert).
 * L'usuari que puntua s'extreu sempre del token JWT de la petició,
 * mai del cos de la petició, per garantir la seguretat.
 *
 * @author Jordi Verdalet Carrera
 */
public class RatingRequestDTO {

    @NotNull(message = "L'identificador del llibre és obligatori")
    private Long bookId;

    @NotNull(message = "La puntuació és obligatòria")
    @DecimalMin(value = "0.0", message = "La puntuació mínima és 0")
    @DecimalMax(value = "5.0", message = "La puntuació màxima és 5")
    private Double rating;

    /**
     * Constructor per defecte.
     *
     * @author Jordi Verdalet Carrera
     */
    public RatingRequestDTO() {
    }

    /**
     * Constructor amb tots els camps.
     *
     * @param bookId L'identificador del llibre a puntuar.
     * @param rating La puntuació (entre 0.0 i 5.0).
     * @author Jordi Verdalet Carrera
     */
    public RatingRequestDTO(Long bookId, Double rating) {
        this.bookId = bookId;
        this.rating = rating;
    }

    /**
     * Obté l'identificador del llibre a puntuar.
     *
     * @return L'identificador del llibre.
     * @author Jordi Verdalet Carrera
     */
    public Long getBookId() {
        return bookId;
    }

    /**
     * Estableix l'identificador del llibre a puntuar.
     *
     * @param bookId L'identificador del llibre.
     * @author Jordi Verdalet Carrera
     */
    public void setBookId(Long bookId) {
        this.bookId = bookId;
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
}
