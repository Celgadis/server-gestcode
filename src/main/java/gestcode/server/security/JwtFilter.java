package gestcode.server.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Filtre que intercepta cada petició HTTP per extreure i validar el token JWT.
 * S'assegura que l'usuari estigui autenticat al context de Spring Security si
 * el token és vàlid.
 *
 * @author Jordi Verdalet Carrera
 */
@Component
public class JwtFilter extends OncePerRequestFilter {

    private final JwtUtils jwtUtils;
    private final CustomUserDetailsService userDetailsService;

    /**
     * Constructor del filtre JWT.
     *
     * @param jwtUtils           Utilitat per processar tokens JWT.
     * @param userDetailsService Servei per carregar detalls de l'usuari.
     * @author Jordi Verdalet Carrera
     */
    public JwtFilter(JwtUtils jwtUtils, CustomUserDetailsService userDetailsService) {
        this.jwtUtils = jwtUtils;
        this.userDetailsService = userDetailsService;
    }

    /**
     * valida el token de la capçalera i
     * estableix l'autenticació al context de seguretat.
     *
     * @param request     La petició HTTP.
     * @param response    La resposta HTTP.
     * @param filterChain La cadena de filtres.
     * @throws ServletException Si hi ha un error al servlet.
     * @throws IOException      Si hi ha un error d'entrada/sortida.
     * @author Jordi Verdalet Carrera
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        final String authorizationHeader = request.getHeader("Authorization");

        String username = null;
        String jwt = null;

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            jwt = authorizationHeader.substring(7);
            try {
                username = jwtUtils.extractUsername(jwt);
            } catch (Exception e) {
                // Log exception if token can't be parsed or is expired
                logger.warn("La validació del JWT ha fallat: " + e.getMessage());
            }
        }

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);

            if (jwtUtils.validateToken(jwt, userDetails)) {
                UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                usernamePasswordAuthenticationToken
                        .setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
            }
        }
        filterChain.doFilter(request, response);
    }
}
