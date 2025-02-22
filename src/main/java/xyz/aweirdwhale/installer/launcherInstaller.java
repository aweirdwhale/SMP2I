package xyz.aweirdwhale.installer;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

import xyz.aweirdwhale.utils.log.logger;
public class launcherInstaller {

    /**
     *
     * Lance l'installation de Minecraft et des mod.
     * Crée un dossier ~/.smp2ix sur unix et %appdata%/.smp2ix sur windows
     * Crée les dossiers mods, assets, libraries, versions et skin dans ~/.smp2ix
     * Télécharge les resources
     *
     */

    public void downloadingPrerequisites() {
        /**
         * Télécharge depuis le serveur :
         * - fabric 1.21.4 : https://example.com
         * - minecraft 1.21.4 : https://example.com
         *
         * 1 -> crée les dirs
         * 2 -> télécharge les versions
         * 3 -> télécharge les mods
         * 4 -> Génère le classpath
         * 5 -> télécharge les librairies
         * 6 -> télécharge les assets
         * 7 -> Lance le jeu
         * **/

    }


    public static void generateClassPath(String dir, String outputFile) {
        File directory = new File(dir);
        StringBuilder classpath = new StringBuilder();

        if (directory.exists() && directory.isDirectory()) {
            findJars(directory, classpath);
        }

        try (FileWriter writer = new FileWriter(outputFile)) {
            writer.write(classpath.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void findJars(File dir, StringBuilder classpath) {
        File[] files = dir.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    findJars(file, classpath);
                } else if (file.getName().endsWith(".jar")) {
                    classpath.append(file.getAbsolutePath()).append(":");
                }
            }
        }
    }

    public void createDirectory(){
        String home = System.getProperty("user.home");
        String os = System.getProperty("os.name").toLowerCase();
        String path = "";

        logger.logInfo("Creating directory for OS: " + os);

        if (os.contains("win")) {
            path = System.getenv("APPDATA") + "/.smp2ix";
            logger.logInfo("Preparing directory for OS: Windows");
        } else {
            path = home + "/.smp2ix";
            logger.logInfo("Preparing directory for OS: Unix-based");
        }
        File file = new File(path);
        if (!file.exists()) {
            file.mkdirs();
            logger.logInfo("Directory created: " + path);
        }
        File mods = new File(path + "/mods");
        if (!mods.exists()) {
            mods.mkdirs();
        }
        File assets = new File(path + "/assets");
        if (!assets.exists()) {
            assets.mkdirs();
        }
        File libraries = new File(path + "/libraries");
        if (!libraries.exists()) {
            libraries.mkdirs();
        }
        File versions = new File(path + "/versions");
        if (!versions.exists()) {
            versions.mkdirs();
        }
        File skin = new File(path + "/skin");
        if (!skin.exists()) {
            skin.mkdirs();
        }
    }

    public void downloadLibraries(String jsonPath) {
        logger.logInfo("Downloading libraries...");
        //String jsonPath = "versions/1.21.4/1.21.4.json";
        String librariesDir = "libraries/";

        // Check if the JSON file exists
        File jsonFile = new File(jsonPath);
        if (!jsonFile.exists()) {
            System.out.println("Erreur: Le fichier " + jsonPath + " n'existe pas.");
            logger.logError("Error: The file " + jsonPath + " does not exist.", null);
            return;
        }

        // Load the JSON file
        JSONObject data;
        try (BufferedReader reader = new BufferedReader(new FileReader(jsonFile))) {
            StringBuilder jsonContent = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                jsonContent.append(line);
            }
            data = new JSONObject(jsonContent.toString());
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        // Get all library URLs
        JSONArray libraries = data.getJSONArray("libraries");
        for (int i = 0; i < libraries.length(); i++) {
            JSONObject lib = libraries.getJSONObject(i);
            JSONObject downloads = lib.optJSONObject("downloads");

            // Check and add the main artifact
            JSONObject artifact = downloads.optJSONObject("artifact");
            if (artifact != null && artifact.has("url")) {
                downloadFile(artifact.getString("url"), librariesDir + artifact.getString("path"));
                logger.logInfo("Downloading library: " + artifact.getString("path"));
            }

            // Check and add native files
            JSONObject classifiers = downloads.optJSONObject("classifiers");
            if (classifiers != null) {
                for (String key : classifiers.keySet()) {
                    JSONObject nativeFile = classifiers.getJSONObject(key);
                    if (nativeFile.has("url")) {
                        downloadFile(nativeFile.getString("url"), librariesDir + nativeFile.getString("path"));
                        logger.logInfo("Downloading native library: " + nativeFile.getString("path"));
                    }
                }
            }
        }
    }

    private void downloadFile(String urlString, String filePath) {
        File file = new File(filePath);
        if (file.exists()) {
            System.out.println("✔ Fichier déjà présent : " + filePath + ", passage au suivant...");
            logger.logInfo("✔ File already satisfied: " + filePath + ", moving on to the next one...");
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
                    }
                }
                System.out.println("✔ Fichier enregistré : " + filePath);
                logger.logInfo("✔ File saved: " + filePath);
            } else {
                System.out.println("❌ Erreur (" + connection.getResponseCode() + ") lors du téléchargement de " + urlString);
                logger.logError("❌ Error (" + connection.getResponseCode() + ") while downloading " + urlString, null);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    }