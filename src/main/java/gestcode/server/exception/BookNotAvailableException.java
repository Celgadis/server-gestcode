package gestcode.server.exception;

/**
 * Excepció que es llança quan un llibre no està disponible per al préstec.
 * 
 * @author Jordi Verdalet Carrera
 */
public class BookNotAvailableException extends RuntimeException {
    public BookNotAvailableException(String message) {
        super(message);
    }
}
