package gestcode.server.repository;

import gestcode.server.model.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repositori JPA per a l'entitat Book.
 * Permet realitzar operacions CRUD i de filtratge (amb JpaSpecificationExecutor).
 *
 * @author Jordi Verdalet Carrera
 */
@Repository
public interface BookRepository extends JpaRepository<Book, Long>, JpaSpecificationExecutor<Book> {
    
    /**
     * Cerca un llibre pel seu ISBN.
     *
     * @param isbn L'ISBN del llibre a buscar.
     * @return Un Optional que pot contenir el llibre si existeix.
     */
    Optional<Book> findByIsbn(String isbn);
    
    /**
     * Comprova si existeix un llibre amb l'ISBN donat.
     *
     * @param isbn L'ISBN a comprovar.
     * @return Cert si existeix, fals en cas contrari.
     */
    boolean existsByIsbn(String isbn);
}
