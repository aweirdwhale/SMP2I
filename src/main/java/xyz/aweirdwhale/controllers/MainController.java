package xyz.aweirdwhale.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import xyz.aweirdwhale.Launcher;
import xyz.aweirdwhale.download.Downloader;
import xyz.aweirdwhale.utils.exceptions.CommunicationException;
import xyz.aweirdwhale.utils.exceptions.DownloadException;
import xyz.aweirdwhale.utils.exceptions.LaunchException;
import xyz.aweirdwhale.utils.security.HashPwd;

import java.util.Arrays;
import java.util.List;

import static xyz.aweirdwhale.utils.database.CommunicationWDatabase.request;

public class MainController {
    public Button loginButton;
    public Button changeSkinButton;
    @FXML private Label loginInfoLabel;
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;

    private static final String[] MOD_URLS = {
            "https://example.com/mods/lithium.jar",
            "https://example.com/mods/sodium.jar",
            "https://example.com/mods/phosphor.jar"
    };

    private static final String MINECRAFT_URL = "https://example.com/minecraft/minecraft_1.21.4.jar";

    public void setInfoLabel(String info, String color){
        loginInfoLabel.setText(info);
        loginInfoLabel.setStyle("-fx-text-fill: " + color + ";");
    }

    public String usernamePostProcessing(String username){
        return username.trim();
    }

    @FXML
    public void handleConnection(ActionEvent actionEvent) {
        String username = usernameField.getText();
        username = usernamePostProcessing(username);
        String password = passwordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            System.out.println("Please fill in both fields.");
            setInfoLabel("⚠ Veuillez remplir les deux champs.", "red");
            return;
        }

        String hashed = HashPwd.hash(password);
        String ip = "http://ec2-13-60-48-128.eu-north-1.compute.amazonaws.com:3000/login";

        System.out.println("Hashed password for " + username + " : " + hashed);

        boolean res = false;
        try {
            res = request(username, hashed, ip);
        } catch (CommunicationException e) {
            setInfoLabel("⚠ Erreur de communication avec le serveur.", "red");
            throw new CommunicationException(e);
        }
        System.out.println(res);
        if (res) {
            setInfoLabel("Connexion ...", "green");

            String homeDir = System.getProperty("user.home");
            String gameDirectory = homeDir + "/.smp2i";
            String modsDirectory = gameDirectory + "/mods";
            String assetsDirectory = gameDirectory + "/assets";

            try {
                Downloader.downloadMod(MOD_URLS, modsDirectory);
                Downloader.installMinecraftandMod(MINECRAFT_URL, gameDirectory);
            } catch (DownloadException e) {
                setInfoLabel("⚠ Erreur lors du téléchargement des fichiers.", "red");
                e.printStackTrace();
                return;
            }

            List<String> jars = Arrays.asList(
                    gameDirectory + "/minecraft_1.21.4.jar",
                    modsDirectory + "/lithium.jar",
                    modsDirectory + "/sodium.jar",
                    modsDirectory + "/phosphor.jar"
            );

            try {
                Launcher.launchMinecraft(gameDirectory, assetsDirectory, "net.minecraft.client.main.Main", jars, "127.0.0.1", "25565", username);
            } catch (LaunchException e) {
                setInfoLabel("⚠ Erreur lors du lancement du jeu.", "red");
                e.printStackTrace();
            }
        } else {
            setInfoLabel("Attention ! Mauvais identifiants.", "red");
        }
    }

    @FXML
    public void handleChangeSkin(ActionEvent actionEvent) {
        // TODO : Changer la skin du joueur (aucune idée de comment pour le moment)
    }
}