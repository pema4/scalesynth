package com.pema4.scalesynth.gui.views;

import com.pema4.scalesynth.gui.services.ScaleService;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;

import java.io.FileInputStream;
import java.io.IOException;

public class ScaleSettingsView extends Parent {
    private final ScaleService scaleService;

    public ScaleSettingsView(ScaleService scaleService) {
        this.scaleService = scaleService;
        var ui = createUI();
        getChildren().add(ui);
    }

    private Node createUI() {
        var reset = new Button("Reset");
        reset.setOnAction(event -> {
            scaleService.disable();
        });

        var open = new Button("Open");
        open.setOnAction(this::readScale);

        var masterFreq = new Slider(440, 480, 440);
        masterFreq.valueProperty().addListener((observable, oldValue, newValue) -> {
            scaleService.setMasterFreq(newValue.doubleValue());
        });

        return new HBox(
                5,
                reset,
                open,
                masterFreq);
    }

    private void readScale(ActionEvent actionEvent) {
        try {
            var openFileDialog = new FileChooser();
            var extension = new FileChooser.ExtensionFilter("Scala tuning file", "*.scl");
            openFileDialog.getExtensionFilters().addAll(extension);
            var file = openFileDialog.showOpenDialog(getScene().getWindow());
            if (file != null)
                scaleService.enable(new FileInputStream(file));
        } catch (IOException | IllegalArgumentException e) {
            new Alert(Alert.AlertType.ERROR, e.getMessage()).showAndWait();
        }
    }
}
