package xyz.aweirdwhale;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import xyz.aweirdwhale.utils.security.HashPwd;

import java.net.MalformedURLException;
import java.net.URL;


public class Controller {
    @FXML private Text actionTarget;
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;

    @FXML
    public void handleConnection(ActionEvent actionEvent) {
        String username = usernameField.getText();
        String password = passwordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            System.out.println("Please fill in both fields.");
            return;
        }

        // hash the password
        String hashed = HashPwd.hash(password);
        System.out.println("Hashed password for " + username + " : " + hashed);





    }

    @FXML
    public void handlePlayButton(ActionEvent actionEvent) {
    }

    @FXML
    public void handleSettingsButton(ActionEvent actionEvent) {
    }

    @FXML
    public void handleLogin(ActionEvent actionEvent) {
    }

    @FXML
    public void handleChangeSkin(ActionEvent actionEvent) {
    }
}