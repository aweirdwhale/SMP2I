package mp2i.sncf.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.util.Objects;

public class SettingsController {

    @FXML
    private Label ramValueLabel;
    @FXML
    private Slider ramSlider;
    @FXML
    private TextField gameFolderField;
    @FXML
    private Button openFolderButton;
    @FXML
    private Button changeSkinButton;
    @FXML
    private Button back;
    @FXML
    private ImageView skinPreview;

    public void initialize() {


        ramSlider.setMin(2);
        ramSlider.setMax(16);
        ramSlider.setValue(4);
        ramValueLabel.setText((int) ramSlider.getValue() + " Go");

        // Mettre à jour le label quand la valeur du slider change
        ramSlider.valueProperty().addListener((observable, oldValue, newValue) -> ramValueLabel.setText(newValue.intValue() + " Go"));

        // Rendre le champ gameFolder non éditable
        gameFolderField.setEditable(false);
        // On peut initialiser le chemin ici, par exemple :
        // gameFolderField.setText(setupEnvironment.getGameDir());
    }

    @FXML
    public void handleChangeSkin(ActionEvent event) {
        // Logique pour changer le skin depuis cet écran
    }

    @FXML
    public void handleOpenFolder(ActionEvent event) {
        try {
            File folder = new File(gameFolderField.getText());
            if (folder.exists() && folder.isDirectory()) {
                Desktop.getDesktop().open(folder);
            }
        } catch (IOException _) {

        }
    }

    @FXML
    public void handleMenu(ActionEvent event)  {
        try {
            // Correction du chemin du FXML (suppression du double slash)
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/mp2i/sncf/main.fxml"));
            Parent mainRoot = loader.load();
            Scene scene = new Scene(mainRoot);


            scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/mp2i/sncf/styles/main.css")).toExternalForm());

            Stage stage = (Stage) back.getScene().getWindow();
            stage.setTitle("SMP2I");


            // Si nécessaire, appliquer le CSS (décommente et ajuste le chemin)
            // scene.getStylesheets().add(getClass().getResource("/css/style.css").toExternalForm());

            stage.setScene(scene);
            stage.show();
        } catch (IOException _) {

        }
    }
}
