package gestcode.server.config;

import gestcode.server.model.entity.User;
import gestcode.server.model.enums.Role;
import gestcode.server.model.enums.UserStatus;
import gestcode.server.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Per omplir la base de dades amb dades basiques o de test
 * (com l'administrador i usuaris de prova) en iniciar l'aplicació.
 *
 * @author Jordi Verdalet Carrera
 */
@Component
@Order(1)
public class UserSeeder implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(UserSeeder.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.default.admin.password:admin1234}")
    private String adminPassword;

    @Value("${app.seed.testdata:false}")
    private boolean seedTestData;

    @Value("${app.seed.testdata.password:test1234}")
    private String testPassword;

    /**
     * Constructor del seeder d'usuaris.
     *
     * @param userRepository  Repositori d'usuaris.
     * @param passwordEncoder Encriptador de contrasenyes.
     * @author Jordi Verdalet Carrera
     */
    public UserSeeder(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Mètode que s'executa en arrencar l'aplicació.
     * Crea l'usuari administrador per defecte i usuaris de prova si està habilitat
     * a la configuració.
     *
     * @param args Arguments de línia de comandes.
     * @throws Exception Si hi ha algun error en la creació.
     * @author Jordi Verdalet Carrera
     */
    @Override
    public void run(String... args) throws Exception {
        seedAdminUser();

        if (seedTestData) {
            seedTestUsers();
        } else {
            logger.info("Test data seeding is disabled by 'app.seed.testdata'. Skipping test users creation.");
        }
    }

    /**
     * Crea l'usuari administrador si no existeix a la base de dades.
     *
     * @author Jordi Verdalet Carrera
     */
    private void seedAdminUser() {
        if (!userRepository.existsByUsername("admin")) {
            logger.info("Creating default admin user...");
            User admin = new User();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode(adminPassword));
            admin.setFirstName("Admin");
            admin.setLastName1("System");
            admin.setEmail("admin@gestcode.local");
            admin.setRole(Role.ADMIN);
            admin.setStatus(UserStatus.ACTIVE);
            admin.setEnabled(true);
            userRepository.save(admin);
            logger.info("Default admin user created successfully.");
        } else {
            logger.info("Admin user already exists. Skipping creation.");
        }
    }

    /**
     * Crea usuaris de prova si no existeixen a la base de dades.
     *
     * @author Jordi Verdalet Carrera
     */
    private void seedTestUsers() {
        if (!userRepository.existsByUsername("test1")) {
            logger.info("Creating test1 user...");
            User test1 = new User();
            test1.setUsername("test1");
            test1.setPassword(passwordEncoder.encode(testPassword));
            test1.setFirstName("Test");
            test1.setLastName1("User 1");
            test1.setEmail("test1@gestcode.test");
            test1.setRole(Role.USER);
            test1.setStatus(UserStatus.ACTIVE);
            test1.setEnabled(true);
            userRepository.save(test1);
            logger.info("Test1 user created successfully.");
        }

        if (!userRepository.existsByUsername("test2")) {
            logger.info("Creating test2 user...");
            User test2 = new User();
            test2.setUsername("test2");
            test2.setPassword(passwordEncoder.encode(testPassword));
            test2.setFirstName("Test");
            test2.setLastName1("User 2");
            test2.setEmail("test2@gestcode.test");
            test2.setRole(Role.USER);
            test2.setStatus(UserStatus.ACTIVE);
            test2.setEnabled(true);
            userRepository.save(test2);
            logger.info("Test2 user created successfully.");
        }
    }
}
