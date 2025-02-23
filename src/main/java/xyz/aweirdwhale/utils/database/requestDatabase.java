package xyz.aweirdwhale.utils.database;

import xyz.aweirdwhale.utils.exceptions.CommunicationException;
import xyz.aweirdwhale.utils.exceptions.DatabaseException;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;


public class requestDatabase {

    /*
    * Use-case :
    * HttpURLConnection connection = getHttpURLConnection(ip, requestBody, methode);
    * connection.getResponseCode();
    *
    */

    private static Integer DELAY = 10000; // Maximum connection delay in ms


    /**
     * Connection à l'URL
     * @param address l'adresse à l'aquelle on se connecte
     * @param request requéte
     * @param httpMethode la méthode utiliser
     * @return la connection a l'URL
     * @throws URISyntaxException erreur
     * @throws IOException erreur
     */
    public static HttpURLConnection getHttpURLConnection(String address, String request, String httpMethode) throws URISyntaxException, IOException {
        // Handle URIs
        URI uri = new URI(address);
        URL url = uri.toURL();

        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        // Configure the request
        connection.setRequestMethod(httpMethode);
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setDoOutput(true);

        connection.setConnectTimeout(DELAY);
        connection.setReadTimeout(DELAY);

        // Send the JSON
        try (OutputStream os = connection.getOutputStream()) {
            byte[] input = request.getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
        } catch (IOException e) {
            throw new DatabaseException("Connexion timeout : " + e.getMessage());
        }
        return connection;
    }
}
