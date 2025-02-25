package mp2i.sncf.installer;

import java.io.File;

public class setUpDirectories {

    public static String getGameDirectory() {
        String path = createGameDirectory();

        // crée les sous dossiers
        createSubDirectories(path, "mods");
        createSubDirectories(path, "assets");
        createSubDirectories(path, "libraries");
        createSubDirectories(path, "versions");
        createSubDirectories(path+"/versions", "1.21.4");
        createSubDirectories(path+"/versions", "fabric-loader");

        return path;
    }

    public static String createGameDirectory() {
        String home = System.getProperty("user.home"); // root
        String os = System.getProperty("os.name").toLowerCase(); // diff unix / windows

        String path; // gameDir

        if (os.contains("win")) {
            path = System.getenv("APPDATA") + "/.smp2ix";
        } else {
            path = home + "/.smp2ix";
        }

        return path;
    }

    public static void createSubDirectories(String parent, String child) {
        File dir = new File(parent + "/" + child);
        
        if (!dir.exists()) { // Si ça existe on s'en fiche
            dir.mkdirs();
            System.out.println(dir.getAbsolutePath());
        }
    }
}
