package mp2i.sncf.utils.security;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URISyntaxException;

import static mp2i.sncf.utils.database.requestDatabase.getHttpURLConnection;

public class Login {

        /*
         * envoie une requette et selon la réponse, store le pseudo, la date de connection
         * return true si le jeu peut se lancer
         * return false sinon
         */


        public static int login(String username, String hashed, String server) {
            String request_body = "{\"user\":\"" + username + "\",\"mdp\":\"" + hashed + "\"}";

            try {
                String TARGET = "/login";
                String PORT = ":6969";
                String METHOD = "POST";
                HttpURLConnection connection = getHttpURLConnection(server + PORT + TARGET, request_body, METHOD);
                return connection.getResponseCode();
            } catch (IOException | URISyntaxException _) {
                System.out.println("⤫ Erreur de connexion (pas de wifi ou serv éteint demande @Aweirdwhale en cas de doute)");
            }

            return 0;
        }

}
