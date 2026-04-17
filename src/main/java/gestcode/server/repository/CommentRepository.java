package gestcode.server.repository;

import gestcode.server.model.entity.Book;
import gestcode.server.model.entity.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repositori JPA per a l'entitat Comment.
 * Inclou consultes optimitzades amb JOIN FETCH (i countQuery separada per
 * compatibilitat amb paginació) per evitar el problema N+1,
 * i una consulta agregada per calcular la mitjana de puntuació d'un llibre.
 *
 * @author Jordi Verdalet Carrera
 */
@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

       /**
        * Cerca un comentari pel user_id i el book_id (per a l'operació upsert).
        *
        * @param userId L'identificador de l'usuari.
        * @param bookId L'identificador del llibre.
        * @return Un Optional que pot contenir el comentari si existeix.
        * @author Jordi Verdalet Carrera
        */
       Optional<Comment> findByUserIdAndBookId(Long userId, Long bookId);

       /**
        * Llista tots els comentaris, carregant user i book en una sola consulta
        * SQL (JOIN FETCH) per evitar el problema N+1. La countQuery separada és
        * necessària perquè Hibernate no pot paginar amb JOIN FETCH en la mateixa
        * query.
        *
        * @param pageable Les dades de paginació.
        * @return Pàgina de tots els comentaris.
        * @author Jordi Verdalet Carrera
        */
       @Query(value = "SELECT r FROM Comment r JOIN FETCH r.user JOIN FETCH r.book", countQuery = "SELECT COUNT(r) FROM Comment r")
       Page<Comment> findAllWithDetails(Pageable pageable);

       /**
        * Llista els comentaris d'un llibre concret, carregant user i book en
        * una sola consulta SQL (JOIN FETCH) per evitar el problema N+1.
        *
        * @param bookId   L'identificador del llibre.
        * @param pageable Les dades de paginació.
        * @return Pàgina de comentaris del llibre.
        * @author Jordi Verdalet Carrera
        */
       @Query(value = "SELECT r FROM Comment r JOIN FETCH r.user JOIN FETCH r.book WHERE r.book.id = :bookId", countQuery = "SELECT COUNT(r) FROM Comment r WHERE r.book.id = :bookId")
       Page<Comment> findByBookIdWithDetails(@Param("bookId") Long bookId, Pageable pageable);

       /**
        * Llista els comentaris d'un usuari concret, carregant user i book en
        * una sola consulta SQL (JOIN FETCH) per evitar el problema N+1.
        *
        * @param userId   L'identificador de l'usuari.
        * @param pageable Les dades de paginació.
        * @return Pàgina de comentaris de l'usuari.
        * @author Jordi Verdalet Carrera
        */
       @Query(value = "SELECT r FROM Comment r JOIN FETCH r.user JOIN FETCH r.book WHERE r.user.id = :userId", countQuery = "SELECT COUNT(r) FROM Comment r WHERE r.user.id = :userId")
       Page<Comment> findByUserIdWithDetails(@Param("userId") Long userId, Pageable pageable);

       /**
        * Llista els comentaris filtrats per usuari i llibre alhora, carregant
        * user i book en una sola consulta SQL (JOIN FETCH) per evitar el problema N+1.
        * S'utilitza quan es proporcionen ambdós filtres a {@code getAllRatings}.
        *
        * @param userId   L'identificador de l'usuari.
        * @param bookId   L'identificador del llibre.
        * @param pageable Les dades de paginació.
        * @return Pàgina de comentaris filtrades per usuari i llibre.
        * @author Jordi Verdalet Carrera
        */
       @Query(value = "SELECT r FROM Comment r JOIN FETCH r.user JOIN FETCH r.book "
                     + "WHERE r.user.id = :userId AND r.book.id = :bookId", countQuery = "SELECT COUNT(r) FROM Comment r "
                                   + "WHERE r.user.id = :userId AND r.book.id = :bookId")
       Page<Comment> findByUserIdAndBookIdWithDetails(@Param("userId") Long userId,
                     @Param("bookId") Long bookId,
                     Pageable pageable);

}
