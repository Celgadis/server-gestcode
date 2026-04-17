package gestcode.server.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Configuració web per servir recursos estàtics com les imatges pujades.
 * 
 * @author Jordi Verdalet Carrera
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${app.upload.dir:uploads/covers}")
    private String uploadDir;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        Path uploadPath = Paths.get(uploadDir).toAbsolutePath().normalize();
        String uploadAbsolutePath = uploadPath.toFile().getAbsolutePath();
        
        // Afegim el handler per servir els fitxers de la carpeta configurada
        registry.addResourceHandler("/uploads/covers/**")
                .addResourceLocations("file:" + uploadAbsolutePath + "/");
    }
}
