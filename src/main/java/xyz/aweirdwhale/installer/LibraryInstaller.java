package xyz.aweirdwhale.installer;

import org.json.JSONArray;
import org.json.JSONObject;
import xyz.aweirdwhale.utils.exceptions.LibraryException;
import xyz.aweirdwhale.utils.log.logger;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LibraryInstaller {

    private static final String[] JSON_PATHS = {
            "/versions/1.21.4/1.21.4.json",
            "/versions/fabric-loader/fabric.json"
    };
    private static final String LIBRARIES_DIR = "/libraries/";
    private static final String[] MAVEN_REPOSITORIES = {
            "https://libraries.minecraft.net/",
            "https://maven.fabricmc.net/"
    };

    public void run(String path) throws LibraryException {
        // Check if JSON files exist
        for (String jsonPath : JSON_PATHS) {
            if (!new File(path+jsonPath).exists()) {
                System.out.println("Erreur: Le fichier " + path+jsonPath + " n'existe pas.");
                logger.logError("Error: The file " + path+jsonPath + " does not exist.", null);
                return;
            }
        }

        // Get all library URLs from JSON files
        List<String[]> urls = new ArrayList<>();
        Map<String, String> versionsMap = new HashMap<>();
        for (String jsonPath : JSON_PATHS) {
            String[] result = getLibrariesFromJson(path+jsonPath);
            urls.addAll(Arrays.stream(result[0].split(";")).map(s -> s.split(",")).toList());
            versionsMap.putAll(parseVersionsMap(result[1]));
        }

        // Remove old versions of libraries
        removeOldVersions(versionsMap, path);

        // Download each library
        for (String[] urlPath : urls) {
            downloadLibrary(urlPath[0], urlPath[1], path);
        }

        System.out.println("✅ Téléchargement terminé !");
        logger.logInfo("✅ Downloading done !");
    }

    /**
     * Cherche l'ensembles des librairie json.
     * @param jsonPath chemin du fichier json.
     * @return ensemble des librairies trouver dans le dossier.
     * @throws LibraryException erreur
     */
    private String[] getLibrariesFromJson(String jsonPath) throws LibraryException {
        StringBuilder urls = new StringBuilder();
        StringBuilder versionsMap = new StringBuilder();

        try (BufferedReader reader = new BufferedReader(new FileReader(jsonPath))) {
            StringBuilder jsonContent = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                jsonContent.append(line);
            }
            JSONObject data = new JSONObject(jsonContent.toString());

            JSONArray libraries = data.getJSONArray("libraries");
            for (int i = 0; i < libraries.length(); i++) {
                JSONObject lib = libraries.getJSONObject(i);
                String name = lib.optString("name");
                String url = lib.optString("url", MAVEN_REPOSITORIES[0]);

                if (!name.isEmpty()) {
                    String[] parts = name.split(":");
                    if (parts.length == 3) {
                        String group = parts[0];
                        String artifact = parts[1];
                        String version = parts[2];
                        String libPath = group.replace('.', '/') + "/" + artifact + "/" + version + "/" + artifact + "-" + version + ".jar";
                        String fullUrl = url + libPath;
                        urls.append(fullUrl).append(",").append(libPath).append(";");

                        String key = group + "." + artifact;
                        if (!versionsMap.toString().contains(key) || version.compareTo(versionsMap.toString()) > 0) {
                            versionsMap.append(key).append(",").append(version).append(";");
                        }
                    }
                }

                JSONObject downloads = lib.optJSONObject("downloads");
                if (downloads != null) {
                    JSONObject artifact = downloads.optJSONObject("artifact");
                    if (artifact != null && artifact.has("url")) {
                        urls.append(artifact.getString("url")).append(",").append(artifact.optString("path", artifact.getString("url").split("/")[artifact.getString("url").split("/").length - 1])).append(";");
                    }

                    JSONObject classifiers = downloads.optJSONObject("classifiers");
                    if (classifiers != null) {
                        for (String key : classifiers.keySet()) {
                            JSONObject nativeFile = classifiers.getJSONObject(key);
                            if (nativeFile.has("url")) {
                                urls.append(nativeFile.getString("url")).append(",").append(nativeFile.optString("path", nativeFile.getString("url").split("/")[nativeFile.getString("url").split("/").length - 1])).append(";");
                            }
                        }
                    }
                }
            }
        }  catch (IOException e) {
            throw new LibraryException(e.getMessage());
        }

        return new String[]{urls.toString(), versionsMap.toString()};
    }

    /**
     *
     * @return différente version de la Map.
     */
    private Map<String, String> parseVersionsMap(String versionsMapStr) {
        Map<String, String> versionsMap = new HashMap<>();
        String[] entries = versionsMapStr.split(";");
        for (String entry : entries) {
            String[] keyValue = entry.split(",");
            if (keyValue.length == 2) {
                versionsMap.put(keyValue[0], keyValue[1]);
            }
        }
        return versionsMap;
    }

    private void removeOldVersions(Map<String, String> versionsMap, String path) {
        File librariesDir = new File(path+LIBRARIES_DIR);
        if (librariesDir.exists() && librariesDir.isDirectory()) {
            for (File file : Objects.requireNonNull(librariesDir.listFiles())) {
                if (file.isFile()) {
                    Matcher matcher = Pattern.compile("(.+)-(\\d+\\.\\d+\\.\\d+).*\\.jar").matcher(file.getName());
                    if (matcher.matches()) {
                        String libKey = matcher.group(1);
                        String version = matcher.group(2);
                        if (versionsMap.containsKey(libKey) && version.compareTo(versionsMap.get(libKey)) < 0) {
                            System.out.println("🗑 Suppression de l'ancienne version: " + file.getPath());
                            logger.logInfo("🗑 Deleting the oldest version: " + file.getPath());
                            file.delete();
                        }
                    }
                }
            }
        }
    }


    /**
     * IDK wtf I have to delete this one manually
     * **/
    public static void deleteThisFuckingAsm(String path){
        String filePath = path + "/libraries/org/ow2/asm/asm/9.6/asm-9.6.jar";
        File file = new File(filePath);

        if (file.exists()) {
            if (file.delete()) {
                System.out.println("✔ Fichier supprimé avec succès !");
            } else {
                System.out.println("❌ Impossible de supprimer le fichier.");
            }
        } else {
            System.out.println("⚠ Fichier introuvable.");
        }

    }

    private void downloadLibrary(String urlString, String filePath, String path) throws LibraryException {
        File file = new File(path+LIBRARIES_DIR + filePath);
        if (file.exists()) {
            System.out.println("✔ Fichier déjà présent : " + file.getPath() + ", passage au suivant...");
            logger.logInfo("✔ File already satisfied : " + file.getPath() + ", moving to the next one...");
            return;
        }

        try {
            File parentDir = file.getParentFile();
            if (!parentDir.exists()) {
                parentDir.mkdirs();
            }

            System.out.println("Téléchargement de " + urlString + "à " + file.getPath() + "...");
            logger.logInfo("Downloading " + urlString + "to" + file.getPath() + "...");

            URI uri = new URI(urlString);
            URL url = uri.toURL(); //URI
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            if (connection.getResponseCode() == 200) {
                try (InputStream in = connection.getInputStream();
                     FileOutputStream out = new FileOutputStream(file)) {
                    byte[] buffer = new byte[4096];
                    int bytesRead;
                    while ((bytesRead = in.read(buffer)) != -1) {
                        out.write(buffer, 0, bytesRead);
                    }
                }
                logger.logInfo("✔ Saved File : " + file.getPath());
            } else {
                System.out.println("❌ Erreur (" + connection.getResponseCode() + ") lors du téléchargement de " + urlString);
                logger.logError("❌ Error (" + connection.getResponseCode() + ") While downloading " + urlString, null);
            }
        } catch (IOException | URISyntaxException e) {
            throw new LibraryException(e.getMessage());
        }

    }
}