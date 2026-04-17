package gestcode.server.service.impl;

import gestcode.server.dto.request.RatingRequestDTO;
import gestcode.server.dto.response.RatingResponseDTO;
import gestcode.server.model.entity.Book;
import gestcode.server.model.entity.User;
import gestcode.server.model.entity.UserBookRating;
import gestcode.server.repository.BookRepository;
import gestcode.server.repository.UserBookRatingRepository;
import gestcode.server.repository.UserRepository;
import gestcode.server.service.RatingAverageObserver;
import gestcode.server.service.UserBookRatingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

/**
 * Implementació del servei de puntuacions d'usuaris sobre llibres.
 * Gestiona l'operació upsert (crear o actualitzar), l'activació/desactivació
 * de puntuacions i els llistats amb paginació. Tots els llistats utilitzen
 * consultes amb JOIN FETCH per evitar el problema N+1. Després de cada
 * modificació, crida l'observer per recalcular la mitjana del llibre afectat.
 *
 * @author Jordi Verdalet Carrera
 */
@Service
public class UserBookRatingServiceImpl implements UserBookRatingService {

    private final UserBookRatingRepository ratingRepository;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;
    private final RatingAverageObserver ratingAverageObserver;

    /**
     * Constructor que injecta els repositoris i l'observer necessaris.
     *
     * @param ratingRepository      Repositori de puntuacions.
     * @param bookRepository        Repositori de llibres.
     * @param userRepository        Repositori d'usuaris.
     * @param ratingAverageObserver Observer que recalcula la mitjana del llibre.
     * @author Jordi Verdalet Carrera
     */
    @Autowired
    public UserBookRatingServiceImpl(UserBookRatingRepository ratingRepository,
                                     BookRepository bookRepository,
                                     UserRepository userRepository,
                                     RatingAverageObserver ratingAverageObserver) {
        this.ratingRepository = ratingRepository;
        this.bookRepository = bookRepository;
        this.userRepository = userRepository;
        this.ratingAverageObserver = ratingAverageObserver;
    }

    /**
     * Crea o actualitza la puntuació de l'usuari autenticat per a un llibre concret.
     * Si ja existia una puntuació per a la combinació usuari-llibre, l'actualitza.
     * En cas contrari, en crea una de nova. Després notifica l'observer perquè
     * recalculi la mitjana del llibre.
     *
     * @param dto      Les dades de la puntuació (bookId i rating).
     * @param username El nom d'usuari autenticat extret del token JWT.
     * @return La puntuació creada o actualitzada en format DTO.
     * @author Jordi Verdalet Carrera
     */
    @Override
    @Transactional
    public RatingResponseDTO upsertRating(RatingRequestDTO dto, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuari no trobat"));

        Book book = bookRepository.findById(dto.getBookId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Llibre no trobat"));

        Optional<UserBookRating> existing = ratingRepository.findByUserIdAndBookId(user.getId(), book.getId());

        UserBookRating rating;
        if (existing.isPresent()) {
            // Actualitzar puntuació existent
            rating = existing.get();
            rating.setRating(dto.getRating());
        } else {
            // Crear nova puntuació
            rating = new UserBookRating(user, book, dto.getRating());
        }

        UserBookRating saved = ratingRepository.save(rating);

        // Notificar l'observer per recalcular la mitjana del llibre
        ratingAverageObserver.recalculateBookRating(book.getId());

        return mapToDTO(saved);
    }

    /**
     * Retorna totes les puntuacions de l'usuari autenticat, de forma paginada.
     * Utilitza JOIN FETCH per evitar el problema N+1.
     *
     * @param username El nom d'usuari autenticat extret del token JWT.
     * @param pageable Les dades de paginació.
     * @return Pàgina de puntuacions de l'usuari.
     * @author Jordi Verdalet Carrera
     */
    @Override
    public Page<RatingResponseDTO> getMyRatings(String username, Pageable pageable) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuari no trobat"));

        return ratingRepository.findByUserIdWithDetails(user.getId(), pageable)
                .map(this::mapToDTO);
    }

