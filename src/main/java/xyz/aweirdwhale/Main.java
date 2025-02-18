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
        * App JavaFX avec un bouton params en haut à droite,
        * un bouton connection et un bouton jouer
        */

        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("main.fxml")));

        Scene scene = new Scene(root, 1280, 720);

        stage.setTitle("SMP2I");
        stage.setScene(scene);
        stage.show();




    }

    public static void main(String[] args) {
        launch();
    }

}