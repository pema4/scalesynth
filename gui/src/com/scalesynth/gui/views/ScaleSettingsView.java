package com.scalesynth.gui.views;

import com.scalesynth.gui.services.ScaleService;
import javafx.event.ActionEvent;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * Scale settings view (just label and two buttons wrapped in HBox).
 */
public class ScaleSettingsView extends Parent {
    private final ScaleService scaleService;
    private final Button resetButton;
    private File lastDirectory;

    public ScaleSettingsView(ScaleService scaleService) {
        this.scaleService = scaleService;
        resetButton = new Button("Reset");
        resetButton.setOnAction(event -> {
            this.scaleService.disable();
            resetButton.setDisable(true);
        });
        resetButton.setDisable(true);

        Button openButton = new Button("Open...");
        openButton.setOnAction(this::readScale);

        var hbox = new HBox(5, new Label("Scale:"), resetButton, openButton);
        hbox.setAlignment(Pos.CENTER);
        getChildren().add(hbox);
    }

    private void readScale(ActionEvent actionEvent) {
        try {
            var openFileDialog = new FileChooser();
            var extension = new FileChooser.ExtensionFilter("Scala tuning file", "*.scl");
            openFileDialog.getExtensionFilters().addAll(extension);
            openFileDialog.setInitialDirectory(lastDirectory);
            var file = openFileDialog.showOpenDialog(getScene().getWindow());
            if (file != null) {
                lastDirectory = file.getParentFile();
                scaleService.enable(new FileInputStream(file));
                resetButton.setDisable(false);
            }
        } catch (IOException | IllegalArgumentException e) {
            new Alert(Alert.AlertType.ERROR, e.getMessage()).showAndWait();
        }
    }
}
