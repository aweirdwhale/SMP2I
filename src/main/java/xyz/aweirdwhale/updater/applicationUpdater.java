package xyz.aweirdwhale.updater;

import org.json.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URISyntaxException;

import static xyz.aweirdwhale.utils.database.requestDatabase.getHttpURLConnection;

public class applicationUpdater {
    /*
    * Au lancement, fait une requete à l'API pour voir si une nouvelle version est disponible
    * Si oui, affiche une popup pour demander si l'utilisateur veut mettre à jour
    * Si oui, télécharge le fichier de mise à jour et l'installe
    * Si non, continue le lancement de l'application
    */


    private static String TARGET = "/updater";
    private static String PORT = "6969";
    private static String SERVER = "http://ec2-13-60-48-128.eu-north-1.compute.amazonaws.com:"+PORT+TARGET;

    private static String METHOD = "POST";

    private String clientVersion;

    public static String getClientVersion() {
        // get the client version from json
        try (BufferedReader reader = new BufferedReader(new FileReader("src/main/java/xyz/aweirdwhale/utils/infos/version.json"))) {
            StringBuilder jsonContent = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                jsonContent.append(line);
            }
            JSONObject jsonObject = new JSONObject(jsonContent.toString());
            return jsonObject.getString("version");
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }



    public static int isUpdateable(String clientVersion) throws IOException, URISyntaxException {
        String request_body = "{\"message\":\"isUpdateAvaliable\",\"client_version\":\"" + clientVersion + "\"}";

        try {
            HttpURLConnection connection = getHttpURLConnection(SERVER, request_body, METHOD);
            int responseCode = connection.getResponseCode();
            String response = connection.getResponseMessage();

            return responseCode;
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }


        return -1;
    }

    public void updateApplication() throws IOException, URISyntaxException {
        if (isUpdateable(getClientVersion()) == 200) {
            // TODO quand on aura un lien de téléchargement
        }
        System.out.println("Everything is Up-To-Date");
    }

    public static void main(String[] args) {
    }

}
