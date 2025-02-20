package xyz.aweirdwhale.controllers;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class ErrorPopUpController {
        @FXML
        private Label errorMessage;

        public void setErrorMessage(String err) {
            errorMessage.setText("Erreur : " + err);
        }

        @FXML
        private void handleOkButton() {
            // Close the pop-up window
            Stage stage = (Stage) errorMessage.getScene().getWindow();
            stage.close();
        }
}
