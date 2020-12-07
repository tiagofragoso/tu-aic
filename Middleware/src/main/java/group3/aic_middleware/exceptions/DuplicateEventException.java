package group3.aic_middleware.exceptions;

public class DuplicateEventException extends Exception {
    public DuplicateEventException(String errorMessage) {
        super(errorMessage);
    }
}
