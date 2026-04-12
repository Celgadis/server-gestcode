package gestcode.server.service;

import gestcode.server.dto.request.RatingRequestDTO;
import gestcode.server.dto.response.RatingResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Interfície del servei per a la gestió de puntuacions d'usuaris sobre llibres.
 *
 * @author Jordi Verdalet Carrera
 */
public interface UserBookRatingService {

    /**
     * Crea una nova puntuació o actualitza l'existent si l'usuari ja havia
     * puntuat el mateix llibre (operació upsert). L'usuari s'identifica
     * pel seu nom d'usuari extret del token JWT.
     *
     * @param dto      Les dades de la puntuació (bookId i rating).
     * @param username El nom d'usuari autenticat extret del token JWT.
     * @return La puntuació creada o actualitzada en format DTO.
     * @author Jordi Verdalet Carrera
     */
    RatingResponseDTO upsertRating(RatingRequestDTO dto, String username);

    /**
     * Retorna totes les puntuacions de l'usuari autenticat, de forma paginada.
     *
     * @param username El nom d'usuari autenticat extret del token JWT.
     * @param pageable Les dades de paginació.
     * @return Pàgina de puntuacions de l'usuari.
     * @author Jordi Verdalet Carrera
     */
    Page<RatingResponseDTO> getMyRatings(String username, Pageable pageable);

    /**
     * Retorna totes les puntuacions d'un llibre concret. Accessible per administradors.
     *
     * @param bookId   L'identificador del llibre.
     * @param pageable Les dades de paginació.
     * @return Pàgina de puntuacions del llibre.
     * @author Jordi Verdalet Carrera
     */
    Page<RatingResponseDTO> getRatingsByBook(Long bookId, Pageable pageable);

    /**
     * Retorna totes les puntuacions d'un usuari concret. Accessible per administradors.
     *
     * @param userId   L'identificador de l'usuari.
     * @param pageable Les dades de paginació.
     * @return Pàgina de puntuacions de l'usuari.
     * @author Jordi Verdalet Carrera
     */
    Page<RatingResponseDTO> getRatingsByUser(Long userId, Pageable pageable);

    /**
     * Retorna totes les puntuacions del sistema amb filtres opcionals per usuari
     * i/o llibre. Accessible per administradors.
     *
     * @param userId   Filtre opcional per l'identificador de l'usuari.
     * @param bookId   Filtre opcional per l'identificador del llibre.
     * @param pageable Les dades de paginació.
     * @return Pàgina de puntuacions filtrades.
     * @author Jordi Verdalet Carrera
     */
    Page<RatingResponseDTO> getAllRatings(Long userId, Long bookId, Pageable pageable);

    /**
     * Activa o desactiva una puntuació. En canviar l'estat, es recalcula
     * automàticament la mitjana del llibre afectat. Accessible per administradors.
     *
     * @param ratingId L'identificador de la puntuació a modificar.
     * @param disabled Cert per desactivar, fals per activar.
     * @return La puntuació modificada en format DTO.
     * @author Jordi Verdalet Carrera
     */
    RatingResponseDTO setRatingDisabled(Long ratingId, boolean disabled);
}
