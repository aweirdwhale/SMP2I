package xyz.aweirdwhale.utils.exceptions;

/**
 * Lève les exception en cas d'échec de téléchargement.
 */

public class DownloadException extends Exception {

    public DownloadException() {
        super();
    }

    public DownloadException(String message) {
        super(message);
    }

    public DownloadException(Throwable cause) {
        super(cause);
    }

    public DownloadException(Throwable cause, String message) {
        super(message, cause);
    }

}
