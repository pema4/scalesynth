package com.pema4.scalesynth.gui.views;

import com.pema4.scalesynth.ScaleSynthParameters;
import com.pema4.scalesynth.gui.controls.*;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;

public class MixerEditorView extends Parent {
    private final ScaleSynthParameters parameters;

    public MixerEditorView(ScaleSynthParameters parameters) {
        this.parameters = parameters;
        var ui = createUI();
        getChildren().add(ui);
    }

    private Node createUI() {
        var gridPane = new GridPane();
        gridPane.setStyle(
                "-fx-vgap: 5; -fx-hgap: 5; -fx-padding: 8px; -fx-border-color: black;" +
                "-fx-border-radius: 8px; -fx-background-radius: 8px; -fx-border-width: 2px;" +
                "-fx-background-color: linear-gradient(#c4ffdf, #c5ffd4);");

        var accentColor = Color.valueOf("#35d295");

        var label = new Label("Mixer");
        label.setStyle("-fx-font-weight: bold; -fx-font-size: 16px;");
        label.setTextFill(accentColor.darker());
        gridPane.add(label, 0 ,0, 2, 1);

        var master = parameters.masterAmplitude;
        var mixKnob = new Knob<>(master, LogarithmicParameterTransform.of(master), accentColor);
        gridPane.add(mixKnob, 0, 1);

        var slave = parameters.slaveAmplitude;
        var slaveKnob = new Knob<>(slave, LogarithmicParameterTransform.of(slave), accentColor);
        gridPane.add(slaveKnob, 0, 2);

        var noise = parameters.noiseAmplitude;
        var noiseKnob = new Knob<>(noise, LogarithmicParameterTransform.of(noise), accentColor);
        gridPane.add(noiseKnob, 0, 3);

        return gridPane;
    }
}
