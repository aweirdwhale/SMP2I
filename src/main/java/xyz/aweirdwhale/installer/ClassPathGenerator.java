package xyz.aweirdwhale.installer;

import xyz.aweirdwhale.utils.log.logger;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

public class ClassPathGenerator {

    public static void main(String[] args) {
        String librariesDir = "libraries";
        String outputFile = "classpath.txt";
        generateClassPath(librariesDir, outputFile);
    }

    public static void generateClasspath(String directory, String outputFile) {
        try {
            // Trouver tous les fichiers .jar dans le répertoire donné
            List<String> jarFiles = Files.walk(Paths.get(directory))
                    .filter(path -> path.toString().endsWith(".jar"))
                    .map(Path::toString)
                    .collect(Collectors.toList());

            // Construire le classpath en joignant les chemins avec ":"
            String classpath = String.join(":", jarFiles);

            // Écrire le résultat dans un fichier
            Files.write(Paths.get(outputFile), classpath.getBytes());

            System.out.println("Classpath généré avec succès !");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void findJars(File dir, StringBuilder classpath) {
        File[] files = dir.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    findJars(file, classpath);
                } else if (file.getName().endsWith(".jar")) {
                    classpath.append(file.getAbsolutePath()).append(":");
                }
            }
        }
    }
}