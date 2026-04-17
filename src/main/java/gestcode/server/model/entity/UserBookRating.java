package gestcode.server.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.LocalDateTime;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

/**
 * Entitat JPA que representa la puntuació que un usuari dona a un llibre.
 * Un usuari només pot puntuar un mateix llibre una vegada (clau única composta
 * sobre user_id i book_id). El camp {@code disabled} permet als administradors
 * excloure una puntuació del càlcul de la mitjana sense eliminar-la.
 *
 * @author Jordi Verdalet Carrera
 */
@Entity
@Table(
    name = "user_book_ratings",
    uniqueConstraints = @UniqueConstraint(
        name = "uk_user_book_rating",
        columnNames = {"user_id", "book_id"}
    )
)
public class UserBookRating {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Usuari que ha realitzat la puntuació.
     * Fetch LAZY per evitar càrregues innecessàries.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /**
     * Llibre al qual s'ha assignat la puntuació.
     * Fetch LAZY per evitar càrregues innecessàries.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    /**
     * Puntuació donada per l'usuari al llibre. Valor entre 0.0 i 5.0.
     */
    @Column(nullable = false)
    private Double rating;

    /**
     * Indica si aquesta puntuació està desactivada i no es compta
     * en el càlcul de la mitjana del llibre. Només modificable per admins.
     */
    @Column(nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
    private boolean disabled = false;

    /**
     * Data i hora de creació de la puntuació. S'assigna automàticament.
     */
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * Data i hora de l'última actualització. S'actualitza automàticament.
     */
    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    /**
     * Constructor per defecte.
     *
     * @author Jordi Verdalet Carrera
     */
    public UserBookRating() {
    }

    /**
     * Constructor amb els camps principals.
     *
     * @param user   L'usuari que puntua.
     * @param book   El llibre puntuat.
     * @param rating La puntuació (0.0 - 5.0).
     * @author Jordi Verdalet Carrera
     */
    public UserBookRating(User user, Book book, Double rating) {
        this.user = user;
        this.book = book;
        this.rating = rating;
    }

    /**
     * Obté l'identificador únic de la puntuació.
     *
     * @return L'identificador
     * @author Jordi Verdalet Carrera
     */
    public Long getId() {
        return id;
    }

    /**
     * Estableix l'identificador únic de la puntuació.
     *
     * @param id L'identificador
     * @author Jordi Verdalet Carrera
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Obté l'usuari associat a la puntuació.
     *
     * @return L'entitat User
     * @author Jordi Verdalet Carrera
     */
    public User getUser() {
        return user;
    }

    /**
     * Estableix l'usuari associat a la puntuació.
     *
     * @param user L'entitat User
     * @author Jordi Verdalet Carrera
     */
    public void setUser(User user) {
        this.user = user;
    }

    /**
     * Obté el llibre associat a la puntuació.
     *
     * @return L'entitat Book
     * @author Jordi Verdalet Carrera
     */
    public Book getBook() {
        return book;
    }

    /**
     * Estableix el llibre associat a la puntuació.
     *
     * @param book L'entitat Book
     * @author Jordi Verdalet Carrera
     */
    public void setBook(Book book) {
        this.book = book;
    }

    /**
     * Obté el valor de la puntuació.
     *
     * @return La puntuació (0.0 - 5.0)
     * @author Jordi Verdalet Carrera
     */
    public Double getRating() {
        return rating;
    }

    /**
     * Estableix el valor de la puntuació.
     *
     * @param rating La puntuació (0.0 - 5.0)
     * @author Jordi Verdalet Carrera
     */
    public void setRating(Double rating) {
        this.rating = rating;
    }

    /**
     * Indica si la puntuació està desactivada.
     *
     * @return Cert si està desactivada
     * @author Jordi Verdalet Carrera
     */
    public boolean isDisabled() {
        return disabled;
    }

    /**
     * Estableix si la puntuació està desactivada.
     *
     * @param disabled Cert per desactivar, fals per activar
     * @author Jordi Verdalet Carrera
     */
    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }

    /**
     * Obté la data de creació de la puntuació.
     *
     * @return La data de creació
     * @author Jordi Verdalet Carrera
     */
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    /**
     * Estableix la data de creació de la puntuació.
     *
     * @param createdAt La data de creació
     * @author Jordi Verdalet Carrera
     */
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    /**
     * Obté la data de l'última actualització de la puntuació.
     *
     * @return La data d'actualització
     * @author Jordi Verdalet Carrera
     */
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    /**
     * Estableix la data de l'última actualització de la puntuació.
     *
     * @param updatedAt La data d'actualització
     * @author Jordi Verdalet Carrera
     */
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
