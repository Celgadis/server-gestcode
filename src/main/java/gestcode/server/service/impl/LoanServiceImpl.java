package gestcode.server.service.impl;

import gestcode.server.dto.request.LoanRequestDTO;
import gestcode.server.dto.response.LoanListResponseDTO;
import gestcode.server.dto.response.LoanResponseDTO;
import gestcode.server.exception.BookNotAvailableException;
import gestcode.server.exception.MaxLoansExceededException;
import gestcode.server.model.entity.Book;
import gestcode.server.model.entity.Loan;
import gestcode.server.model.entity.User;
import gestcode.server.model.enums.LoanStatus;
import gestcode.server.model.enums.Role;
import gestcode.server.repository.BookRepository;
import gestcode.server.repository.LoanRepository;
import gestcode.server.service.LoanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import jakarta.persistence.criteria.Predicate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementació del servei de préstecs.
 * 
 * @author Jordi Verdalet Carrera
 */
@Service
public class LoanServiceImpl implements LoanService {

    @Autowired
    private LoanRepository loanRepository;

    @Autowired
    private BookRepository bookRepository;

    /**
     * Crea un nou préstec per a un usuari i llibre especificats.
     * 
     * @param requestDTO Les dades de la petició del préstec.
     * @param user L'usuari que fa el préstec.
     * @return Les dades del préstec creat.
     * @author Jordi Verdalet Carrera
     */
    @Override
    @Transactional
    public LoanResponseDTO createLoan(LoanRequestDTO requestDTO, User user) {
        int activeLoansCount = loanRepository.countByUserIdAndStatusNot(user.getId(), LoanStatus.RETORNAT);
        if (activeLoansCount >= 3) {
            throw new MaxLoansExceededException("L'usuari ja té el màxim de 3 préstecs actius.");
        }

        Book book = bookRepository.findById(requestDTO.getBookId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Llibre no trobat"));

        if (book.getAvailableCopies() <= 0) {
            throw new BookNotAvailableException("No hi ha còpies disponibles per a aquest llibre.");
        }

        book.setAvailableCopies(book.getAvailableCopies() - 1);
        bookRepository.save(book);

        LocalDateTime now = LocalDateTime.now();
        Loan loan = new Loan(user, book, now, now.plusDays(7), LoanStatus.ACTIU);
        Loan savedLoan = loanRepository.save(loan);

        return mapToDTO(savedLoan);
    }

    /**
     * Retorna un préstec existent.
     * 
     * @param id L'identificador del préstec.
     * @param user L'usuari que fa la devolució.
     * @return Les dades del préstec actualitzat.
     * @author Jordi Verdalet Carrera
     */
    @Override
    @Transactional
    public LoanResponseDTO returnLoan(Long id, User user) {
        Loan loan = loanRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Préstec no trobat"));

        if (user.getRole() != Role.ADMIN && !loan.getUser().getId().equals(user.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No pots retornar un préstec que no és teu");
        }

        if (loan.getStatus() == LoanStatus.RETORNAT) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Aquest préstec ja està retornat");
        }

        loan.setReturnDate(LocalDateTime.now());
        loan.setStatus(LoanStatus.RETORNAT);

        Book book = loan.getBook();
        book.setAvailableCopies(book.getAvailableCopies() + 1);
        bookRepository.save(book);

        Loan updatedLoan = loanRepository.save(loan);
        return mapToDTO(updatedLoan);
    }

    /**
     * Obté tots els préstecs amb opcions de filtre i paginació.
     * 
     * @param userId L'identificador de l'usuari.
     * @param bookId L'identificador del llibre.
     * @param status L'estat del préstec.
     * @param pageNo Número de la pàgina.
     * @param pageSize Mida de la pàgina.
     * @param sortBy Camp d'ordenació.
     * @param sortDir Direcció d'ordenació.
     * @return Pàgina de préstecs.
     * @author Jordi Verdalet Carrera
     */
    @Override
    public LoanListResponseDTO getAllLoans(Long userId, Long bookId, LoanStatus status,
            int pageNo, int pageSize, String sortBy, String sortDir) {

        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(pageNo, pageSize, sort);

        Specification<Loan> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (userId != null) {
                predicates.add(cb.equal(root.get("user").get("id"), userId));
            }
            if (bookId != null) {
                predicates.add(cb.equal(root.get("book").get("id"), bookId));
            }
            if (status != null) {
                predicates.add(cb.equal(root.get("status"), status));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };

        Page<Loan> page = loanRepository.findAll(spec, pageable);
        return mapToPageDTO(page);
    }

    /**
     * Obté els préstecs d'un usuari específic.
     * 
     * @param user L'usuari del qual obtenir els préstecs.
     * @param pageNo Número de la pàgina.
     * @param pageSize Mida de la pàgina.
     * @param sortBy Camp d'ordenació.
     * @param sortDir Direcció d'ordenació.
     * @return Pàgina de préstecs de l'usuari.
     * @author Jordi Verdalet Carrera
     */
    @Override
    public LoanListResponseDTO getMyLoans(User user, int pageNo, int pageSize, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(pageNo, pageSize, sort);

        Page<Loan> page = loanRepository.findByUserId(user.getId(), pageable);
        return mapToPageDTO(page);
    }

    /**
     * Obté els préstecs d'un usuari amb estats específics.
     * 
     * @param user L'usuari del qual obtenir els préstecs.
     * @param statuses Els estats per filtrar.
     * @param pageNo Número de la pàgina.
     * @param pageSize Mida de la pàgina.
     * @param sortBy Camp d'ordenació.
     * @param sortDir Direcció d'ordenació.
     * @return Pàgina de préstecs filtrada.
     * @author Jordi Verdalet Carrera
     */
    @Override
    public LoanListResponseDTO getMyLoansByStatuses(User user, List<LoanStatus> statuses,
            int pageNo, int pageSize, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(pageNo, pageSize, sort);

        Page<Loan> page = loanRepository.findByUserIdAndStatusIn(user.getId(), statuses, pageable);
        return mapToPageDTO(page);
    }

    /**
     * Obté tots els préstecs que coincideixen amb els estats indicats.
     * 
     * @param statuses Els estats per filtrar.
     * @param pageNo Número de la pàgina.
     * @param pageSize Mida de la pàgina.
     * @param sortBy Camp d'ordenació.
     * @param sortDir Direcció d'ordenació.
     * @return Pàgina de préstecs filtrada per estats.
     * @author Jordi Verdalet Carrera
     */
    @Override
    public LoanListResponseDTO getAllLoansByStatuses(List<LoanStatus> statuses,
            int pageNo, int pageSize, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(pageNo, pageSize, sort);

        Specification<Loan> spec = (root, query, cb) -> root.get("status").in(statuses);
        Page<Loan> page = loanRepository.findAll(spec, pageable);
        return mapToPageDTO(page);
    }

    /**
     * Revisa i actualitza els estats dels préstecs segons la data de venciment.
     * @author Jordi Verdalet Carrera
     */
    @Override
    @Transactional
    public void checkAndUpdateLoanStatuses() {
        List<Loan> activeLoans = loanRepository.findByStatus(LoanStatus.ACTIU);
        List<Loan> nearDueLoans = loanRepository.findByStatus(LoanStatus.PROXIM_A_CADUCAR);

        LocalDateTime now = LocalDateTime.now();
        List<Loan> toUpdate = new ArrayList<>();

        for (Loan loan : activeLoans) {
            if (now.isAfter(loan.getDueDate())) {
                loan.setStatus(LoanStatus.FORA_DE_LIMIT);
                toUpdate.add(loan);
            } else if (now.plusDays(2).isAfter(loan.getDueDate())) {
                loan.setStatus(LoanStatus.PROXIM_A_CADUCAR);
                toUpdate.add(loan);
            }
        }

        for (Loan loan : nearDueLoans) {
            if (now.isAfter(loan.getDueDate())) {
                loan.setStatus(LoanStatus.FORA_DE_LIMIT);
                toUpdate.add(loan);
            }
        }

        if (!toUpdate.isEmpty()) {
            loanRepository.saveAll(toUpdate);
        }
    }

    /**
     * Sincronitza el nombre de còpies disponibles dels llibres segons els préstecs actius.
     * @author Jordi Verdalet Carrera
     */
    @Override
    @Transactional
    public void syncBookAvailableCopies() {
        List<Book> books = bookRepository.findAll();
        List<Book> toUpdate = new ArrayList<>();

        for (Book book : books) {
            int activeLoans = loanRepository.countByBookIdAndStatusNot(book.getId(), LoanStatus.RETORNAT);
            int calculatedAvailable = book.getQuantity() - activeLoans;
            
            if (calculatedAvailable < 0) {
                // Prevenir negatius si hi hagués una inconsistència gran. (O ho posem a 0)
                calculatedAvailable = 0;
            }

            if (book.getAvailableCopies() != calculatedAvailable) {
                book.setAvailableCopies(calculatedAvailable);
                toUpdate.add(book);
            }
        }

        if (!toUpdate.isEmpty()) {
            bookRepository.saveAll(toUpdate);
        }
    }

    /**
     * Mapeja una entitat Loan a un LoanResponseDTO.
     * 
     * @param loan L'entitat a mapejar.
     * @return L'objecte DTO mapejat.
     * @author Jordi Verdalet Carrera
     */
    private LoanResponseDTO mapToDTO(Loan loan) {
        return new LoanResponseDTO(
                loan.getId(),
                loan.getUser().getId(),
                loan.getUser().getUsername(),
                loan.getBook().getId(),
                loan.getBook().getTitle(),
                loan.getLoanDate(),
                loan.getDueDate(),
                loan.getReturnDate(),
                loan.getStatus());
    }

    /**
     * Mapeja una pàgina d'entitats Loan a un LoanListResponseDTO.
     * 
     * @param page La pàgina d'entitats a mapejar.
     * @return L'objecte DTO de llista de préstecs.
     * @author Jordi Verdalet Carrera
     */
    private LoanListResponseDTO mapToPageDTO(Page<Loan> page) {
        List<LoanResponseDTO> dtos = page.getContent().stream()
                .map(this::mapToDTO).collect(Collectors.toList());

        LoanListResponseDTO listDTO = new LoanListResponseDTO();
        listDTO.setContent(dtos);
        listDTO.setPageNo(page.getNumber());
        listDTO.setPageSize(page.getSize());
        listDTO.setTotalElements(page.getTotalElements());
        listDTO.setTotalPages(page.getTotalPages());
        listDTO.setLast(page.isLast());

        return listDTO;
    }
}
