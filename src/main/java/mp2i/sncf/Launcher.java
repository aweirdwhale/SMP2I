package mp2i.sncf;

import mp2i.sncf.installer.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static mp2i.sncf.installer.Downloader.downloadFile;

public class Launcher {


    public static void setUp(String username) {

        // crée l'arborescence
        String dir = setUpDirectories.getGameDirectory();
        File gameDir = new File(dir);
        System.out.println(gameDir.getAbsolutePath());

        // télécharge les versions
        Downloader Downloader = new Downloader();
        mp2i.sncf.installer.Downloader.downloadVersions(dir);


        // télécharge les libs
        LibraryInstaller installer = new LibraryInstaller();
        installer.run(dir);


        // systemes unix
        LibraryInstaller.deleteThisFuckingAsm(dir);

        // télécharge les assets
        mp2i.sncf.installer.Downloader.downloadAssets(dir);

        downloadFile("http://217.154.9.109:6969/public/servers.dat", dir+"/servers.dat", false);
        downloadFile("http://217.154.9.109:6969/public/servers.dat_old", dir+"/servers.dat", false);


        // télécharge les mods
        mp2i.sncf.installer.Mods.downloadMods(dir);


        // générer le ClassPath
        ClassPathGenerator.generateClassPath(dir + "/libraries", dir + "/classpath.txt");

        // supprime l'asm chiant pour windows
        String os = System.getProperty("os.name").toLowerCase();

        if (os.contains("win")) {
            ClassPathsCleaner.removeLibraryFromClassPath(dir + "/classpath.txt", ";" + dir + "/libraries/org/ow2/asm/asm/9.6/asm-9.6.jar");
        }
        
        //lance le jeu
        launchMinecraft("4", "2", dir + "/classpath.txt", username, dir);


    }





    public static void launchMinecraft(String maxRam, String minRam, String ClassPaths, String username, String gameDir) {
        try {

            List<String> command = new ArrayList<>();
            command.add("java");
            command.add("-Xmx"+maxRam+"G");
            command.add("-Xms"+minRam+"G");
            command.add("-cp");

            // Assuming the content of <path+classes.b004> is read into a variable called classpathContent
            String classpathContent = new String(Files.readAllBytes(Paths.get(ClassPaths)));
            String classpath;
            String os = System.getProperty("os.name").toLowerCase();

            // windows : terminer la ligne par ";" les autres ":"
            if (os.contains("win")) {
                classpath = gameDir + "/versions/fabric-loader/fabric.jar;" + classpathContent.trim();
            } else {
                classpath = gameDir + "/versions/fabric-loader/fabric.jar:" + classpathContent.trim();
            }
            command.add(classpath);

            command.add("net.fabricmc.loader.impl.launch.knot.KnotClient");

            command.add("--username");
            command.add(username);
            command.add("--version");
            command.add("fabric-loader");
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
        } catch (InterruptedException | IOException _) {

        }
    }

}
