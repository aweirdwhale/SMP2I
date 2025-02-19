package xyz.aweirdwhale.utils.exceptions;

public class SecurityException extends RuntimeException {

    public SecurityException() {
        super();
    }

    public SecurityException(String message) {

        super(message);
    }

    public SecurityException(Throwable cause) {
        super(cause);
    }

    public SecurityException(String message, Throwable cause) {
        super(message, cause);
    }
}
