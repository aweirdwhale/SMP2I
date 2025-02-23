package xyz.aweirdwhale.utils.security;

import xyz.aweirdwhale.utils.exceptions.LoginException;
import xyz.aweirdwhale.utils.log.logger;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URISyntaxException;

import static xyz.aweirdwhale.utils.database.requestDatabase.getHttpURLConnection;

public class Login {

    /*
    * envoie une requette et selon la réponse, store le pseudo, la date de connection
    * return true si le jeu peut se lancer
    * return false sinon
    */


    public static int login(String username, String hashed, String server) throws LoginException {
        String request_body = "{\"user\":\"" + username + "\",\"mdp\":\"" + hashed + "\"}";

        try {
            String TARGET = "/login";
            String PORT = ":6969";
            String METHOD = "POST";
            HttpURLConnection connection = getHttpURLConnection(server+ PORT + TARGET, request_body, METHOD);
            int responseCode = connection.getResponseCode();
            logger.logInfo("Login response : " + responseCode);
            return responseCode;
        } catch (IOException | URISyntaxException e) {
            throw new LoginException(e.getMessage());
        }

    }
}
