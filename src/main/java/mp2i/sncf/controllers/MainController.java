package mp2i.sncf.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import mp2i.sncf.Launcher;
import mp2i.sncf.utils.security.HashPwd;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;

import static mp2i.sncf.utils.security.Login.login;


public class MainController {

    public Button loginButton;
    public Button changeSkinButton;
    public Button settings;

    @FXML
    private Label loginInfoLabel;
    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;

    private static final String SERVER = "http://217.154.9.109";
    public static final String PORT = "6969";


    // Feedback bof mais c'est au moins ça
    public void setInfoLabel(String info, String color) {
        loginInfoLabel.setText(info);
        loginInfoLabel.setStyle("-fx-text-fill: " + color + ";");
    }


    // enleve les espaces avant et apres le nom d'utilisateur
    public String usernamePostProcessing(String username) {
        return username.trim();
    }

    /**
     * S'occupe de connecter le joeur vers le serveur demandé avec c'est info. Gère les différents problèmes.
     *
     * @param actionEvent l'action effectuer (never use)
     */
    @FXML
    public void handleConnection(ActionEvent actionEvent) throws NoSuchAlgorithmException {
        // get creds
        String username = usernameField.getText();
        username = usernamePostProcessing(username);
        String password = passwordField.getText();


        if (username.isEmpty() || password.isEmpty()) {
            System.out.println("Please fill in both fields.");
            setInfoLabel("⚠ Veuillez remplir les deux champs.", "red");
            return;
        }

        // hash password
        String hashed = HashPwd.hash(password);


        int res; // init la réponse (http status code)
        try {
            System.out.println(SERVER);
            res = login(username, hashed, SERVER);

        } finally {

        }

        System.out.println(res);
        if (res == 200) {


            setInfoLabel("Mise en place de l'environnement..", "green");

            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/mp2i/sncf//loggin.fxml"));
                Parent logerrRoot = loader.load();

                Stage stage = (Stage) settings.getScene().getWindow();

                stage.setTitle("SMP2I Log");
                stage.setScene(new Scene(logerrRoot));
                stage.show();

            } catch (IOException _) {
            }



            // Play ????
            Launcher.setUp(username);


        } else {
            setInfoLabel("Attention ! Mauvais identifiants.", "red");
        }
    }

    @FXML
    public void handleChangeSkin(ActionEvent actionEvent) {
        // TODO : Changer la skin du joueur (aucune idée de comment pour le moment)
    }

    @FXML
    public void handlesettings(ActionEvent actionEvent)  {
        try {
            // Charge le fichier FXML de l'écran de configuration
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/mp2i/sncf/settings.fxml"));
            Parent settingsRoot = loader.load();

            Scene scene = new Scene(settingsRoot);
            // Assurez-vous que le fichier CSS est bien placé dans src/main/resources/styles/settings.css


            scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/mp2i/sncf/styles/settings.css")).toExternalForm());
            // On peut éventuellement définir un fond global ici, mais c'est mieux de le faire via le CSS
            scene.getRoot().setStyle("-fx-background-color: #222222;");

            // Remplace la scène de la fenêtre par la nouvelle
            Stage stage = (Stage) settings.getScene().getWindow();
            stage.setTitle("SMP2I Settings");
            stage.setScene(scene);
            stage.show();
        } catch (IOException _) {
        }
    }
}