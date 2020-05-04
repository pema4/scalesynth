package com.pema4.scalesynth.gui.views;

import com.pema4.scalesynth.gui.services.SynthSerializationService;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;

import java.io.*;

public class SynthSerializationView extends Parent {
    private final SynthSerializationService serializationService;

    public SynthSerializationView(SynthSerializationService serializationService) {
        this.serializationService = serializationService;

        Node ui = createUI();
        getChildren().add(ui);
    }


    private void handleSave(ActionEvent event) {
        try {
            var saveFileDialog = new FileChooser();
            var extension = new FileChooser.ExtensionFilter("ScaleSynth preset file", "*.ssynth");
            saveFileDialog.getExtensionFilters().addAll(extension);
            var file = saveFileDialog.showSaveDialog(getScene().getWindow());
            if (file != null)
                serializationService.save(new FileOutputStream(file));
        } catch (ReflectiveOperationException | IOException | IllegalArgumentException e) {
            new Alert(Alert.AlertType.ERROR, e.getMessage()).showAndWait();
        }
    }

    private void handleOpen(ActionEvent event) {
        try {
            var openFileDialog = new FileChooser();
            var extension = new FileChooser.ExtensionFilter("ScaleSynth preset file", "*.ssynth");
            openFileDialog.getExtensionFilters().addAll(extension);
            var file = openFileDialog.showOpenDialog(getScene().getWindow());
            if (file != null)
                serializationService.open(new FileInputStream(file));
        } catch (ReflectiveOperationException | IOException | IllegalArgumentException e) {
            new Alert(Alert.AlertType.ERROR, e.getMessage()).showAndWait();
        }
    }

    private Node createUI() {
        var save = new Button("Save");
        save.setOnAction(this::handleSave);

        var open = new Button("Open");
        open.setOnAction(this::handleOpen);

        return new HBox(5, save, open);
    }
}