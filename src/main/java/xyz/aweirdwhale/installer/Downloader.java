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
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.DigestInputStream;
import java.security.MessageDigest;
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

    // URL de base pour télécharger les assets (modifiez si votre serveur privé est différent)
    private static final String ASSET_BASE_URL = "https://resources.download.minecraft.net";



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


    public static boolean verifyFileIntegrity(String filePath, String expectedHash) throws Exception {
        MessageDigest digest = MessageDigest.getInstance("SHA-1");
        try (InputStream fis = new FileInputStream(filePath);
             DigestInputStream dis = new DigestInputStream(fis, digest)) {
            while (dis.read() != -1) {} // Lire le fichier
        }
        byte[] hashBytes = digest.digest();
        StringBuilder hashHex = new StringBuilder();
        for (byte b : hashBytes) {
            hashHex.append(String.format("%02x", b));
        }
        return hashHex.toString().equals(expectedHash);
    }



    /**
     * Télécharge les assets de Minecraft en se basant sur le fichier version JSON.
     * @param path chemin de base de l'installation
     * @throws DownloadException en cas d'erreur de téléchargement
     */


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


            // Télécharger l'asset index dans un répertoire temporaire
            String assetsDir = path + "/assets/indexes";
            new File(assetsDir).mkdirs();
            String assetIndexFilePath = assetsDir + "/" + assetIndexId + ".json";
            downloadFile(assetIndexUrl, assetIndexFilePath);

            // Lire et parser l'asset index
            String assetIndexContent = new String(Files.readAllBytes(Paths.get(assetIndexFilePath)));
            JSONObject assetIndexJson = new JSONObject(assetIndexContent);
            JSONObject objects = assetIndexJson.getJSONObject("objects");

            // Télécharger chaque asset
            String objectsDirPath = path + "/assets/objects";
            new File(objectsDirPath).mkdirs();

            Iterator<String> keys = objects.keys();
            while (keys.hasNext()) {
                String assetName = keys.next();
                JSONObject assetInfo = objects.getJSONObject(assetName);
                String hash = assetInfo.getString("hash");
                String assetDownloadUrl = ASSET_BASE_URL + "/" + hash.substring(0, 2) + "/" + hash;
                String assetLocalDirPath = objectsDirPath + "/" + hash.substring(0, 2);
                new File(assetLocalDirPath).mkdirs();
                String assetLocalPath = assetLocalDirPath + "/" + hash;

                // Vérifier l'intégrité avant téléchargement
                File assetFile = new File(assetLocalPath);
                if (assetFile.exists()) {
                    logger.logInfo("Asset already verified: " + assetName);
                    continue;
                }

                // Téléchargement de l'asset
                try {
                    logger.logInfo("Downloading asset: " + assetName + " from " + assetDownloadUrl);
                    downloadFile(assetDownloadUrl, assetLocalPath);

                    // Vérifier l'intégrité après téléchargement
                    if (!verifyFileIntegrity(assetLocalPath, hash)) {
                        logger.logInfo("Integrity check failed for: " + assetName + ", redownloading...");
                        assetFile.delete();
                        downloadFile(assetDownloadUrl, assetLocalPath);
                    }

                    logger.logInfo("Integrity check passed for: " + assetName);
                } catch (DownloadException e) {
                    logger.logError("Error while downloading asset: " + assetName + " : " + e.getMessage(), e);
                }
            }
        } catch (Exception e) {
            logger.logError("Error while downloading assets: " + e.getMessage(), e);
            throw new DownloadException("Erreur lors du téléchargement des assets : " + e.getMessage());
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
