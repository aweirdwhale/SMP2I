//package xyz.aweirdwhale.installer;
//
//import org.json.JSONArray;
//import org.json.JSONObject;
//
//import java.io.*;
//import java.net.HttpURLConnection;
//import java.net.URL;
//
//import xyz.aweirdwhale.utils.log.logger;
//
//public class Installer {
//
//    public void setupEnvironment() {
//        createDirectory();
//        downloadVersions();
//        downloadMods("mods.json");
//        downloadLibraries("versions/1.21.4/1.21.4.json");
//        generateClassPath("libraries", "classpath.txt");
//    }
//
//    private void createDirectory() {
//        String home = System.getProperty("user.home");
//        String os = System.getProperty("os.name").toLowerCase();
//        String path;
//
//        if (os.contains("win")) {
//            path = System.getenv("APPDATA") + "/.smp2ix";
//        } else {
//            path = home + "/.smp2ix";
//        }
//
//        createSubDirectory(path, "mods");
//        createSubDirectory(path, "assets");
//        createSubDirectory(path, "libraries");
//        createSubDirectory(path, "versions");
//        createSubDirectory(path, "skin");
//    }
//
//    private void createSubDirectory(String parentPath, String subDir) {
//        File dir = new File(parentPath + "/" + subDir);
//        if (!dir.exists()) {
//            dir.mkdirs();
//            logger.logInfo("Subdirectory created: " + dir.getPath());
//        }
//    }
//
//    private void downloadVersions() {
//        downloadFile("https://example.com/fabric-1.21.4.jar", "versions/fabric-1.21.4.jar");
//        downloadFile("https://example.com/minecraft-1.21.4.jar", "versions/minecraft-1.21.4.jar");
//    }
//
//    private void downloadMods(String jsonPath) {
//        File jsonFile = new File(jsonPath);
//        if (!jsonFile.exists()) {
//            logger.logError("Error: The file " + jsonPath + " does not exist.", null);
//            return;
//        }
//
//        JSONObject data;
//        try (BufferedReader reader = new BufferedReader(new FileReader(jsonFile))) {
//            StringBuilder jsonContent = new StringBuilder();
//            String line;
//            while ((line = reader.readLine()) != null) {
//                jsonContent.append(line);
//            }
//            data = new JSONObject(jsonContent.toString());
//        } catch (IOException e) {
//            e.printStackTrace();
//            return;
//        }
//
//        JSONArray mods = data.getJSONArray("mods");
//        for (int i = 0; i < mods.length(); i++) {
//            String url = mod.getString("url");
//            String path = "mods/" + mod.getString("path");
//            downloadFile(url, path);
//        }
//    }
//
//    private void downloadLibraries(String jsonPath) {
//        File jsonFile = new File(jsonPath);
//        if (!jsonFile.exists()) {
//            logger.logError("Error: The file " + jsonPath + " does not exist.", null);
//            return;
//        }
//
//        JSONObject data;
//        try (BufferedReader reader = new BufferedReader(new FileReader(jsonFile))) {
//            StringBuilder jsonContent = new StringBuilder();
//            String line;
//            while ((line = reader.readLine()) != null) {
//                jsonContent.append(line);
//            }
//            data = new JSONObject(jsonContent.toString());
//        } catch (IOException e) {
//            e.printStackTrace();
//            return;
//        }
//
//        JSONArray libraries = data.getJSONArray("libraries");
//        for (int i = 0; i < libraries.length(); i++) {
//            JSONObject lib = libraries.getJSONObject(i);
//            JSONObject downloads = lib.optJSONObject("downloads");
//
//            if (downloads != null) {
//                JSONObject artifact = downloads.optJSONObject("artifact");
//                if (artifact != null && artifact.has("url")) {
//                    downloadFile(artifact.getString("url"), "libraries/" + artifact.getString("path"));
//                }
//
//                JSONObject classifiers = downloads.optJSONObject("classifiers");
//                if (classifiers != null) {
//                    for (String key : classifiers.keySet()) {
//                        JSONObject nativeFile = classifiers.getJSONObject(key);
//                        if (nativeFile.has("url")) {
//                            downloadFile(nativeFile.getString("url"), "libraries/" + nativeFile.getString("path"));
//                        }
//                    }
//                }
//            }
//        }
//    }
//
//    private void downloadFile(String urlString, String filePath) {
//        File file = new File(filePath);
//        if (file.exists()) {
//            logger.logInfo("✔ File already exists: " + filePath);
//            return;
//        }
//
//        try {
//            File parentDir = file.getParentFile();
//            if (!parentDir.exists()) {
//                parentDir.mkdirs();
//            }
//
//            URL url = new URL(urlString);
//            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
//            connection.setRequestMethod("GET");
//
//            if (connection.getResponseCode() == 200) {
//                try (InputStream in = connection.getInputStream();
//                     FileOutputStream out = new FileOutputStream(file)) {
//                    byte[] buffer = new byte[4096];
//                    int bytesRead;
//                    while ((bytesRead = in.read(buffer)) != -1) {
//                        out.write(buffer, 0, bytesRead);
//                    }
//                }
//            } else {
//                logger.logError("❌ Error (" + connection.getResponseCode() + ") while downloading " + urlString, null);
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//    public static void generateClassPath(String dir, String outputFile) {
//        File directory = new File(dir);
//        StringBuilder classpath = new StringBuilder();
//
//        if (directory.exists() && directory.isDirectory()) {
//            findJars(directory, classpath);
//        }
//
//        try (FileWriter writer = new FileWriter(outputFile)) {
//            writer.write(classpath.toString());
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//    private static void findJars(File dir, StringBuilder classpath) {
//        File[] files = dir.listFiles();
//        if (files != null) {
//            for (File file : files) {
//                if (file.isDirectory()) {
//                    findJars(file, classpath);
//                } else if (file.getName().endsWith(".jar")) {
//                    classpath.append(file.getAbsolutePath()).append(":");
//                }
//            }
//        }
//    }
//}