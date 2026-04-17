package gestcode.server.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * Configuració per al component ObjectMapper (Jackson).
 * S'encarrega d'assegurar que el Bean estigui disponible i configurat per a dates Java 8.
 *
 * @author Jordi Verdalet Carrera
 */
@Configuration
public class JacksonConfig {

    /**
     * Defineix el Bean d'ObjectMapper principal de l'aplicació.
     * Registra el mòdul per a dates i desactiva l'escriptura de dates com a timestamps.
     *
     * @return ObjectMapper configurat.
     * @author Jordi Verdalet Carrera
     */
    @Bean
    @Primary
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        return mapper;
    }
}
