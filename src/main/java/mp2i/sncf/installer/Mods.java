package mp2i.sncf.installer;

import org.json.JSONArray;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import static mp2i.sncf.installer.Downloader.downloadFile;

public class Mods {
    private static final String SERVER = "http://217.154.9.109";
    private static final String PORT = ":6969";

    private static final String MODS_JSON_URL = SERVER + PORT + "/public/mods.json";


    public static void downloadMods(String modDir) {

        try {
            URL newUrl = new URL(MODS_JSON_URL);

            HttpURLConnection connection = (HttpURLConnection) newUrl.openConnection();
            connection.setRequestMethod("GET");

            if (connection.getResponseCode() == 200) {
                StringBuilder jsonContent = new StringBuilder();
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        jsonContent.append(line);
                    }
                }

                JSONArray mods = new JSONArray(jsonContent.toString());
                for (int i = 0; i < mods.length(); i++) {
                    String modUrl = mods.getString(i);
                    String modName = modUrl.substring(modUrl.lastIndexOf('/') + 1);
                    String modPath = modDir + "/" + modName;

                    downloadFile(modUrl, modPath);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
