package xyz.aweirdwhale.installer;

import xyz.aweirdwhale.utils.log.logger;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class ClassPathGenerator {

    public static void main(String[] args) {
        String librariesDir = "libraries";
        String outputFile = "classpath.txt";
        generateClassPath(librariesDir, outputFile);
    }

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