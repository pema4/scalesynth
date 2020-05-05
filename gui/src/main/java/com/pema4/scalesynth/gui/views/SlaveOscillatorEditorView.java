package com.pema4.scalesynth.gui.views;

import com.pema4.scalesynth.ScaleSynthParameters;
import com.pema4.scalesynth.gui.controls.IntegerParameterTransform;
import com.pema4.scalesynth.gui.controls.Knob;
import com.pema4.scalesynth.gui.controls.LinearParameterTransform;
import com.pema4.scalesynth.gui.controls.OnOffButton;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;

public class SlaveOscillatorEditorView extends Parent {
    private final ScaleSynthParameters parameters;

    public SlaveOscillatorEditorView(ScaleSynthParameters parameters) {
        this.parameters = parameters;
        var ui = createUI();
        getChildren().add(ui);
    }

    private Node createUI() {
        var gridPane = new GridPane();
        gridPane.setStyle(
                "-fx-vgap: 5; -fx-hgap: 5; -fx-padding: 8px; -fx-border-color: black;" +
                        "-fx-border-radius: 8px; -fx-background-radius: 8px; -fx-border-width: 2px;" +
                        "-fx-background-color: linear-gradient(#e5ff84, #d5ff86);");

        var accentColor = Color.valueOf("#6eb238");

        var mix = parameters.slaveMix;
        var mixKnob = new Knob<>(mix, LinearParameterTransform.of(mix), accentColor);
        gridPane.add(mixKnob, 0, 0);

        var pw = parameters.slavePW;
        var pwKnob = new Knob<>(pw, LinearParameterTransform.of(pw), accentColor);
        gridPane.add(pwKnob, 1, 0);

        var octave = parameters.slaveOctave;
        var octaveKnob = new Knob<>(octave, IntegerParameterTransform.of(octave), accentColor);
        gridPane.add(octaveKnob, 0, 1);

        var semi = parameters.slaveSemi;
        var semiKnob = new Knob<>(semi, IntegerParameterTransform.of(semi), accentColor);
        gridPane.add(semiKnob, 1, 1);

        var fine = parameters.slaveFine;
        var fineKnob = new Knob<>(fine, IntegerParameterTransform.of(fine), accentColor);
        gridPane.add(fineKnob, 2, 1);

        var sync = parameters.syncEnabled;
        var syncButton = new OnOffButton(sync, accentColor);
        gridPane.add(syncButton,  3, 1);

        return gridPane;
    }
}
