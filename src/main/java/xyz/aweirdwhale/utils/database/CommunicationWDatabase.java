package xyz.aweirdwhale.utils.database;

import xyz.aweirdwhale.utils.exceptions.CommunicationException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.*;
import java.nio.charset.StandardCharsets;

public class CommunicationWDatabase {

    public static boolean request(String username, String hashed, String ip) throws CommunicationException {

        // Construire le JSON correctement
        String requestBody = "{\"user\":\"" + username + "\",\"mdp\":\"" + hashed + "\"}";

        System.out.println("Request : " + requestBody);

        try {
            HttpURLConnection connection = getHttpURLConnection(ip, requestBody);

            // Lire la réponse du serveur
            int responseCode = connection.getResponseCode();
            System.out.println("Response Code: " + responseCode); // Debug

            // Réagir selon le code de réponse
            if (responseCode == HttpURLConnection.HTTP_OK) {
                return true;
            } else {
                return false;
            }

        } catch (MalformedURLException e) {
            throw new CommunicationException("URL mal formée : " + e.getMessage(), e);
        } catch (IOException e) {
            throw new CommunicationException("Erreur lors de la connexion : " + e.getMessage(), e);
        } catch (URISyntaxException e) {
            throw new CommunicationException(e);
        }
    }

    private static HttpURLConnection getHttpURLConnection(String ip, String requestBody) throws URISyntaxException, IOException {
        URI uri = new URI(ip);
        URL url = uri.toURL();

        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        // Configuration de la requête
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setDoOutput(true);

        // Envoyer le JSON
        try (OutputStream os = connection.getOutputStream()) {
            byte[] input = requestBody.getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
        }
        return connection;
    }
}
