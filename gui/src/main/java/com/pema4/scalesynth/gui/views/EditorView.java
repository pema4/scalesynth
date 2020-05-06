package com.pema4.scalesynth.gui.views;

import com.pema4.scalesynth.ScaleSynthParameters;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.TilePane;

import java.awt.*;

public class EditorView extends Parent {
    private final ScaleSynthParameters parameters;

    public EditorView(ScaleSynthParameters parameters) {
        this.parameters = parameters;
        var ui = createUI();
        getChildren().add(ui);
    }

    private Node createUI() {
        /*
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
        var unisonDetune = new DoubleParameterSlider(parameters.unisonDetune);
        var unisonStereo = new DoubleParameterSlider(parameters.unisonStereo);
        var unisonVoices = new IntegerParameterSlider(parameters.unisonVoices);

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
                unisonDetune,
                unisonStereo,
                unisonVoices,
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
         */

        var master = new MasterOscillatorEditorView(parameters);
        var slave = new SlaveOscillatorEditorView(parameters);
        var unison = new UnisonEditorView(parameters);
        var mixer = new MixerEditorView(parameters);
        var filter = new FilterEditorView(parameters);
        var filterEg = new FilterEgEditorView(parameters);
        var ampEg = new AmpEgEditorView(parameters);

        var pane = new GridPane();
        pane.setStyle("-fx-vgap: 5px; -fx-hgap: 5px; -fx-padding: 5px;");
        var row0 = new RowConstraints();
        var row1 = new RowConstraints();
        row0.setValignment(VPos.TOP);
        row1.setValignment(VPos.TOP);
        pane.getRowConstraints().addAll(row0, row1);
        pane.add(master, 0, 0);
        pane.add(slave, 0, 1);
        pane.add(unison, 1, 0, 1, 2);
        pane.add(mixer, 2, 0, 1, 2);
        pane.add(filter, 3, 0, 1, 2);
        pane.add(ampEg, 4, 0);
        pane.add(filterEg, 4, 1);


        return pane;
    }
}
