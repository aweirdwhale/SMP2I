package mp2i.sncf.installer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

public class ClassPathGenerator {

    /**
     * S'ocupe de tout generer (pour le test)
     * @param args différents arguments et propriété.
     */
    public static void main(String[] args) {
        String librariesDir = "libraries";
        String outputFile = "classpath.txt";
        generateClassPath(librariesDir, outputFile);
    }


    /**
     * Gènere une liste exhaustive des libs dont minecraft à besoin.
     * @param directory location in the pc
     * @param outputFile se qui a etait crée.
     */
    public static void generateClassPath(String directory, String outputFile){
        try {
            // Trouver tous les fichiers .jar dans le répertoire donné
            List<String> jarFiles = Files.walk(Paths.get(directory))
                    .map(Path::toString)
                    .filter(string -> string.endsWith(".jar"))
                    .collect(Collectors.toList());

            // Construire le classpath en joignant les chemins avec ":" pour unix et ; pour windows
            String classpath;
            String os = System.getProperty("os.name").toLowerCase();

            if (os.contains("win")) {
                classpath = String.join(";", jarFiles);
            } else {
                classpath = String.join(":", jarFiles);
            }

            // Écrire le résultat dans un fichier
            Files.write(Paths.get(outputFile), classpath.getBytes());

            System.out.println("Classpath généré avec succès !");
            System.out.println("Lancement du jeu ...");
        } catch (IOException _) {
        }
    }

}
