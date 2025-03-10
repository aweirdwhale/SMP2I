package mp2i.sncf.uninstall;

import mp2i.sncf.installer.setUpDirectories;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;

public class Uninstall {
    /**
     * Supprime récursivement le dossier .smp2ix
     **/

    public static void main(String[] args) {
        try {
            kaboom();
        } catch (IOException e) {
            System.err.println("Erreur lors de la suppression : " + e.getMessage());
        }
    }

    public static void kaboom() throws IOException {
        String gameDir = getGameDir();
        deleteFolder(gameDir);
    }

    public static void modCleaning() throws IOException {
        String gameDir = getGameDir();
        deleteFolder(gameDir+"/mods");
    }

    public static void deleteFolder(String path) throws IOException {
        File folder = new File(path);
        if (folder.exists() && folder.isDirectory()) {
            System.out.println("Suppression de " + folder.toPath() + "...");
            deleteRecursively(folder.toPath());
            System.out.println("Le dossier " + folder.toPath() + " a été supprimé avec succès.");
        } else {
            System.out.println("Le dossier " + folder.toPath() + " n'existe pas endore ou a déjà été supprimé.");
        }
    }

    public static void deleteRecursively(Path path) throws IOException {
        if (Files.isDirectory(path)) {
            try (DirectoryStream<Path> entries = Files.newDirectoryStream(path)) {
                for (Path entry : entries) {
                    deleteRecursively(entry);
                }
            }
        }
        Files.delete(path);
    }

    public static String getGameDir() {
        return setUpDirectories.getGameDirectory();
    }
}
