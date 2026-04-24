package gestcode.server.exception;

/**
 * Excepció que es llança quan un usuari excedeix el nombre màxim de préstecs
 * permesos.
 * 
 * @author Jordi Verdalet Carrera
 */
public class MaxLoansExceededException extends RuntimeException {
    public MaxLoansExceededException(String message) {
        super(message);
    }
}
