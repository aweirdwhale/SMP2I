package xyz.aweirdwhale.utils.exceptions;

public class CommunicationException extends RuntimeException {

    public CommunicationException() {
        super();
    }

    public CommunicationException(Throwable cause) {
        super(cause);
    }

    public CommunicationException(String message) {
        super(message);
    }

    public CommunicationException(String message, Throwable cause) {
        super(message, cause);
    }
}
