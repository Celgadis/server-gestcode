package gestcode.server.service;

import gestcode.server.dto.request.BookCreateRequestDTO;
import gestcode.server.dto.request.BookUpdateRequestDTO;
import gestcode.server.dto.response.BookResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

/**
 * Interfície del servei per a la gestió de llibres.
 *
 * @author Jordi Verdalet Carrera
 */
public interface BookService {

    /**
     * Crea un nou llibre.
     *
     * @param bookDTO Dades del llibre a crear.
     * @param cover   La imatge de portada del llibre (opcional).
     * @return El llibre creat com a DTO.
     */
    BookResponseDTO createBook(BookCreateRequestDTO bookDTO, MultipartFile cover);

    /**
     * Actualitza les dades d'un llibre existent.
     *
     * @param id L'identificador del llibre a actualitzar.
     * @param bookDTO Les noves dades.
     * @param cover   La imatge de portada del llibre (opcional).
     * @return El llibre actualitzat com a DTO.
     */
    BookResponseDTO updateBook(Long id, BookUpdateRequestDTO bookDTO, MultipartFile cover);

    /**
     * Elimina un llibre físicament de la base de dades.
     *
     * @param id L'identificador del llibre a eliminar.
     */
    void deleteBook(Long id);

    /**
     * Obté les dades d'un llibre pel seu identificador.
     * No inclou la puntuació personal de cap usuari (myRating = null).
     *
     * @param id L'identificador del llibre.
     * @return El llibre en format DTO.
     * @author Jordi Verdalet Carrera
     */
    BookResponseDTO getBookById(Long id);

    /**
     * Obté les dades d'un llibre pel seu identificador, incloent la puntuació
     * personal de l'usuari autenticat en el camp {@code myRating}.
     * Si l'usuari no ha puntuat el llibre o el username és null, myRating serà null.
     *
     * @param id       L'identificador del llibre.
     * @param username El nom d'usuari autenticat extret del token JWT, o null.
     * @return El llibre en format DTO amb myRating omplert si escau.
     * @author Jordi Verdalet Carrera
     */
    BookResponseDTO getBookById(Long id, String username);

    /**
     * Obté les dades d'un llibre pel seu identificador, amb la possibilitat
     * d'incloure els comentaris de forma paginada.
     *
     * @param id               L'identificador del llibre.
     * @param username         El nom d'usuari autenticat, o null.
     * @param includeComments  Si s'han d'incloure els comentaris en la resposta.
     * @param commentPageable  Dades de paginació per als comentaris.
     * @return El llibre en format DTO, opcionalment amb la pàgina de comentaris.
     * @author Jordi Verdalet Carrera
     */
    BookResponseDTO getBookById(Long id, String username, boolean includeComments, Pageable commentPageable);

    /**
     * Llista llibres de forma paginada amb filtres opcionals.
     *
     * @param title Filtre opcional pel títol.
     * @param author Filtre opcional per l'autor.
     * @param year Filtre opcional per l'any.
     * @param genre Filtre opcional pel gènere.
     * @param language Filtre opcional per l'idioma.
     * @param rating Filtre opcional per la valoració (rating mínim o exacte depenent de la implementació).
     * @param pageable Les dades de paginació i ordenació.
     * @return Els llibres filtrats i paginats.
     */
    Page<BookResponseDTO> getBooks(String title, String author, Integer year, String genre, String language, Double rating, Pageable pageable);
}
