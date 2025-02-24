package xyz.aweirdwhale.installer;

import org.json.JSONObject;
import org.json.JSONArray;
import org.json.JSONException;
import xyz.aweirdwhale.utils.exceptions.DownloadException;
import xyz.aweirdwhale.utils.log.logger;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class Downloader {

    private static final String SERVER = "http://" + getServerUrl.SERVER;
    private static final String PORT = ":6969";
    private static final String FABRIC_URL = SERVER + PORT + "/public/fabric-loader/fabric.jar";
    private static final String FABRIC_JSON_URL = SERVER + PORT + "/public/fabric-loader/fabric.json";
    private static final String MINECRAFT_URL = SERVER + PORT + "/public/1.21.4/1.21.4.jar";
    private static final String MINECRAFT_JSON_URL = SERVER + PORT + "/public/1.21.4/1.21.4.json";
    private static final String MODS_JSON_URL = SERVER + PORT + "/public/mods.json";
    private static final String ASSET_BASE_URL = "http://resources.download.minecraft.net";

    public static void downloadVersions(String path) throws DownloadException {
        downloadFile(FABRIC_URL, path + "/versions/fabric-loader/fabric.jar");
        downloadFile(FABRIC_JSON_URL, path + "/versions/fabric-loader/fabric.json");
        downloadFile(MINECRAFT_URL, path + "/versions/1.21.4/1.21.4.jar");
        downloadFile(MINECRAFT_JSON_URL, path + "/versions/1.21.4/1.21.4.json");

        downloadAssets(path);
        extractSoundsFromJar(path);
    }

    public static void downloadLibs(String path) throws DownloadException {
        LibraryInstaller installer = new LibraryInstaller();
        installer.run(path);
    }

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

    public static void downloadAssets(String path) throws DownloadException {
        String versionJsonPath = path + "/versions/1.21.4/1.21.4.json";
        try {
            String jsonContent = new String(Files.readAllBytes(Paths.get(versionJsonPath)));
            JSONObject versionJson = new JSONObject(jsonContent);

            JSONObject assetIndex = versionJson.getJSONObject("assetIndex");
            String assetIndexUrl = assetIndex.getString("url");
            String assetIndexId = assetIndex.getString("id");

            String assetIndexPath = path + "/assets/indexes/" + assetIndexId + ".json";
            downloadFile(assetIndexUrl, assetIndexPath);

            processAssetIndex(path, assetIndexPath);

        } catch (Exception e) {
            throw new DownloadException("Erreur assets: " + e.getMessage());
        }
    }

    private static void processAssetIndex(String path, String assetIndexPath) throws Exception {
        JSONObject assetIndex = new JSONObject(new String(Files.readAllBytes(Paths.get(assetIndexPath))));
        JSONObject objects = assetIndex.getJSONObject("objects");

        String objectsDir = path + "/assets/objects";
        new File(objectsDir).mkdirs();

        Iterator<String> keys = objects.keys();
        while (keys.hasNext()) {
            String assetKey = keys.next();
            JSONObject asset = objects.getJSONObject(assetKey);
            String hash = asset.getString("hash");

            String assetUrl = ASSET_BASE_URL + "/" + hash.substring(0, 2) + "/" + hash;
            String assetPath = objectsDir + "/" + hash.substring(0, 2) + "/" + hash;

            if (!new File(assetPath).exists()) {
                logger.logInfo("Téléchargement asset: " + assetKey);
                downloadFile(assetUrl, assetPath);
            }
        }
    }

    private static void extractSoundsFromJar(String path) throws DownloadException {
        String jarPath = path + "/versions/1.21.4/1.21.4.jar";
        String soundsDir = path + "/assets/minecraft/sounds";

        try (JarFile jar = new JarFile(jarPath)) {
            Enumeration<JarEntry> entries = jar.entries();

            while (entries.hasMoreElements()) {
                JarEntry entry = entries.nextElement();
                String name = entry.getName();

                if (name.startsWith("assets/minecraft/sounds/") && !entry.isDirectory()) {
                    File dest = new File(soundsDir, name.replace("assets/minecraft/sounds/", ""));
                    dest.getParentFile().mkdirs();

                    try (InputStream is = jar.getInputStream(entry);
                         OutputStream os = new FileOutputStream(dest)) {
                        byte[] buffer = new byte[4096];
                        int bytesRead;
                        while ((bytesRead = is.read(buffer)) != -1) {
                            os.write(buffer, 0, bytesRead);
                        }
                    }
                    logger.logInfo("Son extrait: " + name);
                }
            }
        } catch (IOException e) {
            throw new DownloadException("Erreur extraction sons: " + e.getMessage());
        }
    }
}