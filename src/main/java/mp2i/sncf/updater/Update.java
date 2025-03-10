package mp2i.sncf.updater;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.*;

import static mp2i.sncf.installer.Downloader.downloadFile;
import static mp2i.sncf.installer.setUpDirectories.getGameDirectory;
import static mp2i.sncf.utils.database.requestDatabase.getHttpURLConnection;
import static mp2i.sncf.utils.infos.Version.LAUNCHER_VERSION;

public class Update {

    /**
     * Requête à http://217.154.9.109:6969/updater
     * Réponse : changelog.txt
     *           false
     *
     * Si true : télécharge le nouveau jar (A), stocke le changelog dans .smp2ix, clear le terminal et lance (A) dans une autre fenêtre, puis s'auto-détruit
     * **/


    private static final String UPDATE_URL = "http://217.154.9.109:6969/updater";
    private static final String DOWNLOAD_URL = "http://217.154.9.109:6969/public/launcher/latest/SMP2I.jar";
    private static final String CHANGELOG = "http://217.154.9.109:6969/public/changelog.txt";


    public static void startUpdate() {
        try {
            if (checkForUpdate()) {
                System.out.println("Téléchargement en cours...");
                downloadNewVersion();
                saveChangelog();
                restartApplication();
            }
        } catch (Exception e) {
            System.err.println("Erreur lors de la mise à jour : " + e.getMessage());
        }
    }

    /**
     * Vérifie si une mise à jour est disponible.
     *
     * envoie {version:LAUNCHER_VERSION} à UPDATE_URL
     *
     * -> express : res.send("true")
     */
    public static boolean checkForUpdate() throws IOException, URISyntaxException {
        try{
            HttpURLConnection co = getHttpURLConnection(UPDATE_URL, "{\"version\":"+"\""+LAUNCHER_VERSION+"\"}", "POST");
            if(co.getResponseCode()==200){
                System.out.println("Vérification de la version en cours...");

                BufferedReader in = new BufferedReader(new InputStreamReader(co.getInputStream()));
                String response = in.readLine();  // true : false
                in.close();

                if ("true".equals(response.trim())){
                    System.out.println("Une mise à jour est disponible ! je te l'installe de suite, ne bouge pas :)");
                    return true;
                } else {
                    System.out.println("Tout est à jour ! Tu peux jouer tranquillou !");
                    return false;
                }
            } else {
                System.out.println("Il y a eu une erreur au moment de joindre le serveur :( (serveur éteint ou connexion instable)");
                return false;
            }

        } catch (URISyntaxException e) {
            System.out.println("Il y a eu une erreur au moment de joindre le serveur :( (serveur éteint ou connexion instable)");
            return false;
        }


    }

    /**
     * Télécharge le nouveau JAR et le sauvegarde.
     */
    public static void downloadNewVersion() throws IOException {
        System.out.println("Mise à jour en cours...");

        String pwd = System.getProperty("user.dir");
        downloadFile(DOWNLOAD_URL, pwd+"/SMP2I.jar", true); // télécharge dans au même endroit
    }

    /**
     * Télécharge et sauvegarde le changelog.
     */
    public static void saveChangelog() throws IOException {
        downloadFile(CHANGELOG, getGameDirectory()+"/changelog.txt", true);
    }

    /**
     * Relance la nouvelle version et supprime l'ancienne.
     */
    public static void restartApplication() throws IOException, InterruptedException {
        // Supprimer l'ancien JAR
        Thread.sleep(3000); // regle des 3sec pour etre sur
        Files.deleteIfExists(Path.of(getCurrentJarPath()));
        System.out.println("Suppression de l'ancienne version.");

        System.out.println("Vous pouvez maintenant lancer le launcher avec 'java -jar chemin/vers/SMP2I.jar --changelog' ! ");

        // Quitter l'application actuelle
        System.exit(0);
    }

    /**
     * Trouve le chemin du fichier JAR actuel.
     */
    private static String getCurrentJarPath() {
        try {
            return new File(Update.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getAbsolutePath();
        } catch (Exception e) {
            throw new RuntimeException("Impossible de déterminer le chemin du JAR actuel. Vous allez devoir supprimer à la main.");
        }
    }
}
