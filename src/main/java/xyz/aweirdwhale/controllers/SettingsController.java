package xyz.aweirdwhale.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;

public class SettingsController {

    public Label ramValueLabel;
    public Button openFolderButton;
    public ImageView skinPreview;
    @FXML
    private Slider ramSlider;
    @FXML
    private TextField gameFolderField;
    @FXML
    private Button changeSkinButton;

    // Méthode appelée lors de l'initialisation (optionnelle)
    public void initialize() {
        ramSlider.setMax(16);
    }

    @FXML
    public void handleChangeSkin() {
        // Logique pour changer le skin depuis cet écran
    }

}
