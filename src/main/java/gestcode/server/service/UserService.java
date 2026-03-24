package gestcode.server.service;

import gestcode.server.dto.request.UserRegisterRequestDTO;
import gestcode.server.dto.request.UserUpdateRequestDTO;
import gestcode.server.dto.response.UserListResponseDTO;
import gestcode.server.dto.response.UserProfileResponseDTO;
import org.springframework.data.domain.Pageable;

/**
 * Interfície del servei per a la gestió d'usuaris.
 * Defineix les operacions de negoci principals relacionades amb l'entitat
 * Usuari.
 *
 * @author Jordi Verdalet Carrera
 */
public interface UserService {
    /**
     * Registra un nou usuari.
     *
     * @param request Dades de registre.
     * @return El perfil de l'usuari creat.
     * @author Jordi Verdalet Carrera
     */
    UserProfileResponseDTO registerUser(UserRegisterRequestDTO request);

    /**
     * Obté les dades d'un usuari segons l'identificador.
     *
     * @param id Identificador de l'usuari.
     * @return El perfil de l'usuari corresponent.
     * @author Jordi Verdalet Carrera
     */
    UserProfileResponseDTO getUserById(Long id);

    /**
     * Actualitza les dades d'un usuari existent.
     *
     * @param id      Identificador de l'usuari a modificar.
     * @param request Dades noves de l'usuari.
     * @return El perfil de l'usuari modificat.
     * @author Jordi Verdalet Carrera
     */
    UserProfileResponseDTO updateUser(Long id, UserUpdateRequestDTO request);

    /**
     * Retorna una llista d'usuaris paginada i filtrada.
     *
     * @param keyword  Text per filtrar la cerca d'usuaris (nom, email...).
     * @param pageable Paràmetres de paginació.
     * @return Objecte de resposta amb la llista d'usuaris i dades de la llista.
     * @author Jordi Verdalet Carrera
     */
    UserListResponseDTO listUsers(String keyword, Pageable pageable);

    /**
     * Obté el perfil de l'usuari actual autenticat.
     *
     * @return El perfil de l'usuari logejat.
     * @author Jordi Verdalet Carrera
     */
    UserProfileResponseDTO getMe();
}
