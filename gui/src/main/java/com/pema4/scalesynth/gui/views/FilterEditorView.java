package com.pema4.scalesynth.gui.views;

import com.pema4.scalesynth.ScaleSynthParameters;
import com.pema4.scalesynth.gui.controls.Knob;
import com.pema4.scalesynth.gui.controls.LinearParameterTransform;
import com.pema4.scalesynth.gui.controls.LogarithmicParameterTransform;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

public class FilterEditorView extends Parent {
    private final ScaleSynthParameters parameters;

    public FilterEditorView(ScaleSynthParameters parameters) {
        this.parameters = parameters;
        var ui = createUI();
        getChildren().add(ui);
    }

    private Node createUI() {
        var gridPane = new GridPane();
        gridPane.setStyle(
                "-fx-vgap: 5; -fx-hgap: 5; -fx-padding: 8px; -fx-border-color: black;" +
                "-fx-border-radius: 8px; -fx-background-radius: 8px; -fx-border-width: 2px;" +
                "-fx-background-color: linear-gradient(lightblue, lightskyblue);");

        var accentColor = Color.valueOf("#65B9FF");

        var label = new Label("SVF Filter");
        label.setStyle("-fx-font-weight: bold; -fx-font-size: 16px;");
        label.setTextFill(accentColor.darker());
        gridPane.add(label, 0 ,0, 2, 1);

        var cutoff = parameters.filterCutoff;
        var cutoffKnob = new Knob<>(cutoff, LogarithmicParameterTransform.of(cutoff), accentColor);
        gridPane.add(cutoffKnob, 0, 1);

        var q = parameters.filterQ;
        var qKnob = new Knob<>(q, LinearParameterTransform.of(q), accentColor);
        gridPane.add(qKnob, 1, 1);

        var mode = parameters.filterMode;
        var modeKnob = new Knob<>(mode, LinearParameterTransform.of(mode), accentColor);
        gridPane.add(modeKnob, 0, 2);

        var kb = parameters.filterKeyboardTracking;
        var kbKnob = new Knob<>(kb, LinearParameterTransform.of(kb), accentColor);
        gridPane.add(kbKnob, 1, 2);

        return gridPane;
    }
}
