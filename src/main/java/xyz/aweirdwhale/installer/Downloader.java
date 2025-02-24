package xyz.aweirdwhale.installer;

import org.json.JSONObject;
import org.json.JSONArray;
import org.json.JSONException;
import xyz.aweirdwhale.utils.exceptions.DownloadException;
import xyz.aweirdwhale.utils.log.logger;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Iterator;

public class Downloader {

    private static final String SERVER = "https://" + getServerUrl.SERVER;
    private static final String PORT = ":6969";
    private static final String FABRIC_URL = SERVER + PORT + "/public/fabric-loader/fabric.jar";
    private static final String FABRIC_JSON_URL = SERVER + PORT + "/public/fabric-loader/fabric.json";
    private static final String MINECRAFT_URL = SERVER + PORT + "/public/1.21.4/1.21.4.jar";
    private static final String MINECRAFT_JSON_URL = SERVER + PORT + "/public/1.21.4/1.21.4.json";
    private static final String MODS_JSON_URL = SERVER + PORT + "/public/mods.json";

    // URL de base pour télécharger les assets (modifiez si votre serveur privé est différent)
    private static final String ASSET_BASE_URL = "https://resources.download.minecraft.net";

    /**
     * Télécharge la version requise du jeu sous Fabric.
     */
    public static void downloadVersions(String path) throws DownloadException {

        downloadFile(FABRIC_URL, path + "/versions/fabric-loader/fabric.jar");
        downloadFile(FABRIC_JSON_URL, path + "/versions/fabric-loader/fabric.json");

        downloadFile(MINECRAFT_URL, path + "/versions/1.21.4/1.21.4.jar");
        downloadFile(MINECRAFT_JSON_URL, path + "/versions/1.21.4/1.21.4.json");
    }

    /**
     * Télécharge les librairies essentielles.
     */
    public static void downloadLibs(String path) throws DownloadException {
        LibraryInstaller installer = new LibraryInstaller();
        installer.run(path);
    }

    /**
     * Télécharge les mods prédéfinis et crée le répertoire.
     */
    public static void downloadMods(String path) throws DownloadException {
        String modsDir = path + "/mods";
        File modsDirFile = new File(modsDir);
        if (!modsDirFile.exists()) {
            modsDirFile.mkdirs();
        }

        try {
            URI uri = new URI(MODS_JSON_URL);
            URL url = uri.toURL();
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
        } catch (IOException | URISyntaxException e ) {
            logger.logError("Error while downloading mods: " + e.getMessage(), e);
            throw new DownloadException("Error while downloading mods: " + e.getMessage());
        }
    }

    /**
     * Télécharge un fichier depuis l'URL spécifiée et le sauvegarde dans le chemin donné.
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
            // Crée les dossiers nécessaires
            File parentDir = file.getParentFile();
            if (!parentDir.exists()) {
                parentDir.mkdirs();
            }
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

    /**
     * Télécharge les assets de Minecraft en se basant sur le fichier version JSON.
     * @param path chemin de base de l'installation
     * @throws DownloadException en cas d'erreur de téléchargement
     */
    public static void downloadAssets(String path) throws DownloadException {
        String versionJsonPath = path + "/versions/1.21.4/1.21.4.json";
        File versionJsonFile = new File(versionJsonPath);
        if (!versionJsonFile.exists()) {
            throw new DownloadException("Le fichier version JSON n'existe pas : " + versionJsonPath);
        }

        try {
            // Lire le contenu du fichier version JSON
            String versionJsonContent = new String(Files.readAllBytes(Paths.get(versionJsonPath)));
            JSONObject versionJson = new JSONObject(versionJsonContent);

            // Récupérer l'objet assetIndex
            JSONObject assetIndex = versionJson.getJSONObject("assetIndex");
            String assetIndexUrl = assetIndex.getString("url");
            String assetIndexId = assetIndex.getString("id");

            logger.logInfo("Asset index URL: " + assetIndexUrl);

            // Télécharger l'asset index dans un répertoire temporaire
            String assetsDir = path + "/assets/indexes";
            File assetsDirFile = new File(assetsDir);
            if (!assetsDirFile.exists()) {
                assetsDirFile.mkdirs();
            }
            String assetIndexFilePath = assetsDir + "/" + assetIndexId + ".json";
            downloadFile(assetIndexUrl, assetIndexFilePath);

            // Lire et parser l'asset index
            String assetIndexContent = new String(Files.readAllBytes(Paths.get(assetIndexFilePath)));
            JSONObject assetIndexJson = new JSONObject(assetIndexContent);
            JSONObject objects = assetIndexJson.getJSONObject("objects");

            // Télécharger chaque asset
            String objectsDirPath = path + "/assets/objects";
            File objectsDir = new File(objectsDirPath);
            if (!objectsDir.exists()) {
                objectsDir.mkdirs();
            }

            Iterator<String> keys = objects.keys();
            while (keys.hasNext()) {
                String assetName = keys.next();
                JSONObject assetInfo = objects.getJSONObject(assetName);
                String hash = assetInfo.getString("hash");
                // Construction de l'URL de téléchargement pour l'asset
                // Exemple standard : baseURL + "/" + first2chars(hash) + "/" + hash
                String assetDownloadUrl = ASSET_BASE_URL + "/" + hash.substring(0, 2) + "/" + hash;
                // Chemin local de sauvegarde
                String assetLocalDirPath = objectsDirPath + "/" + hash.substring(0, 2);
                File assetLocalDir = new File(assetLocalDirPath);
                if (!assetLocalDir.exists()) {
                    assetLocalDir.mkdirs();
                }
                String assetLocalPath = assetLocalDirPath + "/" + hash;
                // Téléchargement de l'asset
                try {
                    logger.logInfo("Downloading asset: " + assetName + " from " + assetDownloadUrl);
                    downloadFile(assetDownloadUrl, assetLocalPath);
                } catch (DownloadException e) {
                    logger.logError("Error while downloading asset: " + assetName + " : " + e.getMessage(), e);
                    // Continue to the next asset
                }
            }

        } catch (IOException | JSONException e) {
            logger.logError("Error while downloading assets: " + e.getMessage(), e);
            throw new DownloadException("Erreur lors du téléchargement des assets : " + e.getMessage());
        }
    }
}
