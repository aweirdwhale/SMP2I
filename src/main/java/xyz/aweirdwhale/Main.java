package xyz.aweirdwhale;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class Main  extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        /*
        * App JavaFX (voir main.FXML et main.css),
        * à faire : page paramètres (selection de RAM / Cape et chemin du jeu)
        * pop-ups (updates / coming soon etc)
        * fonctionalité -> controller.java
        */

        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("main.fxml")));

        Scene scene = new Scene(root, 1280, 720);
        scene.getStylesheets().add(getClass().getResource("styles/main.css").toExternalForm());

        scene.getRoot().setStyle("-fx-background-color: #222222;");
        stage.setResizable(false);
        stage.setTitle("SMP2I");
        stage.setScene(scene);
        stage.show();




    }

    public static void main(String[] args) {
        launch();
    }

}