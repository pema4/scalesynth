package com.pema4.scalesynth.gui.views;

import com.pema4.scalesynth.gui.services.ScaleService;
import javafx.event.ActionEvent;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class ScaleSettingsView extends Parent {
    private final ScaleService scaleService;
    private final Button resetButton;
    private File lastDirectory;

    public ScaleSettingsView(ScaleService scaleService) {
        this.scaleService = scaleService;
        resetButton = new Button("Reset scale");
        resetButton.setOnAction(event -> {
            this.scaleService.disable();
            resetButton.setDisable(true);
        });
        resetButton.setDisable(true);

        Button openButton = new Button("Open scale...");
        openButton.setOnAction(this::readScale);

        var ui = new HBox(5, resetButton, openButton);
        getChildren().add(ui);
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
