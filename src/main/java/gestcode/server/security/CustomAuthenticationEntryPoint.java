package gestcode.server.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import gestcode.server.dto.response.ApiErrorDTO;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Punt d'entrada per a peticions no autenticades (401 Unauthorized).
 * Retorna una resposta JSON unificada utilitzant ApiErrorDTO.
 *
 * @author Jordi Verdalet Carrera
 */
@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;

    /**
     * Constructor del punt d'entrada d'autenticació.
     *
     * @param objectMapper Mapejador d'objectes per a la conversió a JSON.
     * @author Jordi Verdalet Carrera
     */
    public CustomAuthenticationEntryPoint(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    /**
     * Gestiona l'excepció d'autenticació escrivint un JSON d'error a la resposta.
     *
     * @param request       La petició HTTP.
     * @param response      La resposta HTTP.
     * @param authException L'excepció capturada.
     * @throws IOException      Si hi ha un error d'escriptura.
     * @throws ServletException Si hi ha un error del servlet.
     * @author Jordi Verdalet Carrera
     */
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException, ServletException {

        ApiErrorDTO errorDTO = new ApiErrorDTO(
                HttpStatus.UNAUTHORIZED.value(),
                "No Autoritzat",
                "Has d'estar autenticat per accedir a aquest recurs o el token és invàlid",
                request.getRequestURI()
        );

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(objectMapper.writeValueAsString(errorDTO));
    }
}
