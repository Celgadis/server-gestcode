package gestcode.server.dto.response;

import java.util.List;

/**
 * Objecte de Transferència de Dades (DTO) per a la llista de préstecs paginada.
 *
 * @author Jordi Verdalet Carrera
 */
public class LoanListResponseDTO {
    private List<LoanResponseDTO> content;
    private int pageNo;
    private int pageSize;
    private long totalElements;
    private int totalPages;
    private boolean last;

    public LoanListResponseDTO() {
    }

    public List<LoanResponseDTO> getContent() {
        return content;
    }

    public void setContent(List<LoanResponseDTO> content) {
        this.content = content;
    }

    public int getPageNo() {
        return pageNo;
    }

    public void setPageNo(int pageNo) {
        this.pageNo = pageNo;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public long getTotalElements() {
        return totalElements;
    }

    public void setTotalElements(long totalElements) {
        this.totalElements = totalElements;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    public boolean isLast() {
        return last;
    }

    public void setLast(boolean last) {
        this.last = last;
    }
}
