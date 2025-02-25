package xyz.aweirdwhale;

import xyz.aweirdwhale.installer.*;
import xyz.aweirdwhale.utils.exceptions.DownloadException;
import xyz.aweirdwhale.utils.exceptions.LaunchException;
import xyz.aweirdwhale.utils.log.logger;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class Launcher {


    /**
     * Commande de lancement :
     * java -Xmx4G -Xms2G \
     *   -cp "versions/fabric-loader/fabric.jar:$(cat <path+classes.b004>)" \
     *   net.fabricmc.loader.impl.launch.knot.KnotClient \
     *   --username <username> \
     *   --version "fabric-loader" \
     *   --gameDir <path> \
     *   --assetsDir "assets" \
     *   --assetIndex "1.21" \
     *   --accessToken "Q&O Les Goats"
     * **/

    public static void setUp(String username) throws LaunchException {
        try {

            String dir = setupEnvironment.getGameDir();
            File gameDir = new File(dir);
            //if (!gameDir.exists() || !gameDir.isDirectory()) {
                // crée la structure de fichiers
                logger.logInfo("Creating game directory...");
                String path = setupEnvironment.createGameDir();
                logger.logInfo("Game directory created.");

                // télécharge les versions
                logger.logInfo("Downloading versions...");
                Downloader Downloader = new Downloader();
                xyz.aweirdwhale.installer.Downloader.downloadVersions(path);
                logger.logInfo("Versions downloaded.");

                // télécharge les libs
                logger.logInfo("Downloading libraries...");
                xyz.aweirdwhale.installer.Downloader.downloadLibs(path);
                logger.logInfo("Libraries downloaded.");

                logger.logInfo("Deleting the old asm");
                LibraryInstaller.deleteThisFuckingAsm(path);

                // télécharge les assets
                logger.logInfo("Downloading assets...");
                xyz.aweirdwhale.installer.Downloader.downloadAssets(path);
                logger.logInfo("Assets downloaded.");

                // télécharge les mods
                logger.logInfo("Downloading mods...");
                xyz.aweirdwhale.installer.Downloader.downloadMods(path);
                logger.logInfo("Mods downloaded.");

                //generating ClassPath
                logger.logInfo("Generating ClassPath...");
                ClassPathGenerator.generateClassPath(path + "/libraries", path + "/classpath.txt");
                logger.logInfo("ClassPath generated : " + path + "/classpaths.txt");

                String os = System.getProperty("os.name").toLowerCase();

                if (os.contains("win")) {
                    logger.logInfo("Cleaning ...");
                    ClassPathsCleaner.removeLibraryFromClassPath(path+"/classpath.txt", ";"+ path +"/libraries/org/ow2/asm/asm/9.6/asm-9.6.jar");
                    logger.logInfo("Clean.");
                }


           //}

            //logger.logInfo("Environment already set up. Skipping setup.");

            //lance le jeu
            launchMinecraft("4", "2", path + "/classpath.txt", username, path, dir);


        } catch (DownloadException | LaunchException e) {
            throw new LaunchException(e.getMessage());
        }
    }


    public static void launchMinecraft(String maxRam, String minRam, String ClassPaths, String username, String gameDir, String path) throws LaunchException {
        try {

            /*
            * TESTS : crée un fichier json blank
            * dir/assets/indexes/blank.json
            * */

            String filePath = gameDir+"/assets/indexes/blank.json";
            File file = new File(filePath);

            // Créer le fichier et écrire "{}"
            FileWriter writer = new FileWriter(file);

            writer.write("{}");
            writer.close();





            List<String> command = new ArrayList<>();
            command.add("java");
            command.add("-Xmx"+maxRam+"G");
            command.add("-Xms"+minRam+"G");
            command.add("-cp");

            // Assuming the content of <path+classes.b004> is read into a variable called classpathContent
            String classpathContent = new String(Files.readAllBytes(Paths.get(ClassPaths)));
            String classpath;
            String os = System.getProperty("os.name").toLowerCase();

            if (os.contains("win")) {
                classpath = path + "/versions/fabric-loader/fabric.jar;" + classpathContent.trim();
            } else {
                classpath = path + "/versions/fabric-loader/fabric.jar:" + classpathContent.trim();
            }
            command.add(classpath);

            command.add("net.fabricmc.loader.impl.launch.knot.KnotClient");

            command.add("--username");
            command.add(username);
            command.add("--version");
            command.add("1.21.4");
            command.add("--gameDir");
            command.add(gameDir);
            command.add("--assetsDir");
            command.add(gameDir+"/assets");
            command.add("--assetIndex");
            command.add("19");
            command.add("--accessToken");
            command.add("Q&O Les Goats");

            ProcessBuilder builder = new ProcessBuilder(command);
            builder.inheritIO();
            Process process = builder.start();
            process.waitFor();
        } catch (IOException | InterruptedException e) {
            logger.logError("Error while launching Minecraft: " + e.getMessage(), e);
            throw new LaunchException("Error while launching Minecraft: " + e.getMessage());
        }
    }
}