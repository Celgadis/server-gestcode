package gestcode.server.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Utilitat per a la gestió de tokens JWT, incloent generació,
 * validació i extracció de dades del token.
 *
 * @author Jordi Verdalet Carrera
 */
@Component
public class JwtUtils {

    @Value("${jwt.secret:AquestaEsUnaContrasenyaPerDefecteMentresDesenvolupo!!Cambila!}")
    private String secret;

    @Value("${jwt.expirationDays:7}")
    private int expirationDays;

    /**
     * Obté la clau de signatura a partir del secret.
     *
     * @return La clau criptogràfica.
     * @author Jordi Verdalet Carrera
     */
    private Key getSigningKey() {
        byte[] keyBytes = secret.getBytes();
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * Extreu el nom d'usuari d'un token JWT.
     *
     * @param token El token JWT.
     * @return El nom d'usuari.
     * @author Jordi Verdalet Carrera
     */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Extreu la data d'expiració d'un token JWT.
     *
     * @param token El token JWT.
     * @return La data d'expiració.
     * @author Jordi Verdalet Carrera
     */
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * Extreu una dada específica dels claims d'un token.
     *
     * @param token          El token JWT.
     * @param claimsResolver Funció per extreure la dada desitjada.
     * @param <T>            Tipus de dada a extreure.
     * @return La dada extreta.
     * @author Jordi Verdalet Carrera
     */
    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Extreu tots els claims d'un token JWT signat.
     *
     * @param token El token JWT.
     * @return Els claims del token.
     * @author Jordi Verdalet Carrera
     */
    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * Comprova si un token ha caducat.
     *
     * @param token El token JWT.
     * @return Cert si ha caducat, fals altrament.
     * @author Jordi Verdalet Carrera
     */
    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    /**
     * Genera un token JWT vàlid per a un usuari.
     *
     * @param userDetails Els detalls de l'usuari.
     * @return El token generat en format String.
     * @author Jordi Verdalet Carrera
     */
    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        // Extract the user entity to get more roles/info if needed
        if (userDetails instanceof CustomUserDetails) {
            CustomUserDetails customUser = (CustomUserDetails) userDetails;
            claims.put("role", customUser.getUser().getRole().name());
            claims.put("id", customUser.getUser().getId());
        }
        return createToken(claims, userDetails.getUsername());
    }

    /**
     * Crea i signa el token JWT.
     *
     * @param claims  Diccionari amb les dades extra a incloure.
     * @param subject El subjecte principal.
     * @return El token JWT generat i signat.
     * @author Jordi Verdalet Carrera
     */
    private String createToken(Map<String, Object> claims, String subject) {
        long expirationTimeMs = expirationDays * 24L * 60L * 60L * 1000L;
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expirationTimeMs))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Valida un token JWT comparant usuari i expiració.
     *
     * @param token       El token JWT a validar.
     * @param userDetails Detalls de l'usuari contra qui el validem.
     * @return Cert si és vàlid, fals si és invàlid o expirat.
     * @author Jordi Verdalet Carrera
     */
    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }
}
