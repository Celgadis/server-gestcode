package gestcode.server.dto.request;

import jakarta.validation.constraints.NotNull;

/**
 * DTO per rebre la sol·licitud de creació d'un préstec.
 * 
 * @author Jordi Verdalet Carrera
 */
public class LoanRequestDTO {

    @NotNull(message = "El ID del llibre és obligatori")
    private Long bookId;

    public LoanRequestDTO() {
    }

    public LoanRequestDTO(Long bookId) {
        this.bookId = bookId;
    }

    public Long getBookId() {
        return bookId;
    }

    public void setBookId(Long bookId) {
        this.bookId = bookId;
    }
}
