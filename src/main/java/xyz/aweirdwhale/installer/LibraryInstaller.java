package xyz.aweirdwhale.installer;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import xyz.aweirdwhale.utils.log.logger;

public class LibraryInstaller {

    private static final String[] JSON_PATHS = {
            "versions/1.21.4/1.21.4.json",
            "versions/fabric-loader/fabric.json"
    };
    private static final String LIBRARIES_DIR = "libraries/";
    private static final String[] MAVEN_REPOSITORIES = {
            "https://libraries.minecraft.net/",
            "https://maven.fabricmc.net/"
    };

    public void run() {
        // Check if JSON files exist
        for (String jsonPath : JSON_PATHS) {
            if (!new File(jsonPath).exists()) {
                System.out.println("Erreur: Le fichier " + jsonPath + " n'existe pas.");
                logger.logError("Error: The file " + jsonPath + " does not exist.", null);
                return;
            }
        }

        // Get all library URLs from JSON files
        List<String[]> urls = new ArrayList<>();
        Map<String, String> versionsMap = new HashMap<>();
        for (String jsonPath : JSON_PATHS) {
            try {
                String[] result = getLibrariesFromJson(jsonPath);
                urls.addAll(Arrays.asList(result[0].split(";")).stream().map(s -> s.split(",")).toList());
                versionsMap.putAll(parseVersionsMap(result[1]));
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
        }

        // Remove old versions of libraries
        removeOldVersions(versionsMap);

        // Download each library
        for (String[] urlPath : urls) {
            downloadLibrary(urlPath[0], urlPath[1]);
        }

        System.out.println("✅ Téléchargement terminé !");
        logger.logInfo("✅ Downloading done !");
    }

    private String[] getLibrariesFromJson(String jsonPath) throws IOException {
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
        }

        return new String[]{urls.toString(), versionsMap.toString()};
    }

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

    private void removeOldVersions(Map<String, String> versionsMap) {
        File librariesDir = new File(LIBRARIES_DIR);
        if (librariesDir.exists() && librariesDir.isDirectory()) {
            for (File file : librariesDir.listFiles()) {
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

    private void downloadLibrary(String urlString, String filePath) {
        File file = new File(LIBRARIES_DIR + filePath);
        if (file.exists()) {
            System.out.println("✔ Fichier déjà présent : " + file.getPath() + ", passage au suivant...");
            logger.logInfo("✔ File already satisfied : " + file.getPath() + ", next...");
            return;
        }

        try {
            File parentDir = file.getParentFile();
            if (!parentDir.exists()) {
                parentDir.mkdirs();
            }

            System.out.println("Téléchargement de " + urlString + "...");
            logger.logInfo("Downloading " + urlString + "...");

            URL url = new URL(urlString);
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
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}