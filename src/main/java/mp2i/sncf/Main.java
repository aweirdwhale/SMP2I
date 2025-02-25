package mp2i.sncf;


import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

/**
 * Launcher entry point
 **/
public class Main extends Application {
    @Override
    public void start(Stage stage) throws IOException, IOException {
        /*
         * App JavaFX (voir main.FXML et main.css),
         * à faire : page paramètres (selection de RAM / Cape et chemin du jeu)
         * pop-ups (updates / coming soon etc)
         * fonctionalité -> controller.java
         */



        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("main.fxml")));

        Scene scene = new Scene(root, 1280, 720);
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("styles/main.css")).toExternalForm());

        scene.getRoot().setStyle("-fx-background-color: #222222; -fx-label-color: #ff0000;");
        stage.setResizable(false);
        stage.getIcons().add(new Image("mp2i/sncf/images/icons/icon.png")); // fixed icon
        stage.setTitle("SMP2I");
        stage.setScene(scene);
        stage.show();


    }

    public static void main(String[] args) {
        Launcher.setUp("SnCf");
    }

}
