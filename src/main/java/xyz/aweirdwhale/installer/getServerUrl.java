package xyz.aweirdwhale.installer;

import xyz.aweirdwhale.utils.exceptions.CommunicationException;
import xyz.aweirdwhale.utils.exceptions.ReadingException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

public class getServerUrl {

    // crée une variable accessible de n'importe où qui contient l'url du serveur
    // récupère la-dite url depuis un fichier texte sur https://raw.githubusercontent.com/aweirdwhale/SMP2I/refs/heads/hidden/server.txt
    // en cas de changement d'adresse intempéstif, il suffit de changer le contenu du fichier texte

    public static String SERVER;

    public static String getServerUrl() throws CommunicationException {
        // parse https://raw.githubusercontent.com/aweirdwhale/SMP2I/refs/heads/hidden/server.txt
        // et stocke le contenu dans SERVER
        StringBuilder content = new StringBuilder();

        try {
            URI uri = new URI("https://raw.githubusercontent.com/aweirdwhale/SMP2I/refs/heads/hidden/server.txt");
            URL url = uri.toURL();
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    content.append(line);
                }
            } catch (IOException e) {
                throw new ReadingException(e.getMessage());
            }

            SERVER = content.toString();

        } catch (URISyntaxException | CommunicationException | ReadingException | IOException e) {
            throw new CommunicationException(e);
        }

        return SERVER;
    }
}
