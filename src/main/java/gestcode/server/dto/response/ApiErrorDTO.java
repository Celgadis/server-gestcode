package gestcode.server.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Objecte de Transferència de Dades (DTO) per gestionar i retornar errors de
 * l'API.
 *
 * @author Jordi Verdalet Carrera
 */
@Schema(description = "Esquema per a les respostes d'error de l'API")
public class ApiErrorDTO {

    @Schema(description = "Data i hora de l'error", example = "2024-04-12T10:00:00")
    private LocalDateTime timestamp;

    @Schema(description = "Codi d'estat HTTP", example = "404")
    private int status;

    @Schema(description = "Tipus d'error HTTP", example = "Tipus d'error")
    private String error;

    @Schema(description = "Missatge detallat de l'error", example = "Missatge detallat del error")
    private String message;

    @Schema(description = "Ruta de l'API on s'ha produït l'error", example = "/api/xxx")
    private String path;

    @Schema(description = "Llista d'errors de validació per camps (si n'hi ha)")
    private List<String> fieldErrors;

    /**
     * Constructor per defecte.
     * Estableix la data i hora actuals.
     *
     * @author Jordi Verdalet Carrera
     */
    public ApiErrorDTO() {
        this.timestamp = LocalDateTime.now();
    }

    /**
     * Constructor amb paràmetres per configurar completament l'error.
     *
     * @param status  Codi d'estat HTTP.
     * @param error   Tipus d'error (ex. "Bad Request").
     * @param message Missatge descriptiu de l'error.
     * @param path    Ruta de l'API on s'ha produït l'error.
     * @author Jordi Verdalet Carrera
     */
    public ApiErrorDTO(int status, String error, String message, String path) {
        this();
        this.status = status;
        this.error = error;
        this.message = message;
        this.path = path;
    }

    /**
     * Obté la marca de temps de l'error.
     *
     * @return La marca de temps.
     * @author Jordi Verdalet Carrera
     */
    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    /**
     * Estableix la marca de temps de l'error.
     *
     * @param timestamp La marca de temps.
     * @author Jordi Verdalet Carrera
     */
    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    /**
     * Obté el codi d'estat HTTP.
     *
     * @return El codi d'estat.
     * @author Jordi Verdalet Carrera
     */
    public int getStatus() {
        return status;
    }

    /**
     * Estableix el codi d'estat HTTP.
     *
     * @param status El codi d'estat.
     * @author Jordi Verdalet Carrera
     */
    public void setStatus(int status) {
        this.status = status;
    }

    /**
     * Obté el tipus d'error.
     *
     * @return El tipus d'error.
     * @author Jordi Verdalet Carrera
     */
    public String getError() {
        return error;
    }

    /**
     * Estableix el tipus d'error.
     *
     * @param error El tipus d'error.
     * @author Jordi Verdalet Carrera
     */
    public void setError(String error) {
        this.error = error;
    }

    /**
     * Obté el missatge d'error.
     *
     * @return El missatge d'error.
     * @author Jordi Verdalet Carrera
     */
    public String getMessage() {
        return message;
    }

    /**
     * Estableix el missatge d'error.
     *
     * @param message El missatge d'error.
     * @author Jordi Verdalet Carrera
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * Obté la ruta on s'ha produït l'error.
     *
     * @return La ruta.
     * @author Jordi Verdalet Carrera
     */
    public String getPath() {
        return path;
    }

    /**
     * Estableix la ruta on s'ha produït l'error.
     *
     * @param path La ruta.
     * @author Jordi Verdalet Carrera
     */
    public void setPath(String path) {
        this.path = path;
    }

    /**
     * Obté els errors de camps específics (validacions).
     *
     * @return Llista d'errors de camp.
     * @author Jordi Verdalet Carrera
     */
    public List<String> getFieldErrors() {
        return fieldErrors;
    }

    /**
     * Estableix els errors de camps específics.
     *
     * @param fieldErrors Llista d'errors de camp.
     * @author Jordi Verdalet Carrera
     */
    public void setFieldErrors(List<String> fieldErrors) {
        this.fieldErrors = fieldErrors;
    }
}
