package xyz.aweirdwhale.utils.database;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.*;

public class CommunicationWDatabase {
    public static String request(String username, String hashed, String ip) {
        // Construire le JSON correctement
        String requestBody = "{\"user\":\"" + username + "\",\"mdp\":\"" + hashed + "\"}";

        System.out.println("Request : " + requestBody);

        try {
            HttpURLConnection connection = getHttpURLConnection(ip, requestBody);

            // Lire la réponse du serveur
            int responseCode = connection.getResponseCode();
            System.out.println("Response Code: " + responseCode);

            // Lire le corps de la réponse
            StringBuilder response = new StringBuilder();
            try (BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(), "utf-8"))) {
                String responseLine;
                while ((responseLine = br.readLine()) != null) {
                    response.append(responseLine.trim());
                }
            }

            connection.disconnect();
            return response.toString(); // Retourne la réponse complète

        } catch (MalformedURLException e) {
            throw new RuntimeException("URL mal formée : " + e.getMessage(), e);
        } catch (IOException e) {
            throw new RuntimeException("Erreur lors de la connexion : " + e.getMessage(), e);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    private static HttpURLConnection getHttpURLConnection(String ip, String requestBody) throws URISyntaxException, IOException {
        URI uri = new URI(ip);
        URL url = uri.toURL();

        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        // Configuration de la requête
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestProperty("Accept", "application/json");
        connection.setDoOutput(true);

        // Envoyer le JSON
        try (OutputStream os = connection.getOutputStream()) {
            byte[] input = requestBody.getBytes("utf-8");
            os.write(input, 0, input.length);
        }
        return connection;
    }
}
