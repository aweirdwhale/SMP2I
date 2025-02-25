package xyz.aweirdwhale.controllers;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;

import java.util.logging.*;

public class LoginController {

    @FXML
    private TextArea logTextArea;

    @FXML
    public void initialize() {
        // Récupère le logger utilisé dans ton projet
        Logger appLogger = Logger.getLogger("xyz.aweirdwhale.utils.log.logger");
        // Crée et configure un handler pour rediriger les logs vers le TextArea
        TextAreaHandler handler = new TextAreaHandler(logTextArea);
        handler.setLevel(Level.ALL);
        handler.setFormatter(new SimpleFormatter());
        appLogger.addHandler(handler);

        // Optionnel : on peut aussi afficher un premier message
        appLogger.info("Lancement du launcher...");
    }

    /**
     * Handler personnalisé qui affiche les messages de log dans un TextArea.
     */
    private static class TextAreaHandler extends Handler {
        private final TextArea textArea;

        public TextAreaHandler(TextArea textArea) {
            this.textArea = textArea;
        }

        @Override
        public void publish(LogRecord record) {
            if (!isLoggable(record)) return;
            String message = getFormatter().format(record);
            // Mise à jour du TextArea sur le thread JavaFX
            Platform.runLater(() -> textArea.appendText(message + "\n"));
        }

        @Override
        public void flush() {
            // Pas nécessaire pour un TextArea
        }

        @Override
        public void close() throws SecurityException {
            // Aucun nettoyage particulier requis
        }
    }
}
