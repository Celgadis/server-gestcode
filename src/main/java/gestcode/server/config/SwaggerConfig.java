package gestcode.server.config;

import io.swagger.v3.core.converter.ModelConverters;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.responses.ApiResponses;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import gestcode.server.dto.response.ApiErrorDTO;
import java.util.Map;

/**
 * Configuració de Swagger/OpenAPI i documentació de l'API REST.
 *
 * @author Jordi Verdalet Carrera
 */
@Configuration
public class SwaggerConfig {

    private static final String SECURITY_SCHEME_NAME = "bearerAuth";

    /**
     * Crea i configura el bean OpenAPI amb la informació de l'API i la seguretat
     * JWT.
     *
     * @return L'objecte OpenAPI configurat.
     * @author Jordi Verdalet Carrera
     */
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Gestcode API")
                        .version("1.0")
                        .description("API REST per a la gestió de la biblioteca - Gestcode.")
                        .contact(new Contact().name("Jordi Verdalet Carrera")
                                .email("max_morrel@hotmail.com")))
                .addSecurityItem(new SecurityRequirement().addList(SECURITY_SCHEME_NAME))
                .components(new Components()
                        .addSecuritySchemes(SECURITY_SCHEME_NAME,
                                new SecurityScheme()
                                        .name(SECURITY_SCHEME_NAME)
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .description("Introdueix el token JWT obtingut en l'inici de sessió.")));
    }

    /**
     * Personalitzador global per a OpenAPI que assegura que totes les respostes
     * d'error (4xx i 5xx)
     * utilitzin l'esquema ApiErrorDTO si no en tenen cap de definit.
     *
     * @return El personalitzador d'OpenAPI.
     * @author Jordi Verdalet Carrera
     */
    @Bean
    public OpenApiCustomizer customerGlobalErrorOpenApiCustomizer() {
        return openApi -> {
            // Registrem l'esquema d'error manualment per garantir que existeixi
            Map<String, Schema> schemas = ModelConverters.getInstance().read(ApiErrorDTO.class);
            schemas.forEach((name, schema) -> openApi.getComponents().addSchemas(name, schema));

            openApi.getPaths().values().forEach(pathItem -> pathItem.readOperations().forEach(operation -> {
                ApiResponses responses = operation.getResponses();
                responses.forEach((code, response) -> {
                    // Si el codi comença per 4 o 5, forcem el contingut a ApiErrorDTO
                    if (code.startsWith("4") || code.startsWith("5")) {
                        MediaType mediaType = new MediaType();
                        mediaType.schema(new Schema<>().$ref("#/components/schemas/ApiErrorDTO"));

                        Content content = new Content();
                        content.addMediaType("application/json", mediaType);

                        response.setContent(content);
                    }
                });
            }));
        };
    }
}
