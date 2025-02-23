package xyz.aweirdwhale.installer;

import org.json.JSONArray;
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
     * in gamedir/versions/
     * **/

    private static final String SERVER = "http://"+getServerUrl.SERVER;
    private static final String PORT = ":6969";
    private static final String FABRIC_URL = SERVER + PORT + "/public/fabric-loader/fabric.jar";
    private static final String FABRIC_JSON_URL = SERVER + PORT + "/public/fabric-loader/fabric.json";
    private static final String MINECRAFT_URL = SERVER + PORT + "/public/1.21.4/1.21.4.jar";
    private static final String MINECRAFT_JSON_URL = SERVER + PORT + "/public/1.21.4/1.21.4.json";
    private static final String MODS_JSON_URL = SERVER + PORT + "/public/mods.json";


    /**
     * s'occupe de telecharger la version requise du jeu sous fabric.
     *
     * @param path chemin demandé de l'installation
     * @throws DownloadException erreur de téléchargement
     */
    public static void downloadVersions(String path) throws DownloadException {
        // On suppose que la dir path/versions/ existe
        downloadFile(FABRIC_URL, path + "/versions/fabric-loader/fabric.jar");
        downloadFile(FABRIC_JSON_URL, path + "/versions/fabric-loader/fabric.json");

        downloadFile(MINECRAFT_URL, path + "/versions/1.21.4/1.21.4.jar");
        downloadFile(MINECRAFT_JSON_URL, path + "/versions/1.21.4/1.21.4.json");
    }

    /**
     * Télécharge les différente librairie essentiel.
     *
     * @param path chemin acccés (never use)
     * @throws DownloadException erreur
     */
    public static void downloadLibs(String path) throws DownloadException {
        // On suppose que la dir path/libraries/ existe
        // carrément le script a une classe solo
        LibraryInstaller installer = new LibraryInstaller();
        installer.run(path);
    }


    /**
     * telecharge tout les modes prédéfinies et crée le répertoire.
     *
     * @param path chemin
     * @throws DownloadException erreur
     */
    public static void downloadMods(String path) throws DownloadException {
        String modsDir = path + "/mods";
        File modsDirFile = new File(modsDir);
        if (!modsDirFile.exists()) {
            modsDirFile.mkdirs();
        }

        try {

            URL url = new URL(MODS_JSON_URL); // uri
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
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
                    String modPath = modsDir + "/" + modName;
                    logger.logInfo("Downloading mod " + modName);
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
     * @param savePath Le chemin où le fichier sera sauvegardé.
     * @throws DownloadException En cas d'erreur de téléchargement.
     */
    public static void downloadFile(String fileUrl, String savePath) throws DownloadException {
        File file = new File(savePath);
        if (file.exists()) {
            logger.logInfo("✔ File already exists: " + savePath + ", skipping download.");
            return;
        }

        try {
            URI uri = URI.create(fileUrl);
            URL url = uri.toURL();

            logger.logInfo("[-] Downloading " + fileUrl + " to " + savePath);

            try (ReadableByteChannel channel = Channels.newChannel(url.openStream());
                 FileOutputStream fos = new FileOutputStream(savePath)) {
                fos.getChannel().transferFrom(channel, 0, Long.MAX_VALUE);
            }

            logger.logInfo("[x] Downloaded " + fileUrl + " to " + savePath);

        } catch (IOException e) {
            logger.logError("Ran into an issue while downloading " + fileUrl + " : " + e.getMessage(), e);
            throw new DownloadException("Erreur lors du téléchargement de " + fileUrl + " : " + e.getMessage());
        }
    }
}