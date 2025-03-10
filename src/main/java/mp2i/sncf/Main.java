package mp2i.sncf;

import mp2i.sncf.utils.security.HashPwd;
import mp2i.sncf.utils.security.Login;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;

import static mp2i.sncf.uninstall.Uninstall.*;
import static mp2i.sncf.updater.Update.startUpdate;

/**
 * Launcher entry point
 **/
public class Main {

    private static final String CHANGELOG_PATH = getGameDir() + "/changelog.txt";


    public static void listenEntries(){
        // demande le nom d'utilisateur :
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

        String username = " ";
        String password;
        String digest = " ";

        try {
            System.out.print("Entre ton pseudo >> ");
            username = reader.readLine();
            System.out.print("Entre ton mot de passe >> ");
            password = reader.readLine();

            //faut hash le mdp aussi
            try {
                digest = HashPwd.hash(password);
            } catch (NoSuchAlgorithmException e) {
                throw new RuntimeException(e);
            }
        } catch (IOException e) {
            System.out.println("Oups ! Il y'a une erreur causée par " + e.getCause() + " : " + e.getMessage());
        }

        List<String> li = new ArrayList<>();
        li.add(username);
        li.add(digest);

        tryLogging(li);
    }

    public static void tryLogging(List<String> entries) {

        int logged = Login.login(entries.getFirst(), entries.get(1), "http://217.154.9.109");

        if (logged == 200){
            // Bah lance le jeu ma belle
            Launcher.setUp(entries.getFirst());
        } else {
            System.out.println("⤫ Ré-essaie, si tu as oublié tes identifiants, ping moi sur discord");
            // Écoute encore pour les creds :
            listenEntries();
        }

        System.out.println(" ");
    }

    public static void main(String[] args) throws NoSuchAlgorithmException, IOException {
        System.out.println("""
               .-'''-. ,---.    ,---..-------.   .`````-.  .-./`) \s
              / _     \\|    \\  /    |\\  _(`)_ \\ /   ,-.  \\ \\ .-.')\s
             (`' )/`--'|  ,  \\/  ,  || (_ o._)|(___/  |   |/ `-' \\\s
            (_ o _).   |  |\\_   /|  ||  (_,_) /      .'  /  `-'`"`\s
             (_,_). '. |  _( )_/ |  ||   '-.-'   _.-'_.-'   .---. \s
            .---.  \\  :| (_ o _) |  ||   |     _/_  .'      |   | \s
            \\    `-'  ||  (_,_)  |  ||   |    ( ' )(__..--. |   | \s
             \\       / |  |      |  |/   )   (_{;}_)      | |   | \s
              `-...-'  '--'      '--'`---'    (_,_)-------' '---' \s
            """);

        /**
         * Si changelog.txt dans .smp2ix : affiche le contenu puis supprime changelog.txt
         * **/


        /** Regarde s'il y a des arguments :
         * --uninstall : supprime %appdata%/.smp2ix ou ~/.smp2ix selon l'os
         * --clean : supprime %appdata%/.smp2ix/mods ou ~/.smp2ix/mods selon l'os
         * --update : télécharge un nouveau jar
         * --load-skin : ouvre %appdata%/.smp2ix/skins ou ~/.smp2ix/skins selon l'os
         * --reach-presence true/false : lance reachPresence.java
         * --changelog : affiche le changelog
         * --creds : username:password
         * **/

        if (args.length > 0) {
            switch (args[0]) {
                case "--uninstall" -> uninstall();
                case "--clean" -> cleanMods();
                case "--update" -> updateJar();
//                case "--load-skin" -> openSkinsFolder();
                case "--changelog" -> printChangelog();
                case "--creds" -> {
                    if (args.length > 1) {
                        handleScriptedLogin(args[1]);
                    } else {
                        System.out.println("Hint : --creds pseudo:mot-de-passe");
                    }
                }

                default -> System.out.println("Argument inconnu : " + args[0]);
            }
        } else {
            listenEntries();
        }
    }

    // Implémente les méthodes selon ton besoin
    private static void uninstall() throws IOException {
        kaboom();
    }

    private static void cleanMods() throws IOException {
        modCleaning();
        listenEntries();
    }

    private static void updateJar() {
        startUpdate();
    }

    /**
     * Récup le changelog depuis gameDir/changelog.txt et print son contenu
     * **/
    private static void printChangelog() {


        Path path = Path.of(CHANGELOG_PATH);

        if (!Files.exists(path)) {
            System.out.println("Malheur ! Impossible de trouver le changelog !!");

            waitForUser();
        }

        try {
            String content = Files.readString(path, StandardCharsets.UTF_8);
            System.out.println("📜 Changelog :\n" + content);

            waitForUser();


        } catch (IOException e) {
            System.err.println("Oula il va falloir ping @Qomoxy ou @Aweirdwhale là... : " + e.getMessage());
            waitForUser();
        }
    }

    private static void waitForUser() {
        // Appuis sur une touche pour continuer :
        System.out.println("\n🔹 Appuis sur [Entrée] pour continuer...");
        Scanner scanner = new Scanner(System.in);
        scanner.nextLine();  // Attend une entrée

        listenEntries();
    }

    /**
     * permet de skip le moment ou faut taper pseudo/mdp
     * @param login : "username:password"
     * **/
    private static void handleScriptedLogin(String login){
        // coupe login en 2 strings à partir des :
        if (login == null || !login.contains(":")) {
            System.out.println("Utilisez \"username:password\"");
            return;
        }

        // Séparation du login en "username" et "password"
        String[] parts = login.split(":", 2);
        String username = parts[0];
        String password = parts[1];

        String digest;
        try {
            digest = HashPwd.hash(password);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }

        List<String> li = new ArrayList<>();
        li.add(username);
        li.add(digest);

        tryLogging(li);
    }


}
