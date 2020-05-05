package com.pema4.scalesynth.gui.views;

import com.pema4.scalesynth.ScaleSynthParameters;
import com.pema4.scalesynth.gui.controls.Knob;
import com.pema4.scalesynth.gui.controls.LinearParameterTransform;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;

public class MasterOscillatorEditorView extends Parent {
    private final ScaleSynthParameters parameters;

    public MasterOscillatorEditorView(ScaleSynthParameters parameters) {
        this.parameters = parameters;
        var ui = createUI();
        getChildren().add(ui);
    }

    private Node createUI() {
        var gridPane = new GridPane();
        gridPane.setStyle(
                "-fx-vgap: 5; -fx-hgap: 5; -fx-padding: 8px; -fx-border-color: black;" +
                "-fx-border-radius: 8px; -fx-background-radius: 8px; -fx-border-width: 2px;" +
                "-fx-background-color: linear-gradient(#fbffcd, #fff7cb);");

        var accentColor = Color.valueOf("#FFFF77");

        var mix = parameters.masterMix;
        var mixKnob = new Knob<>(mix, LinearParameterTransform.of(mix), accentColor);
        gridPane.add(mixKnob, 0, 0);

        var pw = parameters.masterPW;
        var pwKnob = new Knob<>(pw, LinearParameterTransform.of(pw), accentColor);
        gridPane.add(pwKnob, 1, 0);

        return gridPane;
    }
}
