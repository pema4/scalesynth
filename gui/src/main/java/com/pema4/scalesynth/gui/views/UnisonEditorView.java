package com.pema4.scalesynth.gui.views;

import com.pema4.scalesynth.ScaleSynthParameters;
import com.pema4.scalesynth.gui.controls.IntegerParameterTransform;
import com.pema4.scalesynth.gui.controls.Knob;
import com.pema4.scalesynth.gui.controls.LinearParameterTransform;
import com.pema4.scalesynth.gui.controls.LogarithmicParameterTransform;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;

public class UnisonEditorView extends Parent {
    private final ScaleSynthParameters parameters;

    public UnisonEditorView(ScaleSynthParameters parameters) {
        this.parameters = parameters;
        var ui = createUI();
        getChildren().add(ui);
    }

    private Node createUI() {
        var gridPane = new GridPane();
        gridPane.setStyle(
                "-fx-vgap: 5; -fx-hgap: 5; -fx-padding: 8px; -fx-border-color: black;" +
                        "-fx-border-radius: 8px; -fx-background-radius: 8px; -fx-border-width: 2px;" +
                        "-fx-background-color: linear-gradient(#bdffee, #c3faff);");

        var accentColor = Color.valueOf("#2ce9ee");

        var voices = parameters.unisonVoices;
        var voicesKnob = new Knob<>(voices, IntegerParameterTransform.of(voices), accentColor);
        gridPane.add(voicesKnob, 0, 0);

        var detune = parameters.unisonDetune;
        var detuneKnob = new Knob<>(detune, LinearParameterTransform.of(detune), accentColor);
        gridPane.add(detuneKnob, 0, 1);

        var width = parameters.unisonStereo;
        var widthKnob = new Knob<>(width, LinearParameterTransform.of(width), accentColor);
        gridPane.add(widthKnob, 0, 2);

        var drift = parameters.drift;
        var driftKnob = new Knob<>(drift, LinearParameterTransform.of(drift), accentColor);
        gridPane.add(driftKnob, 0, 3);

        return gridPane;
    }
}
