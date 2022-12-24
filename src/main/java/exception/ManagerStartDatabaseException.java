package exception;

public class ManagerStartDatabaseException extends RuntimeException {
    public ManagerStartDatabaseException(String message) {
        super(message);
    }
}