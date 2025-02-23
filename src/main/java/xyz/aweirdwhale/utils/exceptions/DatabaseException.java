package xyz.aweirdwhale.utils.exceptions;

public class DatabaseException extends RuntimeException {

    public DatabaseException() { super();}

    public DatabaseException(Throwable cause) { super(cause); }

    public DatabaseException(String message) {
        super(message);
    }

    public DatabaseException(String message, Throwable cause) { super(message, cause); }
}
