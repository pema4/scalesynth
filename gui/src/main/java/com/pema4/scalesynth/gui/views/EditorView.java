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
        var masterAmplitude = new DoubleParameterSlider(parameters.masterAmplitude);
        var slaveAmplitude = new DoubleParameterSlider(parameters.slaveAmplitude);
        var noiseAmplitude = new DoubleParameterSlider(parameters.noiseAmplitude);
        var slaveOctave = new DoubleParameterSlider(parameters.slaveOctave);
        var slaveSemi = new DoubleParameterSlider(parameters.slaveSemi);
        var slaveFine = new DoubleParameterSlider(parameters.slaveFine);
        var drift = new DoubleParameterSlider(parameters.drift);
        var masterMix = new DoubleParameterSlider(parameters.masterMix);
        var slaveMix = new DoubleParameterSlider(parameters.slaveMix);
        var masterPW = new DoubleParameterSlider(parameters.masterPW);
        var slavePW = new DoubleParameterSlider(parameters.slavePW);

        var ampEgAttackRate = new DoubleParameterSlider(parameters.ampEgAttackRate);
        var ampEgDecayRate = new DoubleParameterSlider(parameters.ampEgDecayRate);
        var ampEgSustainLevel = new DoubleParameterSlider(parameters.ampEgSustainLevel);
        var ampEgReleaseRate = new DoubleParameterSlider(parameters.ampEgReleaseRate);

        var ampAmplitude = new DoubleParameterSlider(parameters.ampAmplitude);

        var filterEgAttackRate = new DoubleParameterSlider(parameters.filterEgAttackRate);
        var filterEgDecayRate = new DoubleParameterSlider(parameters.filterEgDecayRate);
        var filterEgSustainLevel = new DoubleParameterSlider(parameters.filterEgSustainLevel);
        var filterEgReleaseRate = new DoubleParameterSlider(parameters.filterEgReleaseRate);
        var filterEgAmount = new DoubleParameterSlider(parameters.filterEgAmount);

        var filterCutoff = new DoubleParameterSlider(parameters.filterCutoff);
        var filterQ = new DoubleParameterSlider(parameters.filterQ);
        var filterMode = new DoubleParameterSlider(parameters.filterMode);
        var filterKeyboardTracking = new DoubleParameterSlider(parameters.filterKeyboardTracking);

        return new VBox(5,
                slaveAmplitude,
                masterAmplitude,
                noiseAmplitude,
                slaveOctave,
                slaveSemi,
                slaveFine,
                drift,
                masterMix,
                slaveMix,
                masterPW,
                slavePW,
                ampAmplitude,
                ampEgAttackRate,
                ampEgDecayRate,
                ampEgSustainLevel,
                ampEgReleaseRate,
                filterEgAttackRate,
                filterEgDecayRate,
                filterEgSustainLevel,
                filterEgReleaseRate,
                filterEgAmount,
                filterCutoff,
                filterQ,
                filterMode,
                filterKeyboardTracking
        );
    }
}
