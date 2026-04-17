package gestcode.server.service.impl;

import gestcode.server.service.FileStorageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

/**
 * Implementació del servei d'emmagatzematge de fitxers que guarda al sistema de fitxers local.
 * 
 * @author Jordi Verdalet Carrera
 */
@Service
public class FileStorageServiceImpl implements FileStorageService {

    private final Path fileStorageLocation;
    private final String uploadPathRoute = "/uploads/covers/";

    public FileStorageServiceImpl(@Value("${app.upload.dir:uploads/covers}") String uploadDir) {
        this.fileStorageLocation = Paths.get(uploadDir).toAbsolutePath().normalize();
        try {
            Files.createDirectories(this.fileStorageLocation);
        } catch (Exception ex) {
            throw new RuntimeException("No s'ha pogut crear el directori on es guardaran les portades.", ex);
        }
    }

    @Override
    public String saveCover(MultipartFile file, String uniqueName) throws IOException {
        String originalFilename = StringUtils.cleanPath(file.getOriginalFilename());
        
        // Obtenir extensió
        String extension = "";
        if (originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        } else {
            // Predeterminat si no hi ha extensió però és una imatge (ex. png/jpg)
            if (file.getContentType() != null) {
                if (file.getContentType().equals("image/png")) extension = ".png";
                else if (file.getContentType().equals("image/jpeg")) extension = ".jpg";
            }
        }
        
        // Nom de l'arxiu sanititzat
        String fileName = uniqueName + extension;

        if (fileName.contains("..")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El nom de l'arxiu conté una seqüència de camí invàlida " + fileName);
        }

        // Guardar arxiu
        Path targetLocation = this.fileStorageLocation.resolve(fileName);
        Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
        
        return uploadPathRoute + fileName;
    }

    @Override
    public void deleteCover(String imageUrl) {
        if (imageUrl == null || imageUrl.isEmpty()) {
            return;
        }

        try {
            // Obtenim només el nom de l'arxiu de la ruta
            String fileName = imageUrl.substring(imageUrl.lastIndexOf("/") + 1);
            Path filePath = this.fileStorageLocation.resolve(fileName).normalize();
            
            // Assegurar que estem esborrant dins de la carpeta
            if (filePath.startsWith(this.fileStorageLocation)) {
                Files.deleteIfExists(filePath);
            }
        } catch (IOException ex) {
            System.err.println("No s'ha pogut esborrar l'arxiu d'imatge: " + imageUrl);
        }
    }
}
