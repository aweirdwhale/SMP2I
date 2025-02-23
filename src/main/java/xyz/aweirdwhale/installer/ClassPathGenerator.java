package xyz.aweirdwhale.installer;

import xyz.aweirdwhale.utils.log.logger;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class ClassPathGenerator {

    /**
     * S'ocupe de tout generer
     * @param args différents arguments et propriété.
     */
    public static void main(String[] args) {
        String librariesDir = "libraries";
        String outputFile = "classpath.txt";
        generateClassPath(librariesDir, outputFile);
    }

    /**
     * Gènere l'essentiel des fichier et package pour les parametre.
     * @param dir location in the pc
     * @param outputFile se qui a etait crée.
     */
    public static void generateClassPath(String dir, String outputFile) {
        File directory = new File(dir);
        StringBuilder classpath = new StringBuilder();

        if (directory.exists() && directory.isDirectory()) {
            findJars(directory, classpath);
        }

        try (FileWriter writer = new FileWriter(outputFile)) {
            writer.write(classpath.toString());
        } catch (IOException e) {
            logger.logError("Error while writing classpath to file", e);
            e.printStackTrace(); // not secure need upadte
        }
    }

    /**
     * Cherche et renvoie le jar demandé.
     * @param dir directory où il faut chercher le jar
     * @param classpath chemin des param et autre
     */
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