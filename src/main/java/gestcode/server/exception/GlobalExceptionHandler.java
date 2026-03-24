package gestcode.server.exception;

import gestcode.server.dto.response.ApiErrorDTO;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Controlador de tractament global d'excepcions per a l'API.
 * Retorna una resposta unificada en cas d'error.
 *
 * @author Jordi Verdalet Carrera
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Gestiona excepcions de validació de paràmetres.
     *
     * @param ex      L'excepció de validació.
     * @param request La petició HTTP.
     * @return Una resposta amb els detalls de l'error i codi HTTP 400.
     * @author Jordi Verdalet Carrera
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorDTO> handleValidationExceptions(MethodArgumentNotValidException ex,
            HttpServletRequest request) {
        List<String> errors = ex.getBindingResult().getFieldErrors()
                .stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.toList());

        ApiErrorDTO errorDTO = new ApiErrorDTO(
                HttpStatus.BAD_REQUEST.value(),
                "Petició incorrecta",
                "Error de validació",
                request.getRequestURI());
        errorDTO.setFieldErrors(errors);

        return new ResponseEntity<>(errorDTO, HttpStatus.BAD_REQUEST);
    }

    /**
     * Gestiona excepcions de resposta amb estat específic.
     *
     * @param ex      L'excepció d'estat de resposta.
     * @param request La petició HTTP.
     * @return Una resposta amb el codi i missatge corresponents.
     * @author Jordi Verdalet Carrera
     */
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ApiErrorDTO> handleResponseStatusException(ResponseStatusException ex,
            HttpServletRequest request) {
        ApiErrorDTO errorDTO = new ApiErrorDTO(
                ex.getStatusCode().value(),
                ex.getStatusCode().toString(),
                ex.getReason() != null ? ex.getReason() : "S'ha produït un error",
                request.getRequestURI());
        return new ResponseEntity<>(errorDTO, ex.getStatusCode());
    }

    /**
     * Gestiona excepcions per credencials d'inici de sessió invàlides.
     *
     * @param ex      L'excepció de credencials dolentes.
     * @param request La petició HTTP.
     * @return Una resposta amb error d'autenticació i codi HTTP 401.
     * @author Jordi Verdalet Carrera
     */
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiErrorDTO> handleBadCredentialsException(BadCredentialsException ex,
            HttpServletRequest request) {
        ApiErrorDTO errorDTO = new ApiErrorDTO(
                HttpStatus.UNAUTHORIZED.value(),
                "No autoritzat",
                "Nom d'usuari o contrasenya invàlids",
                request.getRequestURI());
        return new ResponseEntity<>(errorDTO, HttpStatus.UNAUTHORIZED);
    }

    /**
     * Gestiona excepcions per accés denegat.
     *
     * @param ex      L'excepció d'accés denegat.
     * @param request La petició HTTP.
     * @return Una resposta amb error d'accés i codi HTTP 403.
     */
    @ExceptionHandler(org.springframework.security.access.AccessDeniedException.class)
    public ResponseEntity<ApiErrorDTO> handleAccessDeniedException(
            org.springframework.security.access.AccessDeniedException ex, HttpServletRequest request) {
        ApiErrorDTO errorDTO = new ApiErrorDTO(
                HttpStatus.FORBIDDEN.value(),
                "Accés Denegat",
                "No tens permisos per accedir a aquest recurs",
                request.getRequestURI());
        return new ResponseEntity<>(errorDTO, HttpStatus.FORBIDDEN);
    }

    /**
     * Gestiona qualsevol altra excepció genèrica no capturada.
     *
     * @param ex      L'excepció capturada.
     * @param request La petició HTTP.
     * @return Una resposta d'error intern del servidor i codi HTTP 500.
     * @author Jordi Verdalet Carrera
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorDTO> handleAllExceptions(Exception ex, HttpServletRequest request) {
        ApiErrorDTO errorDTO = new ApiErrorDTO(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Error intern del servidor",
                ex.getMessage() != null ? ex.getMessage() : "Error intern de servidor desconegut",
                request.getRequestURI());
        return new ResponseEntity<>(errorDTO, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
