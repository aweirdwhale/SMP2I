package xyz.aweirdwhale.utils.exceptions;

/**
 * Lève les exceptions en cas d'échec Authentication.
 */


public class AuthException extends Exception {

    public AuthException() {
        super();
    }

    public AuthException(String message) {
        super(message);
    }

    public AuthException(Throwable cause) {
        super(cause);
    }



    public AuthException(Throwable cause, String message) {
        super(message, cause);
    }

}
