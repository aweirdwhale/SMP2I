package xyz.aweirdwhale.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import xyz.aweirdwhale.Launcher;

import xyz.aweirdwhale.installer.ClassPathGenerator;
import xyz.aweirdwhale.installer.Downloader;
import xyz.aweirdwhale.installer.setupEnvironment;
import xyz.aweirdwhale.utils.exceptions.CommunicationException;
import xyz.aweirdwhale.utils.exceptions.DownloadException;
import xyz.aweirdwhale.utils.exceptions.LaunchException;
import xyz.aweirdwhale.utils.log.logger;
import xyz.aweirdwhale.utils.security.HashPwd;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Arrays;

import java.util.List;

import static xyz.aweirdwhale.utils.security.Login.login;

public class MainController {
    public Button loginButton;
    public Button changeSkinButton;
    @FXML private Label loginInfoLabel;
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;

    public static final String SERVER = "http://ec2-13-49-57-24.eu-north-1.compute.amazonaws.com";
    public static final String PORT = "6969";

    /**
     * Assuming the existance of server/public/mods.json
     * **/
//    private static final String[] MOD_URLS = {
//            "https://cdn.modrinth.com/data/PtjYWJkn/versions/f4TfteNb/sodium-extra-fabric-0.6.1%2Bmc1.21.4.jar"
//    };




    // Feedback bof mais c'est au moins ça
    public void setInfoLabel(String info, String color){
        loginInfoLabel.setText(info);
        loginInfoLabel.setStyle("-fx-text-fill: " + color + ";");
    }


    // enleve les espaces avant et apres le nom d'utilisateur
    public String usernamePostProcessing(String username){
        return username.trim();
    }


    @FXML
    public void handleConnection(ActionEvent actionEvent) {
        // get creds
        String username = usernameField.getText();
        username = usernamePostProcessing(username);
        String password = passwordField.getText();


        if (username.isEmpty() || password.isEmpty()) {
            System.out.println("Please fill in both fields.");
            setInfoLabel("⚠ Veuillez remplir les deux champs.", "red");
            logger.logWarning("⚠ One or more field empty : can't login.");
            return;
        }

        // hash password
        String hashed = HashPwd.hash(password);



        int res = -1; // init la réponse (http status code)
        try {
            res = login(username, hashed, SERVER);
        } catch (CommunicationException e) {
            setInfoLabel("⚠ Erreur de communication avec le serveur.", "red");
            logger.logError("⚠ Communication error with server : " + e.getMessage(), e);
            e.printStackTrace();
            return;
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }

        System.out.println(res);
        if (res==200) {
            setInfoLabel("Mise en place de l'environnement..", "green");
            logger.logInfo("Connection successful.");

            // Play ????
            Launcher.setUp(username);

        } else {
            setInfoLabel("Attention ! Mauvais identifiants.", "red");
            logger.logWarning("⚠ Wrong credentials.");
        }
    }

    @FXML
    public void handleChangeSkin(ActionEvent actionEvent) {
        // TODO : Changer la skin du joueur (aucune idée de comment pour le moment)
    }
}