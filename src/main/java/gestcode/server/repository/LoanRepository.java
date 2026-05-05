package gestcode.server.repository;

import gestcode.server.model.entity.Loan;
import gestcode.server.model.enums.LoanStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repositori per accedir a les dades de préstecs a la base de dades.
 * 
 * @author Jordi Verdalet Carrera
 */
@Repository
public interface LoanRepository extends JpaRepository<Loan, Long>, JpaSpecificationExecutor<Loan> {

    /**
     * Compta el nombre de préstecs d'un usuari que no tenen un estat concret.
     * 
     * @param userId L'identificador de l'usuari.
     * @param status L'estat a excloure.
     * @return El nombre de préstecs.
     * @author Jordi Verdalet Carrera
     */
    int countByUserIdAndStatusNot(Long userId, LoanStatus status);

    /**
     * Compta el nombre de préstecs d'un llibre que no tenen un estat concret.
     * 
     * @param bookId L'identificador del llibre.
     * @param status L'estat a excloure.
     * @return El nombre de préstecs.
     * @author Jordi Verdalet Carrera
     */
    int countByBookIdAndStatusNot(Long bookId, LoanStatus status);

    /**
     * Cerca els préstecs d'un usuari amb paginació.
     * 
     * @param userId L'identificador de l'usuari.
     * @param pageable La informació de paginació.
     * @return Una pàgina de préstecs.
     * @author Jordi Verdalet Carrera
     */
    Page<Loan> findByUserId(Long userId, Pageable pageable);

    /**
     * Cerca els préstecs d'un usuari que tinguin algun dels estats indicats, amb paginació.
     * 
     * @param userId L'identificador de l'usuari.
     * @param statuses La llista d'estats a incloure.
     * @param pageable La informació de paginació.
     * @return Una pàgina de préstecs.
     * @author Jordi Verdalet Carrera
     */
    Page<Loan> findByUserIdAndStatusIn(Long userId, List<LoanStatus> statuses, Pageable pageable);

    /**
     * Cerca els préstecs per estat.
     * 
     * @param status L'estat del préstec.
     * @return Una llista de préstecs amb l'estat indicat.
     * @author Jordi Verdalet Carrera
     */
    List<Loan> findByStatus(LoanStatus status);
}
