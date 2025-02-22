package xyz.aweirdwhale;

import xyz.aweirdwhale.utils.exceptions.LaunchException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Launcher {

    /**
     * Lance Minecraft avec les mods.
     *
     * @param gameDir directory of minecraft in user pc
     * @param assetsDir directorie of assets
     * @param mainClass the mainclass of the game to launch
     * @param jars all mods to get
     * @param server the attribute of the serveur
     * @param port the public port of the serveur or ip
     * @param pseudo the pseudo of the player
     * @throws LaunchException erreur of launch
     */

    public static void launchMinecraft(String gameDir, String assetsDir, String mainClass, List<String> jars, String server, String port, String pseudo) throws LaunchException {
        try {
            List<String> command = new ArrayList<>();
            command.add("java");
            command.add("-Xmx2G"); // ça serait bien de pouvoir le moduler car les mods ça consomme et les dernière vision.
            command.add("-cp");

            String classpath = String.join(File.pathSeparator, jars);
            command.add(classpath);
            command.add(mainClass);

            command.add("--gameDir");
            command.add(gameDir);
            command.add("--assetsDir");
            command.add(assetsDir);
            command.add("--server");
            command.add(server);
            command.add("--port");
            command.add(port);
            command.add("--username");
            command.add(pseudo);

            ProcessBuilder builder = new ProcessBuilder(command);
            command.add(classpath);
            command.add(mainClass);
            builder.inheritIO();
            Process process = builder.start(); // you need to keep it to use it after call im
        } catch (IOException e) {
            throw new LaunchException("Erreur lors du lancement du jeu : " + e.getMessage());
        }
    }
}