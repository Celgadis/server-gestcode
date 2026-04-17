package gestcode.server.service;

import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;

/**
 * Interfície per a l'emmagatzematge de fitxers al servidor.
 * 
 * @author Jordi Verdalet Carrera
 */
public interface FileStorageService {
    
    /**
     * Guarda la imatge de portada al servidor.
     * 
     * @param file L'arxiu pujat per l'usuari.
     * @param uniqueName Nom únic per guardar l'arxiu (ex: l'ISBN del llibre).
     * @return El path relatiu o URL on s'ha guardat per accedir-hi.
     * @throws IOException Si hi ha un error pujant el fitxer.
     */
    String saveCover(MultipartFile file, String uniqueName) throws IOException;

    /**
     * Elimina la imatge de portada del servidor.
     * 
     * @param imageUrl La ruta relativa o URL de la imatge a eliminar.
     */
    void deleteCover(String imageUrl);
}
