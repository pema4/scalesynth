package com.scalesynth.gui.views;

import com.scalesynth.gui.services.SynthSerializationService;
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
import java.io.FileOutputStream;

/**
 * Synth serialization view (just label and two buttons wrapped in HBox).
 */
public class SynthSerializationView extends Parent {
    private final SynthSerializationService serializationService;
    private File lastDirectory;

    public SynthSerializationView(SynthSerializationService serializationService) {
        this.serializationService = serializationService;

        var save = new Button("Save...");
        save.setOnAction(this::handleSave);

        var open = new Button("Load...");
        open.setOnAction(this::handleOpen);

        var hbox = new HBox(5, new Label("Settings:"), save, open);
        hbox.setAlignment(Pos.CENTER);
        getChildren().add(hbox);
    }


    private void handleSave(ActionEvent event) {
        try {
            var saveFileDialog = new FileChooser();
            var extension = new FileChooser.ExtensionFilter("ScaleSynth preset file", "*.ssynth");
            saveFileDialog.getExtensionFilters().addAll(extension);
            saveFileDialog.setInitialDirectory(lastDirectory);
            var file = saveFileDialog.showSaveDialog(getScene().getWindow());
            if (file != null) {
                lastDirectory = file.getParentFile();
                serializationService.save(new FileOutputStream(file));
            }
        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR, "File cannot be saved:\n" + e.getMessage()).showAndWait();
        }
    }

    private void handleOpen(ActionEvent event) {
        try {
            var openFileDialog = new FileChooser();
            var extension = new FileChooser.ExtensionFilter("ScaleSynth preset file", "*.ssynth");
            openFileDialog.getExtensionFilters().addAll(extension);
            openFileDialog.setInitialDirectory(lastDirectory);
            var file = openFileDialog.showOpenDialog(getScene().getWindow());
            if (file != null) {
                lastDirectory = file.getParentFile();
                serializationService.open(new FileInputStream(file));
            }
        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR, "File cannot be opened:\n" + e.getMessage()).showAndWait();
        }
    }
}
