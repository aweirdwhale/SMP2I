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

import java.util.Collections;
import java.util.List;

import static xyz.aweirdwhale.Launcher.launchMinecraft;
import static xyz.aweirdwhale.download.Downloader.installMinecraftandMod;
import static xyz.aweirdwhale.utils.database.CommunicationWDatabase.request;


import static xyz.aweirdwhale.utils.database.CommunicationWDatabase.request;

public class MainController {
    public Button loginButton;
    public Button changeSkinButton;
    @FXML private Label loginInfoLabel;
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;

    public static final String server = "http://ec2-13-60-48-128.eu-north-1.compute.amazonaws.com:6969/";

    // TODO GET URLS FROM SERVER
    private static final String[] MOD_URLS = {
            "https://cdn.modrinth.com/data/PtjYWJkn/versions/f4TfteNb/sodium-extra-fabric-0.6.1%2Bmc1.21.4.jar"
    };

    // TODO UPLOAD MINECRAFT TO SERVER + DL IT
    private static final String MINECRAFT_URL = server + "download";

    public void setInfoLabel(String info, String color){
        loginInfoLabel.setText(info);
        loginInfoLabel.setStyle("-fx-text-fill: " + color + ";");
    }

    public String usernamePostProcessing(String username){
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

        String hashed = HashPwd.hash(password);
        String ip = server + "login";

        //List<String> mods = Arrays.asList("https://cdn.modrinth.com/data/PtjYWJkn/versions/f4TfteNb/sodium-extra-fabric-0.6.1%2Bmc1.21.4.jar", "https://cdn.modrinth.com/data/gvQqBUqZ/versions/kLc5Oxr4/lithium-fabric-0.14.8%2Bmc1.21.4.jar");

        System.out.println("Hashed password for " + username + " : " + hashed);

        // send the credentials to the server
        boolean res = false; // initialiser la réponse, pour info c'est la valeur de base.
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
                System.out.println("téléchargement des fichiers.");

                Downloader.downloadMod(MOD_URLS, modsDirectory);
                System.out.println("Installation du jeu.");

                Downloader.installMinecraftandMod(MINECRAFT_URL, gameDirectory);
                System.out.println("OK.");

            } catch (DownloadException e) {
                System.out.println("téléchargement des fichiers. ERR");
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
                System.out.println("lancement du jeu.");

                Launcher.launchMinecraft(gameDirectory, assetsDirectory, "net.minecraft.client.main.Main", jars, "13.60.48.128", "25565", username);
            } catch (LaunchException e) {
                System.out.println("ERR : lancement du jeu");

                setInfoLabel("⚠ Erreur lors du lancement du jeu.", "red");
                e.printStackTrace();
            }
            installMinecraftandMod("Minecraft", "");

        } else {
            setInfoLabel("Attention ! Mauvais identifiants.", "red");
        }
    }

    @FXML
    public void handleChangeSkin(ActionEvent actionEvent) {
        // TODO : Changer la skin du joueur (aucune idée de comment pour le moment)
    }
}