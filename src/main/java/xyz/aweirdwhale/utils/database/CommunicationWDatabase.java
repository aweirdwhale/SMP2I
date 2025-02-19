package xyz.aweirdwhale.utils.database;

import xyz.aweirdwhale.utils.exceptions.CommunicationException;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;

public class CommunicationWDatabase {
    public static void createRequest(String username, String hashed) throws CommunicationException {
        // send the credentials to the server
        /*
         * Request : {
         *    "user":"username",
         *    "mdp":"hash"
         *}
         */
        String requestBody = "{\"user\":\"" + username + "\"mdp\":" + hashed + "\"}";
        String ip = "https://localhost:3000/login";

        System.out.println("Request : " + requestBody);

        try {
            URI uri = URI.create(ip);
            URL url = uri.toURL();

        } catch (
                MalformedURLException e) {
            throw new CommunicationException("Erreur lors de la communication de : " + e.getMessage());
        }
    }
    // send the request to the server
}
