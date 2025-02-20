package xyz.aweirdwhale.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import xyz.aweirdwhale.utils.exceptions.CommunicationException;
import xyz.aweirdwhale.utils.exceptions.DownloadException;
import xyz.aweirdwhale.utils.exceptions.LaunchException;
import xyz.aweirdwhale.utils.security.HashPwd;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static xyz.aweirdwhale.Launcher.launchMinecraft;
import static xyz.aweirdwhale.download.Downloader.installMinecraftandMod;
import static xyz.aweirdwhale.utils.database.CommunicationWDatabase.request;


public class MainController {
    public Button loginButton;
    public Button changeSkinButton;
    @FXML private Label loginInfoLabel;
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;

    public void setInfoLabel(String info, String color){
        loginInfoLabel.setText(info);
        loginInfoLabel.setStyle("-fx-text-fill: " + color + ";");
    }

    public String usernamePostProcessing(String username){
        // Supprimer les espaces
        return username.trim();
    }

    @FXML
    public void handleConnection(ActionEvent actionEvent) throws DownloadException, LaunchException {
        String username = usernameField.getText();
        username = usernamePostProcessing(username);
        String password = passwordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            System.out.println("Please fill in both fields.");
            setInfoLabel("⚠ Veuillez remplir les deux champs.", "red");
            return;
        }

        // hash the password
        String hashed = HashPwd.hash(password);

        // 404 ???
        String ip = "http://127.0.0.1:3000/login"; // IP à modifier quand le serv sera en ligne

        List<String> mods = Arrays.asList("https://cdn.modrinth.com/data/PtjYWJkn/versions/f4TfteNb/sodium-extra-fabric-0.6.1%2Bmc1.21.4.jar", "https://cdn.modrinth.com/data/gvQqBUqZ/versions/kLc5Oxr4/lithium-fabric-0.14.8%2Bmc1.21.4.jar");

        System.out.println("Hashed password for " + username + " : " + hashed);

        // send the credentials to the server
        boolean res = false; // initialiser la réponse, pour info c'est la valeur de base.
        try {
            res = request(username, hashed, ip);
        } catch (CommunicationException e) {
            setInfoLabel("⚠ Erreur de communication avec le serveur.", "red");
            throw new CommunicationException(e);
        }
        System.out.println(res); // true = on peut lancer, false = non
        if (res) {
            setInfoLabel("Connexion ...", "green");
            installMinecraftandMod("Minecraft", "");
            launchMinecraft( "",  "",  "Main", mods, "SMP2I", ip);
            // TODO : Lancer le jeu avec mods + pseudo du joueur
            // Server : localhost:25565
        } else {
            setInfoLabel("Attention ! Mauvais identifiants.", "red");
        }

    }

    @FXML
    public void handleChangeSkin(ActionEvent actionEvent) {
        // TODO : Changer la skin du joueur (aucune idée de comment pour le moment)
    }
}