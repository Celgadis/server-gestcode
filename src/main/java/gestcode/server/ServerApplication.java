package gestcode.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Classe principal de l'aplicació Spring Boot per a la gestió de la biblioteca.
 *
 * @author Jordi Verdalet Carrera
 */
@SpringBootApplication
@EnableScheduling
public class ServerApplication {

	/**
	 * Mètode principal que arranca l'aplicació Spring Boot.
	 *
	 * @param args Arguments de línia de comandes.
	 * @author Jordi Verdalet Carrera
	 */
	public static void main(String[] args) {
		SpringApplication.run(ServerApplication.class, args);
	}

}
