package xyz.aweirdwhale.uninstaller;

import xyz.aweirdwhale.utils.exceptions.DeletionException;
import xyz.aweirdwhale.utils.exceptions.PathException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static java.nio.file.Files.delete;

public class uninstall {
    /**
     * Supprime récursivement le dossier .smp2ix
     * **/

    public static void main(String[] args) throws IOException {
        String gameDir = getGameDir();
        isFolder(gameDir);
    }

    public static void kaboom() throws IOException {
        String gameDir = getGameDir();
        isFolder(gameDir);
    }

    public static void isFolder(String path) throws PathException, IOException {
        File folder = new File(path);
        System.out.println(folder.toPath() + " is already deleted.");
        if (folder.exists() && folder.isDirectory()) {
            deleteRecursively(folder.toPath());
        }
    }

    public static void deleteRecursively(Path path) throws IOException {
        if (Files.isDirectory(path)) {
            try (var entries = Files.newDirectoryStream(path)) {
                for (Path entry : entries) {
                    System.out.println("Deleting " + entry);
                    deleteRecursively(entry);
                }
            }
        }
        Files.delete(path);
    }


    public static String getGameDir() {
        // gets the home dir
        String home = System.getProperty("user.home");
        String os = System.getProperty("os.name").toLowerCase();
        String path;

        // win / unix
        if (os.contains("win")) {
            path = System.getenv("APPDATA") + "/.smp2ix";
        } else {
            path = home + "/.smp2ix";
        }

        return path;
    }

}
