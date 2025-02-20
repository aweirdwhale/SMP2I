package xyz.aweirdwhale.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import xyz.aweirdwhale.utils.security.HashPwd;

import static xyz.aweirdwhale.utils.database.CommunicationWDatabase.request;


public class Controller {
    public Button loginButton;
    public Button changeSkinButton;
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

        // 404 ???
        String ip = "http://127.0.0.1:3000/login"; // IP à modifier quand le serv sera en ligne

        System.out.println("Hashed password for " + username + " : " + hashed);

        // send the credentials to the server
        boolean res = request(username, hashed, ip);
        System.out.println(res); // true = on peut lancer, false = non
        if (res) {
            // TODO : Lancer le jeu avec mods + pseudo du joueur
            // TODO : update l'UI pour que le joueur sache que ça marche
        } else {
            // TODO : update l'UI pour dire au joueur que ça marche pas
        }

    }

    @FXML
    public void handleChangeSkin(ActionEvent actionEvent) {
        // TODO : Changer la skin du joueur (aucune idée de comment pour le moment)
    }
}