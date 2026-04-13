package gestcode.server.service.impl;

import gestcode.server.dto.request.BookCreateRequestDTO;
import gestcode.server.dto.request.BookUpdateRequestDTO;
import gestcode.server.dto.response.BookResponseDTO;
import gestcode.server.dto.response.CommentResponseDTO;
import gestcode.server.model.entity.Book;
import gestcode.server.model.entity.Comment;
import gestcode.server.model.entity.User;
import gestcode.server.model.entity.UserBookRating;
import gestcode.server.repository.BookRepository;
import gestcode.server.repository.CommentRepository;
import gestcode.server.repository.UserBookRatingRepository;
import gestcode.server.repository.UserRepository;
import gestcode.server.service.BookService;
import jakarta.persistence.criteria.Predicate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.request;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Implementació del servei d'entitats Book.
 *
 * @author Jordi Verdalet Carrera
 */
@Service
public class BookServiceImpl implements BookService {

    private final BookRepository bookRepository;
    private final UserBookRatingRepository ratingRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;

    /**
     * Constructor que injecta els repositoris necessaris.
     *
     * @param bookRepository   Repositori de la base de dades de llibres.
     * @param ratingRepository Repositori de puntuacions.
     * @param userRepository   Repositori d'usuaris.
     */
    @Autowired
    public BookServiceImpl(BookRepository bookRepository,
            UserBookRatingRepository ratingRepository,
            UserRepository userRepository,
            CommentRepository commentRepository) {
        this.bookRepository = bookRepository;
        this.ratingRepository = ratingRepository;
        this.userRepository = userRepository;
        this.commentRepository = commentRepository;
    }

    /**
     * Crea un llibre nou a la base de dades.
     * Si l'ISBN ja existeix, es llança una excepció 400. S'inicialitza el valor del
     * rating a 0.
     *
     * @param bookDTO Dades del llibre a crear.
     * @return El DTO del llibre creat guardat.
     */
    @Override
    @Transactional
    public BookResponseDTO createBook(BookCreateRequestDTO bookDTO) {
        if (bookRepository.existsByIsbn(bookDTO.getIsbn())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Aquest ISBN ja existeix");
        }

        // Crear llibre: passem els paràmetres, el rating l'iniciem a 0.0
        Book book = new Book(
                bookDTO.getIsbn(),
                bookDTO.getTitle(),
                bookDTO.getAuthor(),
                bookDTO.getYear(),
                bookDTO.getGenre(),
                bookDTO.getPages(),
                bookDTO.getLanguage(),
                bookDTO.getDescription(),
                bookDTO.getQuantity(),
                0.0);

        Book savedBook = bookRepository.save(book);
        return mapToDTO(savedBook, null);
    }

    /**
     * Actualitza totes les dades d'un llibre existent (excepte el rating).
     * Comprova que l'ISBN no estigui ja assignat a un altre llibre diferent.
     *
     * @param id      L'identificador de llibre.
     * @param bookDTO Les dades actualitzades per introduir.
     * @return El DTO resultant després d'aplicar la modificació.
     */
    @Override
    @Transactional
    public BookResponseDTO updateBook(Long id, BookUpdateRequestDTO bookDTO) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Llibre no trobat"));

        if (!book.getIsbn().equals(bookDTO.getIsbn()) && bookRepository.existsByIsbn(bookDTO.getIsbn())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Aquest ISBN ja està utilitzat per un altre llibre");
        }

        if (bookDTO.getIsbn() != null) {
            book.setIsbn(bookDTO.getIsbn());
        }

        if (bookDTO.getTitle() != null) {
            book.setTitle(bookDTO.getTitle());
        }

        if (bookDTO.getAuthor() != null) {
            book.setAuthor(bookDTO.getAuthor());
        }

        if (bookDTO.getYear() != null) {
            book.setYear(bookDTO.getYear());
        }

        if (bookDTO.getGenre() != null) {
            book.setGenre(bookDTO.getGenre());
        }

        if (bookDTO.getPages() != null) {
            book.setPages(bookDTO.getPages());
        }

        if (bookDTO.getLanguage() != null) {
            book.setLanguage(bookDTO.getLanguage());
        }

        if (bookDTO.getDescription() != null) {
            book.setDescription(bookDTO.getDescription());
        }

        if (bookDTO.getQuantity() != null) {
            book.setQuantity(bookDTO.getQuantity());
        }
        // El rating no s'actualitza manualment amb l'edició, s'actualitza a través
        // de les puntuacions que fan els usuaris.

