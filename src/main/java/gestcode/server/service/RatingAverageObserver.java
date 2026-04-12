package gestcode.server.service;

import gestcode.server.model.entity.Book;
import gestcode.server.repository.BookRepository;
import gestcode.server.repository.UserBookRatingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

/**
 * Component Observer que recalcula la mitjana de puntuació d'un llibre
 * sempre que es crea, actualitza o canvia l'estat d'una puntuació.
 * Només es comptabilitzen les puntuacions actives (disabled = false).
 * Si no hi ha cap puntuació activa, el rating del llibre es posa a 0.0.
 *
 * @author Jordi Verdalet Carrera
 */
@Component
public class RatingAverageObserver {

    private final UserBookRatingRepository ratingRepository;
    private final BookRepository bookRepository;

    /**
     * Constructor que injecta els repositoris necessaris.
     *
     * @param ratingRepository Repositori de puntuacions.
     * @param bookRepository   Repositori de llibres.
     * @author Jordi Verdalet Carrera
     */
    @Autowired
    public RatingAverageObserver(UserBookRatingRepository ratingRepository, BookRepository bookRepository) {
        this.ratingRepository = ratingRepository;
        this.bookRepository = bookRepository;
    }

    /**
     * Recalcula i guarda la nova mitjana de puntuació del llibre amb l'id indicat.
     * Si no hi ha puntuacions actives per al llibre, el rating es posa a 0.0.
     *
     * @param bookId L'identificador del llibre al qual s'ha de recalcular la mitjana.
     * @author Jordi Verdalet Carrera
     */
    public void recalculateBookRating(Long bookId) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Llibre no trobat"));

        double newAverage = ratingRepository.calculateAverageByBookId(bookId).orElse(0.0);

        // Arrodonir a 2 decimals per evitar valors com 4.333333...
        double rounded = Math.round(newAverage * 100.0) / 100.0;

        book.setRating(rounded);
        bookRepository.save(book);
    }
}
