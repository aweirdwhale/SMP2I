package xyz.aweirdwhale.installer;

import xyz.aweirdwhale.utils.log.logger;

import java.io.File;

public class setupEnvironment {

    /**
     * Creates the root directory of the game and its subdirectories
     * **/
    public static String createGameDir() {
        String home = System.getProperty("user.home"); // best to use : String path = getGameDir(); (static and less complexe)
        String os = System.getProperty("os.name").toLowerCase();
        String path;

        if (os.contains("win")) {
            path = System.getenv("APPDATA") + "/.smp2ix";
        } else {
            path = home + "/.smp2ix";
        }

        createSubDirectory(path, "mods");
        createSubDirectory(path, "assets");
        createSubDirectory(path, "libraries");
        createSubDirectory(path, "versions");
        createSubDirectory(path, "skin");

        return path;
    }

    /**
     * Créateur de sous dossier du jeu dans le répertoire du jeu.
     * @param parentPath le chemin du dossier parent,
     * @param subDir nom du sous dossier
     */
    private static void createSubDirectory(String parentPath, String subDir) {
        File dir = new File(parentPath + "/" + subDir);
        if (!dir.exists()) {
            dir.mkdirs();
            logger.logInfo("Subdirectory created: " + dir.getPath());
        }
    }

    /**
     * Cherche le dossier du jeu.
     * @return Renoie le chemin pour trouver le jeu.
     */
    public static String getGameDir() {
        String home = System.getProperty("user.home");
        String os = System.getProperty("os.name").toLowerCase();
        String path;

        if (os.contains("win")) {
            path = System.getenv("APPDATA") + "/.smp2ix";
        } else {
            path = home + "/.smp2ix";
        }

        return path;
    }
}