        Book updatedBook = bookRepository.save(book);
        return mapToDTO(updatedBook, null);
    }

    /**
     * Esborra un llibre de la base de dades.
     *
     * @param id L'identificador del llibre a eliminar.
     */
    @Override
    @Transactional
    public void deleteBook(Long id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Llibre no trobat"));

        bookRepository.delete(book);
    }

    /**
     * Obté la informació d'un llibre específic cercat pel seu ID.
     * No inclou la puntuació personal de cap usuari (myRating = null).
     *
     * @param id Identificador per buscar.
     * @return DTO amb tots els detalls del llibre.
     * @author Jordi Verdalet Carrera
     */
    @Override
    @Transactional(readOnly = true)
    public BookResponseDTO getBookById(Long id) {
        return getBookById(id, null, false, null);
    }

    /**
     * Obté la informació d'un llibre específic cercat pel seu ID,
     * incloent la puntuació personal de l'usuari autenticat (myRating).
     * Si l'usuari no existeix o no ha puntuat el llibre, myRating serà null.
     *
     * @param id       Identificador del llibre per buscar.
     * @param username El nom d'usuari autenticat extret del token JWT.
     * @return DTO amb tots els detalls del llibre incloent myRating.
     * @author Jordi Verdalet Carrera
     */
    @Override
    @Transactional(readOnly = true)
    public BookResponseDTO getBookById(Long id, String username) {
        return getBookById(id, username, false, null);
    }

    /**
     * Obté la informació d'un llibre específic cercat pel seu ID,
     * amb la possibilitat d'incloure els comentaris de forma paginada
     * i la puntuació personal de l'usuari autenticat.
     *
     * @param id               Identificador del llibre per buscar.
     * @param username         El nom d'usuari autenticat, o null.
     * @param includeComments  Si s'han d'incloure els comentaris.
     * @param commentPageable  Dades de paginació per als comentaris (pot ser null si includeComments és false).
     * @return DTO amb els detalls del llibre, myRating i opcionalment els comentaris.
     * @author Jordi Verdalet Carrera
     */
    @Override
    @Transactional(readOnly = true)
    public BookResponseDTO getBookById(Long id, String username, boolean includeComments, Pageable commentPageable) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Llibre no trobat"));

        Double myRating = null;
        if (username != null) {
            Optional<User> userOpt = userRepository.findByUsername(username);
            if (userOpt.isPresent()) {
                myRating = ratingRepository
                        .findActiveByUserIdAndBookId(userOpt.get().getId(), book.getId())
                        .map(UserBookRating::getRating)
                        .orElse(null);
            }
        }

        BookResponseDTO dto = mapToDTO(book, myRating);

        if (includeComments && commentPageable != null) {
            Page<Comment> commentsPage = commentRepository.findByBookIdWithDetails(id, commentPageable);
            dto.setComments(commentsPage.map(this::mapCommentToDTO));
        }

        return dto;
    }

    /**
     * Llista llibres de forma paginada aplicant diferents filtres dinàmics
     * estructurats mitjançant Specification.
     *
     * @param title    Valor opcional per filtrar pel títol sencer o una part.
     * @param author   Valor opcional per filtrar pel nom de l'autor.
     * @param year     Valor opcional per filtrar per un any de publicació exacte.
     * @param genre    Valor opcional per filtrar per gènere.
     * @param language Valor opcional per l'idioma exacte (ex: 'cat', 'es').
     * @param rating   Valor opcional per filtrar per rating (igual o superior).
     * @param pageable Informació de pàgina, mida i ordre proporcionada en la
     *                 petició.
     * @return Entitat Page que conté els llibres paginats i mapejats a DTO.
     */
    @Override
    @Transactional(readOnly = true)
    public Page<BookResponseDTO> getBooks(String title, String author, Integer year, String genre, String language,
            Double rating, Pageable pageable) {
        Specification<Book> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (title != null && !title.isEmpty()) {
                predicates.add(cb.like(cb.lower(root.get("title")), "%" + title.toLowerCase() + "%"));
            }
            if (author != null && !author.isEmpty()) {
                predicates.add(cb.like(cb.lower(root.get("author")), "%" + author.toLowerCase() + "%"));
            }
            if (year != null) {
                predicates.add(cb.equal(root.get("year"), year));
            }
            if (genre != null && !genre.isEmpty()) {
                predicates.add(cb.like(cb.lower(root.get("genre")), "%" + genre.toLowerCase() + "%"));
            }
            if (language != null && !language.isEmpty()) {
                predicates.add(cb.equal(cb.lower(root.get("language")), language.toLowerCase()));
            }
            if (rating != null) {
                // Aquí podríem filtrar per rating superior o igual
                predicates.add(cb.greaterThanOrEqualTo(root.get("rating"), rating));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        Page<Book> booksPage = bookRepository.findAll(spec, pageable);
        return booksPage.map(b -> mapToDTO(b, null));
    }

    /**
     * Mètode privat d'ajuda per transformar l'entitat de base de dades cap al DTO
     * de resposta, incloent la puntuació personal de l'usuari si es proporciona.
     *
     * @param book     Entitat Book.
     * @param myRating La puntuació personal de l'usuari autenticat, o null.
     * @return El pertinent BookResponseDTO preparat.
     * @author Jordi Verdalet Carrera
     */
    private BookResponseDTO mapToDTO(Book book, Double myRating) {
        return new BookResponseDTO(
                book.getId(),
                book.getIsbn(),
                book.getTitle(),
                book.getAuthor(),
                book.getYear(),
                book.getGenre(),
                book.getPages(),
                book.getLanguage(),
                book.getDescription(),
                book.getQuantity(),
                book.getRating(),
                myRating,
                book.getCreatedAt());
    }

    /**
     * Mètode privat d'ajuda per transformar l'entitat Comment al DTO de
     * resposta.
     *
     * @param comment Entitat Comment.
     * @return El pertinent CommentResponseDTO preparat.
     * @author Jordi Verdalet Carrera
     */
    private CommentResponseDTO mapCommentToDTO(Comment comment) {
        return new CommentResponseDTO(
                comment.getId(),
                comment.getUser().getId(),
                comment.getUser().getUsername(),
                comment.getBook().getId(),
                comment.getBook().getTitle(),
                comment.getContent(),
                comment.getCreatedAt(),
                comment.getUpdatedAt());
    }
}
