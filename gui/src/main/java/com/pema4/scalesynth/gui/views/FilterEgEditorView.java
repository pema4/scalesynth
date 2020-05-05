package com.pema4.scalesynth.gui.views;

import com.pema4.scalesynth.ScaleSynthParameters;
import com.pema4.scalesynth.gui.controls.Knob;
import com.pema4.scalesynth.gui.controls.LinearParameterTransform;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;

public class FilterEgEditorView extends Parent {
    private final ScaleSynthParameters parameters;

    public FilterEgEditorView(ScaleSynthParameters parameters) {
        this.parameters = parameters;
        var ui = createUI();
        getChildren().add(ui);
    }

    private Node createUI() {
        var gridPane = new GridPane();
        gridPane.setStyle(
                "-fx-vgap: 5; -fx-hgap: 5; -fx-padding: 8px; -fx-border-color: black;" +
                        "-fx-border-radius: 8px; -fx-background-radius: 8px; -fx-border-width: 2px;" +
                        "-fx-background-color: linear-gradient(#ffddbc, #ffd5c2);");

        var accentColor = Color.valueOf("#f08b66");

        var attack = parameters.filterEgAttackRate;
        var attackKnob = new Knob<>(attack, LinearParameterTransform.of(attack).inverted(), accentColor);
        gridPane.add(attackKnob, 0, 0);

        var decay = parameters.filterEgDecayRate;
        var decayKnob = new Knob<>(decay, LinearParameterTransform.of(decay).inverted(), accentColor);
        gridPane.add(decayKnob, 1, 0);

        var sustain = parameters.filterEgSustainLevel;
        var sustainKnob = new Knob<>(sustain, LinearParameterTransform.of(sustain), accentColor);
        gridPane.add(sustainKnob, 2, 0);

        var release = parameters.filterEgReleaseRate;
        var releaseKnob = new Knob<>(release, LinearParameterTransform.of(release).inverted(), accentColor);
        gridPane.add(releaseKnob, 3, 0);

        var amp = parameters.filterEgAmount;
        var ampKnob = new Knob<>(amp, LinearParameterTransform.of(amp), accentColor);
        gridPane.add(ampKnob, 4, 0);

        return gridPane;
    }
}
