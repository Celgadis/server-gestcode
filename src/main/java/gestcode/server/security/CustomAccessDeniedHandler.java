package gestcode.server.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import gestcode.server.dto.response.ApiErrorDTO;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Gestor personalitzat per a l'accés denegat (403 Forbidden).
 * Retorna una resposta JSON unificada utilitzant ApiErrorDTO.
 *
 * @author Jordi Verdalet Carrera
 */
@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    private final ObjectMapper objectMapper;

    /**
     * Constructor del gestor d'accés denegat.
     *
     * @param objectMapper Mapejador d'objectes per a la conversió a JSON.
     * @author Jordi Verdalet Carrera
     */
    public CustomAccessDeniedHandler(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    /**
     * Gestiona l'excepció d'accés denegat escrivint un JSON d'error a la resposta.
     *
     * @param request               La petició HTTP.
     * @param response              La resposta HTTP.
     * @param accessDeniedException L'excepció capturada.
     * @throws IOException      Si hi ha un error d'escriptura.
     * @throws ServletException Si hi ha un error del servlet.
     * @author Jordi Verdalet Carrera
     */
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException, ServletException {

        ApiErrorDTO errorDTO = new ApiErrorDTO(
                HttpStatus.FORBIDDEN.value(),
                "Accés Denegat",
                "No tens permisos per accedir a aquest recurs",
                request.getRequestURI()
        );

        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(objectMapper.writeValueAsString(errorDTO));
    }
}
