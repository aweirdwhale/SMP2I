package xyz.aweirdwhale.utils.security;

import java.io.IOException;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.URISyntaxException;

import static xyz.aweirdwhale.utils.database.requestDatabase.getHttpURLConnection;

public class Login {

    /*
    * envoie une requette et selon la réponse, store le pseudo, la date de connection
    * return true si le jeu peut se lancer
    * return false sinon
    */


    private static String PORT = "6969";
    private static String TARGET = "/login";
    private static String SERVER = "http://ec2-13-60-48-128.eu-north-1.compute.amazonaws.com:"+PORT+TARGET;

    private static String METHOD = "POST";

    public static int login(String username, String hashed) throws IOException, URISyntaxException {
        String request_body = "{\"user\":\"" + username + "\",\"mdp\":\"" + hashed + "\"}";

        try {
            HttpURLConnection connection = getHttpURLConnection(SERVER, request_body, METHOD);
            int responseCode = connection.getResponseCode();

            return responseCode;
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }


        return -1;
    }
}
