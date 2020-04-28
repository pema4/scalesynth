package com.pema4.scalesynth.gui.views;

import com.pema4.scalesynth.ScaleSynthParameters;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.layout.VBox;

public class EditorView extends Parent {
    private final ScaleSynthParameters parameters;

    public EditorView(ScaleSynthParameters parameters) {
        this.parameters = parameters;
        var ui = createUI();
        getChildren().add(ui);
    }

    private Node createUI() {
        var octave = new FloatParameterSlider(parameters.getOctave());
        var volume = new FloatParameterSlider(parameters.getVolume());
        return new VBox(5,
                octave,
                volume
        );
    }
}
