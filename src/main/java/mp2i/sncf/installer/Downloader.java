package mp2i.sncf.installer;

import java.io.*;

import org.json.*;
import java.net.URI;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Iterator;

public class Downloader {

    private static final String SERVER = "http://217.154.9.109";
    private static final String PORT = ":6969";

    private static final String FABRIC_URL = SERVER + PORT + "/public/fabric-loader/fabric.jar";
    private static final String FABRIC_JSON_URL = SERVER + PORT + "/public/fabric-loader/fabric.json";
    private static final String MINECRAFT_URL = SERVER + PORT + "/public/1.21.4/1.21.4.jar";
    private static final String MINECRAFT_JSON_URL = SERVER + PORT + "/public/1.21.4/1.21.4.json";

    private static final String MODS_JSON_URL = SERVER + PORT + "/public/mods.json";

    private static final String ASSET_BASE_URL = "https://resources.download.minecraft.net";

    public static void downloadVersions(String path) {
        downloadFile(FABRIC_URL, path + "/versions/fabric-loader/fabric.jar");
        downloadFile(FABRIC_JSON_URL, path + "/versions/fabric-loader/fabric.json");
        downloadFile(MINECRAFT_URL, path + "/versions/1.21.4/1.21.4.jar");
        downloadFile(MINECRAFT_JSON_URL, path + "/versions/1.21.4/1.21.4.json");

        downloadAssets(path);
    }

    /**
     * downloadFile :
     * @param fileUrl : Url du fichier à télécharger;
     * @param savePath : Cheminoù stocker le fichier;
     * **/
    public static void downloadFile(String fileUrl, String savePath) {
        File file = new File(savePath);

        // Pour éviter de télécharger plusieurs fois (long et inutile)
        if (file.exists()) {
            return;
        }

        try {
            URI uri = URI.create(fileUrl);
            URL url = uri.toURL();

            File parentDir = file.getParentFile();

            // crée le dossier parent si il n'existe pas
            if (!parentDir.exists()) {
                parentDir.mkdirs();
            }

            try (ReadableByteChannel channel = Channels.newChannel(url.openStream());
                 FileOutputStream fos = new FileOutputStream(savePath)) {
                fos.getChannel().transferFrom(channel, 0, Long.MAX_VALUE);
            }

        } catch (IOException _) {
        }
    }

    public static void downloadAssets(String path)  {
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
                    continue;
                }

                // Téléchargement de l'asset
                downloadFile(assetDownloadUrl, assetLocalPath);
            }
        } catch (Exception _) {
        }
    }


}
