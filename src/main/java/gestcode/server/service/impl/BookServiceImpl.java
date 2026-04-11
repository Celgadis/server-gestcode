package gestcode.server.service.impl;

import gestcode.server.dto.request.BookCreateRequestDTO;
import gestcode.server.dto.request.BookUpdateRequestDTO;
import gestcode.server.dto.response.BookResponseDTO;
import gestcode.server.model.entity.Book;
import gestcode.server.repository.BookRepository;
import gestcode.server.service.BookService;
import jakarta.persistence.criteria.Predicate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

/**
 * Implementació del servei d'entitats Book.
 *
 * @author Jordi Verdalet Carrera
 */
@Service
public class BookServiceImpl implements BookService {

    private final BookRepository bookRepository;

    /**
     * Constructor que injecta el repositori de llibres.
     *
     * @param bookRepository Repositori de la base de dades.
     */
    @Autowired
    public BookServiceImpl(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
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
        return mapToDTO(savedBook);
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
    public BookResponseDTO updateBook(Long id, BookUpdateRequestDTO bookDTO) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Llibre no trobat"));

        if (!book.getIsbn().equals(bookDTO.getIsbn()) && bookRepository.existsByIsbn(bookDTO.getIsbn())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Aquest ISBN ja està utilitzat per un altre llibre");
        }

        book.setIsbn(bookDTO.getIsbn());
        book.setTitle(bookDTO.getTitle());
        book.setAuthor(bookDTO.getAuthor());
        book.setYear(bookDTO.getYear());
        book.setGenre(bookDTO.getGenre());
        book.setPages(bookDTO.getPages());
        book.setLanguage(bookDTO.getLanguage());
        book.setDescription(bookDTO.getDescription());
        book.setQuantity(bookDTO.getQuantity());
        // El rating no s'actualitza manualment amb l'edició, s'espera que sigui un camp
        // autogestionat pels comentaris que s'implementarà més endavant.

        Book updatedBook = bookRepository.save(book);
        return mapToDTO(updatedBook);
    }

    /**
     * Esborra un llibre de la base de dades.
     *
     * @param id L'identificador del llibre a eliminar.
     */
    @Override
    public void deleteBook(Long id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Llibre no trobat"));

        bookRepository.delete(book);
    }

    /**
     * Obté la informació d'un llibre específic cercat pel seu ID.
     *
     * @param id Identificador per buscar.
     * @return DTO amb tots els detalls del llibre.
     */
    @Override
    public BookResponseDTO getBookById(Long id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Llibre no trobat"));
        return mapToDTO(book);
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
        return booksPage.map(this::mapToDTO);
    }

    /**
     * Mètode privat d'ajuda per transformar l'entitat de base de dades cap al DTO
     * de resposta.
     *
     * @param book Entitat Book.
     * @return El pertinent BookResponseDTO preparat.
     */
    private BookResponseDTO mapToDTO(Book book) {
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
                book.getCreatedAt());
    }
}