    /**
     * Retorna totes les puntuacions d'un llibre concret, de forma paginada.
     * Utilitza JOIN FETCH per evitar el problema N+1.
     *
     * @param bookId   L'identificador del llibre.
     * @param pageable Les dades de paginació.
     * @return Pàgina de puntuacions del llibre.
     * @author Jordi Verdalet Carrera
     */
    @Override
    public Page<RatingResponseDTO> getRatingsByBook(Long bookId, Pageable pageable) {
        if (!bookRepository.existsById(bookId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Llibre no trobat");
        }
        return ratingRepository.findByBookIdWithDetails(bookId, pageable)
                .map(this::mapToDTO);
    }

    /**
     * Retorna totes les puntuacions d'un usuari concret, de forma paginada.
     * Utilitza JOIN FETCH per evitar el problema N+1.
     *
     * @param userId   L'identificador de l'usuari.
     * @param pageable Les dades de paginació.
     * @return Pàgina de puntuacions de l'usuari.
     * @author Jordi Verdalet Carrera
     */
    @Override
    public Page<RatingResponseDTO> getRatingsByUser(Long userId, Pageable pageable) {
        if (!userRepository.existsById(userId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuari no trobat");
        }
        return ratingRepository.findByUserIdWithDetails(userId, pageable)
                .map(this::mapToDTO);
    }

    /**
     * Retorna totes les puntuacions amb filtres opcionals per userId i/o bookId.
     * Tots els casos utilitzen consultes amb JOIN FETCH per evitar el problema N+1:
     * cap filtre → {@code findAllWithDetails}, un sol filtre → query especialitzada,
     * ambdós filtres → {@code findByUserIdAndBookIdWithDetails}.
     *
     * @param userId   Filtre opcional per l'identificador de l'usuari.
     * @param bookId   Filtre opcional per l'identificador del llibre.
     * @param pageable Les dades de paginació.
     * @return Pàgina de puntuacions filtrades.
     * @author Jordi Verdalet Carrera
     */
    @Override
    public Page<RatingResponseDTO> getAllRatings(Long userId, Long bookId, Pageable pageable) {
        if (userId != null && bookId == null) {
            return getRatingsByUser(userId, pageable);
        }
        if (bookId != null && userId == null) {
            return getRatingsByBook(bookId, pageable);
        }
        if (userId != null) {
            // Ambdós filtres: query JOIN FETCH específica per evitar N+1
            return ratingRepository.findByUserIdAndBookIdWithDetails(userId, bookId, pageable)
                    .map(this::mapToDTO);
        }
        // Cap filtre: retornar totes amb JOIN FETCH
        return ratingRepository.findAllWithDetails(pageable)
                .map(this::mapToDTO);
    }

    /**
     * Activa o desactiva una puntuació i recalcula la mitjana del llibre afectat.
     *
     * @param ratingId L'identificador de la puntuació a modificar.
     * @param disabled Cert per desactivar, fals per activar.
     * @return La puntuació modificada en format DTO.
     * @author Jordi Verdalet Carrera
     */
    @Override
    @Transactional
    public RatingResponseDTO setRatingDisabled(Long ratingId, boolean disabled) {
        UserBookRating rating = ratingRepository.findById(ratingId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Puntuació no trobada"));

        rating.setDisabled(disabled);
        UserBookRating saved = ratingRepository.save(rating);

        // Recalcular la mitjana del llibre afectat
        ratingAverageObserver.recalculateBookRating(saved.getBook().getId());

        return mapToDTO(saved);
    }

    /**
     * Mètode privat d'ajuda per transformar l'entitat UserBookRating al DTO de resposta.
     *
     * @param rating Entitat UserBookRating.
     * @return El pertinent RatingResponseDTO preparat.
     * @author Jordi Verdalet Carrera
     */
    private RatingResponseDTO mapToDTO(UserBookRating rating) {
        return new RatingResponseDTO(
                rating.getId(),
                rating.getUser().getId(),
                rating.getUser().getUsername(),
                rating.getBook().getId(),
                rating.getBook().getTitle(),
                rating.getRating(),
                rating.isDisabled(),
                rating.getCreatedAt(),
                rating.getUpdatedAt()
        );
    }
}
