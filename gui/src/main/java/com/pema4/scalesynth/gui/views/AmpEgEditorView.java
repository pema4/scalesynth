package com.pema4.scalesynth.gui.views;

import com.pema4.scalesynth.ScaleSynthParameters;
import com.pema4.scalesynth.gui.controls.Knob;
import com.pema4.scalesynth.gui.controls.LinearParameterTransform;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;

public class AmpEgEditorView extends Parent {
    private final ScaleSynthParameters parameters;

    public AmpEgEditorView(ScaleSynthParameters parameters) {
        this.parameters = parameters;
        var ui = createUI();
        getChildren().add(ui);
    }

    private Node createUI() {
        var gridPane = new GridPane();
        gridPane.setStyle(
                "-fx-vgap: 5; -fx-hgap: 5; -fx-padding: 8px; -fx-border-color: black;" +
                        "-fx-border-radius: 8px; -fx-background-radius: 8px; -fx-border-width: 2px;" +
                        "-fx-background-color: linear-gradient(#ffccd2, #ffd3e4);");

        var accentColor = Color.valueOf("#ea747a");

        var label = new Label("Amp EG");
        label.setStyle("-fx-font-weight: bold; -fx-font-size: 16px;");
        label.setTextFill(accentColor.darker());
        gridPane.add(label, 0 ,0, 5, 1);

        var attack = parameters.ampEgAttackRate;
        var attackKnob = new Knob<>(attack, LinearParameterTransform.of(attack).inverted(), accentColor);
        gridPane.add(attackKnob, 0, 1);

        var decay = parameters.ampEgDecayRate;
        var decayKnob = new Knob<>(decay, LinearParameterTransform.of(decay).inverted(), accentColor);
        gridPane.add(decayKnob, 1, 1);

        var sustain = parameters.ampEgSustainLevel;
        var sustainKnob = new Knob<>(sustain, LinearParameterTransform.of(sustain), accentColor);
        gridPane.add(sustainKnob, 2, 1);

        var release = parameters.ampEgReleaseRate;
        var releaseKnob = new Knob<>(release, LinearParameterTransform.of(release).inverted(), accentColor);
        gridPane.add(releaseKnob, 3, 1);

        var amp = parameters.ampAmplitude;
        var ampKnob = new Knob<>(amp, LinearParameterTransform.of(amp), accentColor);
        gridPane.add(ampKnob, 4, 1);

        return gridPane;
    }
}
