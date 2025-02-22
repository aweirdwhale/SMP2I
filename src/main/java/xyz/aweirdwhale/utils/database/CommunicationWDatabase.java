package xyz.aweirdwhale.utils.database;

import xyz.aweirdwhale.utils.exceptions.CommunicationException;

import java.io.IOException;
import java.io.OutputStream;
import java.net.*;
import java.nio.charset.StandardCharsets;

public class CommunicationWDatabase {

    public static boolean loginRequest(String username, String hashed, String ip) throws CommunicationException {

        // Construct the JSON request body
        String requestBody = "{\"user\":\"" + username + "\",\"mdp\":\"" + hashed + "\"}";

        System.out.println("Request : " + requestBody);

        try {
            HttpURLConnection connection = getHttpURLConnection(ip, requestBody);

            // Set connection and read timeouts
            connection.setConnectTimeout(10000); // 10 seconds
            connection.setReadTimeout(10000); // 10 seconds

            // Read the server response
            int responseCode = connection.getResponseCode();
            System.out.println("Response Code: " + responseCode); // Debug

            // Handle the response code
            if (responseCode == HttpURLConnection.HTTP_OK) {
                return true;
            } else if (responseCode == HttpURLConnection.HTTP_NOT_FOUND) {
                System.out.println("404 Not Found");
                return false;
            } else if (responseCode == HttpURLConnection.HTTP_FORBIDDEN) {
                System.out.println("Mauvais identifiants");
                return false;
            }
            return false;

        } catch (MalformedURLException e) {
            throw new CommunicationException("Malformed URL: " + e.getMessage(), e);
        } catch (SocketTimeoutException e) {
            throw new CommunicationException("Connection timed out: " + e.getMessage(), e);
        } catch (IOException e) {
            throw new CommunicationException("Connection error: " + e.getMessage(), e);
        } catch (URISyntaxException e) {
            throw new CommunicationException(e);
        }
    }

    private static HttpURLConnection getHttpURLConnection(String ip, String requestBody) throws URISyntaxException, IOException {
        URI uri = new URI(ip);
        URL url = uri.toURL();

        System.out.println("URL : " + url); // URL OK SERVER OK

        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        // Configure the request
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setDoOutput(true);

        // Send the JSON
        try (OutputStream os = connection.getOutputStream()) {
            byte[] input = requestBody.getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
        }
        return connection;
    }
}