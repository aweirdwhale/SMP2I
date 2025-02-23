package xyz.aweirdwhale.installer;

import org.json.JSONArray;
import org.json.JSONObject;
import xyz.aweirdwhale.utils.exceptions.DownloadException;
import xyz.aweirdwhale.utils.log.logger;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

public class Downloader {
    /**
     * Downloads minecraft and fabric jar files
     * in gamedir/versions
     * **/

    private static String SERVER = "http://ec2-13-49-57-24.eu-north-1.compute.amazonaws.com";
    private static String PORT = "6969";
    private static final String FABRIC_URL = SERVER +PORT+ "/public/fabric-loader/fabric.jar";
    private static final String FABRIC_JSON_URL = SERVER +PORT+ "/public/fabric-loader/fabric.json";
    private static final String MINECRAFT_URL = SERVER +PORT+ "/public/1.21.4/1.21.4.jar";
    private static final String MINECRAFT_JSON_URL = SERVER +PORT+ "/public/1.21.4/1.21.4.json";


    public static void downloadVersions(String path) throws DownloadException {
        // On suppose que la dir path/versions/ existe
        downloadFile(FABRIC_URL, path + "/versions/fabric.jar");
        downloadFile(FABRIC_JSON_URL, path + "/versions/fabric.json");

        downloadFile(MINECRAFT_URL, path + "/versions/1.21.4.jar");
        downloadFile(MINECRAFT_JSON_URL, path + "/versions/1.21.4.json");
    }

    public static void downloadLibs(String path) throws DownloadException {
        // On suppose que la dir path/libraries/ existe
        // carrément le script a une classe solo
        LibraryInstaller installer = new LibraryInstaller();
        installer.run();
    }

    private static final String MODS_JSON_URL = "server/public/mods.json";

    public static void downloadMods(String path) throws DownloadException {
        String modsDir = path + "/mods";
        File modsDirFile = new File(modsDir);
        if (!modsDirFile.exists()) {
            modsDirFile.mkdirs();
        }

        try {
            URL url = new URL(MODS_JSON_URL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            if (connection.getResponseCode() == 200) {
                StringBuilder jsonContent = new StringBuilder();
                try (BufferedReader reader = new BufferedReader(new FileReader(url.getPath()))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        jsonContent.append(line);
                    }
                }

                JSONObject data = new JSONObject(jsonContent.toString());
                JSONArray mods = data.getJSONArray("mods");

                for (int i = 0; i < mods.length(); i++) {
                    JSONObject mod = mods.getJSONObject(i);
                    String modUrl = mod.getString("url");
                    String modPath = modsDir + "/" + mod.getString("path");
                    downloadFile(modUrl, modPath);
                }
            } else {
                logger.logError("❌ Error (" + connection.getResponseCode() + ") while fetching mods.json", null);
            }
        } catch (IOException e) {
            logger.logError("Error while downloading mods: " + e.getMessage(), e);
            throw new DownloadException("Error while downloading mods: " + e.getMessage());
        }
    }

    /**
     * Télécharge un fichier depuis l'URL spécifiée et le sauvegarde dans le chemin donné.
     *
     * @param fileUrl  L'URL du fichier à télécharger.
     * @param SavePath Le chemin où le fichier sera sauvegardé.
     * @throws DownloadException En cas d'erreur de téléchargement.
     */
    public static void downloadFile(String fileUrl, String SavePath) throws DownloadException {
        try {
            URI uri = URI.create(fileUrl);
            URL url = uri.toURL();

            logger.logInfo("[-] Downloading " + fileUrl + " to " + SavePath);

            try (ReadableByteChannel channel = Channels.newChannel(url.openStream());
                 FileOutputStream fos = new FileOutputStream(SavePath)) {
                fos.getChannel().transferFrom(channel, 0, Long.MAX_VALUE);
            }

            logger.logInfo("[x] Downloaded " + fileUrl + " to " + SavePath);


        } catch (IOException e) {
            logger.logError("Ran into an issue while downloading " + fileUrl + " : " + e.getMessage(), e);
            throw new DownloadException("Erreur lors du téléchargement de " + fileUrl + " : " + e.getMessage());

        }
}
  }