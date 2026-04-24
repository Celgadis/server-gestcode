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

    int countByUserIdAndStatusNot(Long userId, LoanStatus status);

    int countByBookIdAndStatusNot(Long bookId, LoanStatus status);

    Page<Loan> findByUserId(Long userId, Pageable pageable);

    Page<Loan> findByUserIdAndStatusIn(Long userId, List<LoanStatus> statuses, Pageable pageable);

    List<Loan> findByStatus(LoanStatus status);
}
