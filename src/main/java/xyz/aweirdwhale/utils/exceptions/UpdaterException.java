package xyz.aweirdwhale.utils.exceptions;

public class UpdaterException extends RuntimeException {

    public UpdaterException() {
        super();
    }

    public UpdaterException(String messsage, Throwable cause) { super(messsage, cause); }


    public UpdaterException(String message) {
        super(message);
    }
}
