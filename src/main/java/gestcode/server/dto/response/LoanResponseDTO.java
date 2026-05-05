package gestcode.server.dto.response;

import gestcode.server.model.enums.LoanStatus;
import java.time.LocalDateTime;

/**
 * DTO de resposta que conté la informació d'un préstec.
 * 
 * @author Jordi Verdalet Carrera
 */
public class LoanResponseDTO {

    private Long id;
    private Long userId;
    private String username;
    private Long bookId;
    private String bookTitle;
    private LocalDateTime loanDate;
    private LocalDateTime dueDate;
    private LocalDateTime returnDate;
    private LoanStatus status;

    public LoanResponseDTO() {
    }

    public LoanResponseDTO(Long id, Long userId, String username, Long bookId, String bookTitle,
            LocalDateTime loanDate, LocalDateTime dueDate, LocalDateTime returnDate, LoanStatus status) {
        this.id = id;
        this.userId = userId;
        this.username = username;
        this.bookId = bookId;
        this.bookTitle = bookTitle;
        this.loanDate = loanDate;
        this.dueDate = dueDate;
        this.returnDate = returnDate;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Long getBookId() {
        return bookId;
    }

    public void setBookId(Long bookId) {
        this.bookId = bookId;
    }

    public String getBookTitle() {
        return bookTitle;
    }

    public void setBookTitle(String bookTitle) {
        this.bookTitle = bookTitle;
    }

    public LocalDateTime getLoanDate() {
        return loanDate;
    }

    public void setLoanDate(LocalDateTime loanDate) {
        this.loanDate = loanDate;
    }

    public LocalDateTime getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDateTime dueDate) {
        this.dueDate = dueDate;
    }

    public LocalDateTime getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(LocalDateTime returnDate) {
        this.returnDate = returnDate;
    }

    public LoanStatus getStatus() {
        return status;
    }

    public void setStatus(LoanStatus status) {
        this.status = status;
    }
}
