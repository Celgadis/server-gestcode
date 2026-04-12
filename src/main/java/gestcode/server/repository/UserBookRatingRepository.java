package gestcode.server.repository;

import gestcode.server.model.entity.UserBookRating;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repositori JPA per a l'entitat UserBookRating.
 * Inclou consultes optimitzades amb JOIN FETCH (i countQuery separada per
 * compatibilitat amb paginació) per evitar el problema N+1,
 * i una consulta agregada per calcular la mitjana de puntuació d'un llibre.
 *
 * @author Jordi Verdalet Carrera
 */
@Repository
public interface UserBookRatingRepository extends JpaRepository<UserBookRating, Long> {

    /**
     * Cerca una puntuació pel user_id i el book_id (per a l'operació upsert).
     *
     * @param userId L'identificador de l'usuari.
     * @param bookId L'identificador del llibre.
     * @return Un Optional que pot contenir la puntuació si existeix.
     * @author Jordi Verdalet Carrera
     */
    Optional<UserBookRating> findByUserIdAndBookId(Long userId, Long bookId);

    /**
     * Calcula la mitjana de les puntuacions actives (no desactivades) d'un llibre.
     * Si no hi ha puntuacions actives, retorna un Optional buit.
     *
     * @param bookId L'identificador del llibre.
     * @return Un Optional amb la mitjana, o buit si no hi ha puntuacions.
     * @author Jordi Verdalet Carrera
     */
    @Query("SELECT AVG(r.rating) FROM UserBookRating r WHERE r.book.id = :bookId AND r.disabled = false")
    Optional<Double> calculateAverageByBookId(@Param("bookId") Long bookId);

    /**
     * Llista totes les puntuacions, carregant user i book en una sola consulta
     * SQL (JOIN FETCH) per evitar el problema N+1. La countQuery separada és
     * necessària perquè Hibernate no pot paginar amb JOIN FETCH en la mateixa query.
     *
     * @param pageable Les dades de paginació.
     * @return Pàgina de totes les puntuacions.
     * @author Jordi Verdalet Carrera
     */
    @Query(value = "SELECT r FROM UserBookRating r JOIN FETCH r.user JOIN FETCH r.book",
           countQuery = "SELECT COUNT(r) FROM UserBookRating r")
    Page<UserBookRating> findAllWithDetails(Pageable pageable);

    /**
     * Llista les puntuacions d'un llibre concret, carregant user i book en
     * una sola consulta SQL (JOIN FETCH) per evitar el problema N+1.
     *
     * @param bookId   L'identificador del llibre.
     * @param pageable Les dades de paginació.
     * @return Pàgina de puntuacions del llibre.
     * @author Jordi Verdalet Carrera
     */
    @Query(value = "SELECT r FROM UserBookRating r JOIN FETCH r.user JOIN FETCH r.book WHERE r.book.id = :bookId",
           countQuery = "SELECT COUNT(r) FROM UserBookRating r WHERE r.book.id = :bookId")
    Page<UserBookRating> findByBookIdWithDetails(@Param("bookId") Long bookId, Pageable pageable);

    /**
     * Llista les puntuacions d'un usuari concret, carregant user i book en
     * una sola consulta SQL (JOIN FETCH) per evitar el problema N+1.
     *
     * @param userId   L'identificador de l'usuari.
     * @param pageable Les dades de paginació.
     * @return Pàgina de puntuacions de l'usuari.
     * @author Jordi Verdalet Carrera
     */
    @Query(value = "SELECT r FROM UserBookRating r JOIN FETCH r.user JOIN FETCH r.book WHERE r.user.id = :userId",
           countQuery = "SELECT COUNT(r) FROM UserBookRating r WHERE r.user.id = :userId")
    Page<UserBookRating> findByUserIdWithDetails(@Param("userId") Long userId, Pageable pageable);

    /**
     * Llista les puntuacions filtrades per usuari i llibre alhora, carregant
     * user i book en una sola consulta SQL (JOIN FETCH) per evitar el problema N+1.
     * S'utilitza quan es proporcionen ambdós filtres a {@code getAllRatings}.
     *
     * @param userId   L'identificador de l'usuari.
     * @param bookId   L'identificador del llibre.
     * @param pageable Les dades de paginació.
     * @return Pàgina de puntuacions filtrades per usuari i llibre.
     * @author Jordi Verdalet Carrera
     */
    @Query(value = "SELECT r FROM UserBookRating r JOIN FETCH r.user JOIN FETCH r.book "
                 + "WHERE r.user.id = :userId AND r.book.id = :bookId",
           countQuery = "SELECT COUNT(r) FROM UserBookRating r "
                      + "WHERE r.user.id = :userId AND r.book.id = :bookId")
    Page<UserBookRating> findByUserIdAndBookIdWithDetails(@Param("userId") Long userId,
                                                          @Param("bookId") Long bookId,
                                                          Pageable pageable);

    /**
     * Cerca la puntuació activa (no desactivada) d'un usuari per a un llibre concret.
     * S'utilitza per omplir el camp {@code myRating} de la fitxa del llibre.
     *
     * @param userId L'identificador de l'usuari.
     * @param bookId L'identificador del llibre.
     * @return Un Optional amb la puntuació activa, o buit si no existeix.
     * @author Jordi Verdalet Carrera
     */
    @Query("SELECT r FROM UserBookRating r WHERE r.user.id = :userId AND r.book.id = :bookId AND r.disabled = false")
    Optional<UserBookRating> findActiveByUserIdAndBookId(@Param("userId") Long userId, @Param("bookId") Long bookId);
}
