package xyz.aweirdwhale.installer;

import java.io.File;

public class launcherInstaller {

    /**
     * Lance l'installation de Minecraft et des mods avec l'ensemble des paramètre nécessaires.
     *
     */

    public static void main(String[] args) {
        // Define the directories
        String homeDir = System.getProperty("user.home");
        String gameDirectory = homeDir + "/.smp2i";
        String modsDirectory = gameDirectory + "/mods";
        String assetsDirectory = gameDirectory + "/assets";
        String resourcePacksDirectory = gameDirectory + "/resourcepacks";

        // Create the directories if they do not exist
        createDirectory(gameDirectory);
        createDirectory(modsDirectory);
        createDirectory(assetsDirectory);
        createDirectory(resourcePacksDirectory);
    }

    private static void createDirectory(String path) {
        File directory = new File(path);
        if (!directory.exists()) {
            if (directory.mkdirs()) {
                System.out.println("Directory created: " + path);
            } else {
                System.out.println("Failed to create directory: " + path);
            }
        } else {
            System.out.println("Directory already exists: " + path);
        }
    }
}