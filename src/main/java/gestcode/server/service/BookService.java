package gestcode.server.service;

import gestcode.server.dto.request.BookCreateRequestDTO;
import gestcode.server.dto.request.BookUpdateRequestDTO;
import gestcode.server.dto.response.BookResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

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
     * @return El llibre creat com a DTO.
     */
    BookResponseDTO createBook(BookCreateRequestDTO bookDTO);

    /**
     * Actualitza les dades d'un llibre existent.
     *
     * @param id L'identificador del llibre a actualitzar.
     * @param bookDTO Les noves dades.
     * @return El llibre actualitzat com a DTO.
     */
    BookResponseDTO updateBook(Long id, BookUpdateRequestDTO bookDTO);

    /**
     * Elimina un llibre físicament de la base de dades.
     *
     * @param id L'identificador del llibre a eliminar.
     */
    void deleteBook(Long id);

    /**
     * Obté les dades d'un llibre pel seu identificador.
     *
     * @param id L'identificador del llibre.
     * @return El llibre en format DTO.
     */
    BookResponseDTO getBookById(Long id);

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
