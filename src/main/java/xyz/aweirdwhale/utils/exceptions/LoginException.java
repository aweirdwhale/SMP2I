package xyz.aweirdwhale.utils.exceptions;

/**
 * Lève les exceptions en cas d'échec Authentication.
 */


public class LoginException extends Exception {

    public LoginException() {
        super();
    }

    public LoginException(String message) {
        super(message);
    }

    public LoginException(Throwable cause) {
        super(cause);
    }

    public LoginException(Throwable cause, String message) {
        super(message, cause);
    }

}
