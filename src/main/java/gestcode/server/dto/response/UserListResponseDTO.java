package gestcode.server.dto.response;

import java.util.List;

/**
 * Objecte de Transferència de Dades (DTO) per a la llista d'usuaris paginada.
 *
 * @author Jordi Verdalet Carrera
 */
public class UserListResponseDTO {
    private List<UserProfileResponseDTO> content;
    private int pageNo;
    private int pageSize;
    private long totalElements;
    private int totalPages;
    private boolean last;

    /**
     * Constructor per defecte.
     *
     * @author Jordi Verdalet Carrera
     */
    public UserListResponseDTO() {}

    /**
     * Obté la llista d'usuaris.
     *
     * @return Llista de perfils d'usuari.
     * @author Jordi Verdalet Carrera
     */
    public List<UserProfileResponseDTO> getContent() {
        return content;
    }

    /**
     * Estableix la llista d'usuaris.
     *
     * @param content Llista de perfils d'usuari.
     * @author Jordi Verdalet Carrera
     */
    public void setContent(List<UserProfileResponseDTO> content) {
        this.content = content;
    }

    /**
     * Obté el número de pàgina actual.
     *
     * @return Número de pàgina.
     * @author Jordi Verdalet Carrera
     */
    public int getPageNo() {
        return pageNo;
    }

    /**
     * Estableix el número de pàgina actual.
     *
     * @param pageNo Número de pàgina.
     * @author Jordi Verdalet Carrera
     */
    public void setPageNo(int pageNo) {
        this.pageNo = pageNo;
    }

    /**
     * Obté la mida de la pàgina.
     *
     * @return Mida de la pàgina.
     * @author Jordi Verdalet Carrera
     */
    public int getPageSize() {
        return pageSize;
    }

    /**
     * Estableix la mida de la pàgina.
     *
     * @param pageSize Mida de la pàgina.
     * @author Jordi Verdalet Carrera
     */
    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    /**
     * Obté el total d'elements.
     *
     * @return Total d'elements.
     * @author Jordi Verdalet Carrera
     */
    public long getTotalElements() {
        return totalElements;
    }

    /**
     * Estableix el total d'elements.
     *
     * @param totalElements Total d'elements.
     * @author Jordi Verdalet Carrera
     */
    public void setTotalElements(long totalElements) {
        this.totalElements = totalElements;
    }

    /**
     * Obté el total de pàgines.
     *
     * @return Total de pàgines.
     * @author Jordi Verdalet Carrera
     */
    public int getTotalPages() {
        return totalPages;
    }

    /**
     * Estableix el total de pàgines.
     *
     * @param totalPages Total de pàgines.
     * @author Jordi Verdalet Carrera
     */
    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    /**
     * Indica si és l'última pàgina.
     *
     * @return Verdader si és l'última pàgina.
     * @author Jordi Verdalet Carrera
     */
    public boolean isLast() {
        return last;
    }

    /**
     * Estableix si és l'última pàgina.
     *
     * @param last Indica si és l'última pàgina.
     * @author Jordi Verdalet Carrera
     */
    public void setLast(boolean last) {
        this.last = last;
    }
}
