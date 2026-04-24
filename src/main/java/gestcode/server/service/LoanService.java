package gestcode.server.service;

import gestcode.server.dto.request.LoanRequestDTO;
import gestcode.server.dto.response.LoanListResponseDTO;
import gestcode.server.dto.response.LoanResponseDTO;
import gestcode.server.model.entity.User;
import gestcode.server.model.enums.LoanStatus;
import java.util.List;

/**
 * Interfície del servei de préstecs.
 * 
 * @author Jordi Verdalet Carrera
 */
public interface LoanService {

        LoanResponseDTO createLoan(LoanRequestDTO requestDTO, User user);

        LoanResponseDTO returnLoan(Long id, User user);

        LoanListResponseDTO getAllLoans(Long userId, Long bookId, LoanStatus status,
                        int pageNo, int pageSize, String sortBy, String sortDir);

        LoanListResponseDTO getMyLoans(User user, int pageNo, int pageSize, String sortBy, String sortDir);

        LoanListResponseDTO getMyLoansByStatuses(User user, List<LoanStatus> statuses,
                        int pageNo, int pageSize, String sortBy, String sortDir);

        LoanListResponseDTO getAllLoansByStatuses(List<LoanStatus> statuses,
                        int pageNo, int pageSize, String sortBy, String sortDir);

        void checkAndUpdateLoanStatuses();

        void syncBookAvailableCopies();
}
