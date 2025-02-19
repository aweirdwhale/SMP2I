package xyz.aweirdwhale.download;

import xyz.aweirdwhale.utils.exceptions.DownloadException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

/**
 * Regarder si l'utilisateur à bien tout les mods et ou la bonne version de Fabric
 * SI OUi : Ne rien faire
 * SINON : installer ce qui lui manque (au max tout et au minimun mettre un jour un mod.
 * Info :
 * Version 1.21.4 de Minecraft sous Fabrics
 *  Mods : Lithium, Sodium et Prosphor
 */

public class Downloader {


    /**
     * Télécharge un fichier depuis l'URL spécifiée et le sauvegarde dans le chemin donné.
     *
     * @param fileUrl  L'URL du fichier à télécharger.
     * @param SavePath Le chemin où le fichier sera sauvegardé.
     * @throws DownloadException En cas d'erreur de téléchargement.
     */
    public static void downloadFile(String fileUrl, String SavePath) throws DownloadException {
        try {
            URI uri = URI.create(fileUrl);
            URL url = uri.toURL();
            try (ReadableByteChannel channel = Channels.newChannel(url.openStream());
                FileOutputStream fos = new FileOutputStream(SavePath)) {
                fos.getChannel().transferFrom(channel, 0, Long.MAX_VALUE);
            }


    } catch (IOException e) {
            throw new DownloadException("Erreur lors du téléchargement de " + fileUrl + " : " + e.getMessage());
        }
    }


    /**
     * Télécharge et installe Minecraft 1.21.4 dans le dossier spécifié
     *
     * @param modUrl URL du fichier de Minecraft 1.21.4.
     * @param modsDir   Dossier d'installation de Minecraft.
     * @throws DownloadException En cas d'erreur lors du téléchargement.
     */
    public static void downloadMod(String[] modUrl, String modsDir) throws DownloadException {
        File modsDirFile = new File(modsDir);
        if (!modsDirFile.exists()) {
            modsDirFile.mkdirs();
        }

        int totalMods = modUrl.length;
        for (String url : modUrl) {
            String fileName = url.substring(url.lastIndexOf("/") + 1);
            String savePath = modsDir + File.separator + fileName;

            try {
                downloadFile(url, savePath);
            } catch (DownloadException e) {
                throw new DownloadException("Erreur lors du téléchargement des mods : " + e.getMessage());
            }

        }

    }

    /**
     * Télécharge et installe Minecraft 1.21.4 dans le dossier spécifié.
     *
     * @param MinecraftUrl URL du fichier de Minecraft 1.21.4.
     * @param installDir   Dossier d'installation de Minecraft.
     * @throws DownloadException En cas d'erreur lors du téléchargement.
     */
    public static void installMinecraftandMod(String MinecraftUrl, String installDir) throws DownloadException {
        File modsDirFile = new File(installDir);
        if (!modsDirFile.exists()) {
            modsDirFile.mkdirs();
        }

        String fileName = "minecraft_1.12.4_Fabric_SMP004.jar";
        String savePath = modsDirFile.getAbsolutePath() + File.separator + fileName;

        try {
            downloadFile(MinecraftUrl, savePath);


        } catch (DownloadException e) {
            throw new DownloadException("Erreur lors du téléchargement de Minecraft 1.21.4 : " + e.getMessage());
        }

    }

}
