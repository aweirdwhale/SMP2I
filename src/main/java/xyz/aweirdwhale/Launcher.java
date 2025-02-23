package xyz.aweirdwhale;

import xyz.aweirdwhale.installer.ClassPathGenerator;
import xyz.aweirdwhale.installer.Downloader;
import xyz.aweirdwhale.installer.setupEnvironment;
import xyz.aweirdwhale.utils.exceptions.DownloadException;
import xyz.aweirdwhale.utils.exceptions.LaunchException;
import xyz.aweirdwhale.utils.log.logger;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class Launcher {

    /**
     * Lance Minecraft avec les mods.
     *
     * @param gameDir directory of minecraft in user pc
     * @param assetsDir directorie of assets
     * @param mainClass the mainclass of the game to launch
     * @param jars all mods to get
     * @param server the attribute of the serveur
     * @param port the public port of the serveur or ip
     * @param pseudo the pseudo of the player
     * @throws LaunchException erreur of launch
     */


    /**
     * Commande de lancement :
     *
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

    public static void setUp(String username) {
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
                Downloader.downloadVersions(path);
                logger.logInfo("Versions downloaded.");

                // télécharge les libs
                logger.logInfo("Downloading libraries...");
                Downloader.downloadLibs(path);
                logger.logInfo("Libraries downloaded.");

                // télécharge les mods
                logger.logInfo("Downloading mods...");
                Downloader.downloadMods(path);
                logger.logInfo("Mods downloaded.");

                //generating ClassPath
                logger.logInfo("Generating ClassPath...");
                ClassPathGenerator.generateClassPath(path + "/libraries", path + "/classpaths.txt");
                logger.logInfo("ClassPath generated : " + path + "/classpaths.txt");

           // }

            //logger.logInfo("Environment already set up. Skipping setup.");

            //lance le jeu
            launchMinecraft("4", "2", path + "/classpath.txt", username, path);


        } catch (DownloadException e) {
            throw new RuntimeException(e);
        } catch (LaunchException e) {
            throw new RuntimeException(e);
        }
    }

    public static void launchMinecraft(String maxRam, String minRam, String ClassPaths, String username, String gameDir) throws LaunchException {
        try {
            List<String> command = new ArrayList<>();
            command.add("java");
            command.add("-Xmx"+maxRam+"G");
            command.add("-Xms"+minRam+"G");
            command.add("-cp");

            // Assuming the content of <path+classes.b004> is read into a variable called classpathContent
            String classpathContent = new String(Files.readAllBytes(Paths.get(ClassPaths)));
            String classpath = "versions/fabric-loader/fabric.jar:" + classpathContent.trim();
            command.add(classpath);

            command.add("net.fabricmc.loader.impl.launch.knot.KnotClient");
            command.add("--username");
            command.add(username);
            command.add("--version");
            command.add("fabric-loader");
            command.add("--gameDir");
            command.add(gameDir);
            command.add("--assetsDir");
            command.add("assets");
            command.add("--assetIndex");
            command.add("1.21");
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