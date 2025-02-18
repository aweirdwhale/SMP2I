package xyz.aweirdwhale.utils.exceptions;

/**
 * Lève les exceptions en cas d'échec du lancement du jeu par le launcher.
 */

public class LaunchException extends Exception {

    public LaunchException () {
        super();
    }

    public LaunchException(String message) {
        super(message);
    }

    public LaunchException(Throwable cause) {
        super(cause);
    }

    public LaunchException (Throwable cause, String message) {
        super(message, cause);
    }

}
