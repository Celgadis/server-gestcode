package gestcode.server.repository;

import gestcode.server.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repositori de dades per a l'entitat Usuari.
 * Proporciona els mètodes de gestió a la base de dades.
 *
 * @author Jordi Verdalet Carrera
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {
    /**
     * Cerca un usuari pel seu nom d'usuari.
     *
     * @param username El nom d'usuari a cercar.
     * @return L'usuari.
     * @author Jordi Verdalet Carrera
     */
    Optional<User> findByUsername(String username);

    /**
     * Cerca un usuari pel seu correu electrònic.
     *
     * @param email El correu electrònic a cercar.
     * @return L'usuari.
     * @author Jordi Verdalet Carrera
     */
    Optional<User> findByEmail(String email);

    /**
     * Comprova si existeix un usuari amb el nom d'usuari indicat.
     *
     * @param username El nom d'usuari.
     * @return Cert si existeix.
     * @author Jordi Verdalet Carrera
     */
    boolean existsByUsername(String username);

    /**
     * Comprova si existeix un usuari amb el correu electrònic indicat.
     *
     * @param email El correu electrònic.
     * @return Cert si existeix.
     * @author Jordi Verdalet Carrera
     */
    boolean existsByEmail(String email);
}
