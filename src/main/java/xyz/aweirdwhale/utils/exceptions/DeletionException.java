package xyz.aweirdwhale.utils.exceptions;

import java.io.IOException;

public class DeletionException extends IOException {
    public DeletionException(String message) {
        super(message);
    }
}
