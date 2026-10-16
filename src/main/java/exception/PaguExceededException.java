package exception;

public class PaguExceededException extends RuntimeException {
    public PaguExceededException(String message) {
        super(message);
    }

    public PaguExceededException(String message, Throwable cause) {
        super(message, cause);
    }
}
