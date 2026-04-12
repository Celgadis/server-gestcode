package gestcode.server.service;

import gestcode.server.dto.request.CommentRequestDTO;
import gestcode.server.dto.response.CommentResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Interfície del servei per a la gestió dels comentaris dels usuaris sobre
 * llibres.
 *
 * @author Jordi Verdalet Carrera
 */
public interface CommentService {

    /**
     * Crea un nou comentari o actualitza l'existent si l'usuari ja havia
     * comentat el mateix llibre (operació upsert). L'usuari s'identifica
     * pel seu nom d'usuari extret del token JWT.
     *
     * @param dto      Les dades del comentari (bookId i content).
     * @param username El nom d'usuari autenticat extret del token JWT.
     * @return El comentari creat o actualitzat en format DTO.
     * @author Jordi Verdalet Carrera
     */
    CommentResponseDTO upsertComment(CommentRequestDTO dto, String username);

    /**
     * Retorna els comentaris de l'usuari autenticat, de forma paginada.
     *
     * @param username El nom d'usuari autenticat extret del token JWT.
     * @param pageable Les dades de paginació.
     * @return Pàgina de comentaris de l'usuari.
     * @author Jordi Verdalet Carrera
     */
    Page<CommentResponseDTO> getMyComments(String username, Pageable pageable);

    /**
     * Retorna tots els comentaris d'un llibre concret. Accessible per
     * usuaris autenticats.
     *
     * @param bookId   L'identificador del llibre.
     * @param pageable Les dades de paginació.
     * @return Pàgina de comentaris del llibre.
     * @author Jordi Verdalet Carrera
     */
    Page<CommentResponseDTO> getCommentsByBook(Long bookId, Pageable pageable);

    /**
     * Retorna tots els comentaris d'un usuari concret. Accessible per
     * administradors.
     *
     * @param userId   L'identificador de l'usuari.
     * @param pageable Les dades de paginació.
     * @return Pàgina de comentaris de l'usuari.
     * @author Jordi Verdalet Carrera
     */
    Page<CommentResponseDTO> getCommentsByUser(Long userId, Pageable pageable);

    /**
     * Retorna tots els comentaris del sistema amb filtres opcionals per usuari
     * i/o llibre. Accessible per administradors.
     *
     * @param userId   Filtre opcional per l'identificador de l'usuari.
     * @param bookId   Filtre opcional per l'identificador del llibre.
     * @param pageable Les dades de paginació.
     * @return Pàgina de comentaris filtrades.
     * @author Jordi Verdalet Carrera
     */
    Page<CommentResponseDTO> getAllComments(Long userId, Long bookId, Pageable pageable);

    /**
     * Elimina un comentari físicament de la base de dades.
     *
     * @param id L'identificador del comentari a eliminar.
     */
    void deleteComment(Long id);
}
