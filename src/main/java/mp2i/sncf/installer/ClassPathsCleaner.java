package mp2i.sncf.installer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.stream.Collectors;

public class ClassPathsCleaner {
    public static void removeLibraryFromClassPath(String classPathFile, String libraryToRemove) {
        try {
            // Lire toutes les lignes du fichier
            List<String> lines = Files.readAllLines(Paths.get(classPathFile));

            // Filtrer les lignes pour supprimer celle contenant le chemin à enlever
            List<String> updatedLines = lines.stream()
                    .filter(line -> !line.equals(libraryToRemove))
                    .collect(Collectors.toList());

            // Réécrire le fichier sans la ligne supprimée
            Files.write(Paths.get(classPathFile), updatedLines, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE);


        } catch (IOException _) {

        }
    }
}
