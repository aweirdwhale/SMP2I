package xyz.aweirdwhale.utils.database;

import java.net.MalformedURLException;
import java.net.URL;

public class CommunicationWDatabase {
    public static void createRequest() {
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
            URL url = new URL(ip);

        } catch (
                MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }
    // send the request to the server
}
