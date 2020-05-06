package com.pema4.scalesynth.gui.views;

import com.pema4.scalesynth.ScaleSynthParameters;
import com.pema4.scalesynth.gui.controls.*;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import javafx.scene.paint.Color;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class EditorView extends Parent {
    private final ScaleSynthParameters parameters;
    private static final String BASE_EDITOR_STYLE =
            "-fx-vgap: 5; -fx-hgap: 5; -fx-padding: 4px; -fx-border-radius: 8px;" +
            "-fx-background-radius: 8px; -fx-border-width: 2px;" +
            "-fx-border-color: linear-gradient(#646464 0%, #4f4f4f 7%);" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 3, 0.1, 0, 0);";
    private static final String BASE_LABEL_STYLE = "-fx-font-weight: bold; -fx-font-size: 16px;";

    public EditorView(ScaleSynthParameters parameters) {
        this.parameters = parameters;
        var ui = createUI();
        getChildren().add(ui);
    }

    private Node createUI() {
        var pane = new GridPane();
        pane.setStyle("-fx-vgap: 5px; -fx-hgap: 5px; -fx-padding: 5px;");

        var rows = Stream.generate(RowConstraints::new).peek(x -> {
            x.setValignment(VPos.TOP);
            //x.setVgrow(Priority.ALWAYS);
        }).limit(3).toArray(RowConstraints[]::new);
        pane.getRowConstraints().addAll(rows);

        pane.add(createFirstOscillatorEditor(), 0, 0);
        pane.add(createSecondOscillatorEditor(), 0, 1, 1, 2);
        pane.add(createUnisonEditor(), 1, 0, 1, 3);
        pane.add(createMixerEditor(), 2, 0, 1, 3);
        pane.add(createFilterEditor(), 3, 0, 1, 3);
        pane.add(createAmpEgEditor(), 4, 0);
        pane.add(createFilterEgEditor(), 4, 1);

        return pane;
    }

    private List<ColumnConstraints> createColumns(int n) {
        return Stream.generate(ColumnConstraints::new).peek(x -> {
            x.setHgrow(Priority.ALWAYS);
            x.setHalignment(HPos.CENTER);
        }).limit(n).collect(Collectors.toList());
    }

    private List<RowConstraints> createRows(int n) {
        var rows = Stream.generate(RowConstraints::new).peek(x -> {
            x.setVgrow(Priority.ALWAYS);
            x.setValignment(VPos.CENTER);
        }).limit(n).collect(Collectors.toList());
        rows.get(0).setVgrow(Priority.NEVER);
        return rows;
    }

    private Node createFirstOscillatorEditor() {
        var pane = new GridPane();
        pane.setStyle(BASE_EDITOR_STYLE + "-fx-background-color: linear-gradient(#e5ff84, #d5ff86);");
        pane.getColumnConstraints().setAll(createColumns(2));
        pane.getRowConstraints().setAll(createRows(2));

        var accentColor = Color.valueOf("#07e200");

        var label = new Label("Oscillator A");
        label.setStyle(BASE_LABEL_STYLE);
        label.setTextFill(accentColor.darker());
        pane.add(label, 0 ,0, 2, 1);

        var mix = this.parameters.masterMix;
        var mixKnob = new Knob<>(mix, LinearParameterTransform.of(mix), accentColor);
        pane.add(mixKnob, 0, 1);

        var pw = this.parameters.masterPW;
        var pwKnob = new Knob<>(pw, LinearParameterTransform.of(pw), accentColor);
        pane.add(pwKnob, 1, 1);

        return pane;
    }

    private Node createSecondOscillatorEditor() {
        var pane = new GridPane();
        pane.setStyle(BASE_EDITOR_STYLE + "-fx-background-color: linear-gradient(#c9f4a5, #bef4a8);");
        pane.getColumnConstraints().addAll(createColumns(3));
        pane.getRowConstraints().addAll(createRows(3));

        var accentColor = Color.valueOf("#00db65");

        var label = new Label("Oscillator B");
        label.setStyle(BASE_LABEL_STYLE);
        label.setTextFill(accentColor.darker());
        pane.add(label, 0 ,0, 4, 1);

        var mix = parameters.slaveMix;
        var mixKnob = new Knob<>(mix, LinearParameterTransform.of(mix), accentColor);
        pane.add(mixKnob, 0, 1);

        var pw = parameters.slavePW;
        var pwKnob = new Knob<>(pw, LinearParameterTransform.of(pw), accentColor);
        pane.add(pwKnob, 1, 1);

        var sync = parameters.syncEnabled;
        var syncButton = new OnOffButton(sync, accentColor);
        pane.add(syncButton,  2, 1);

        var octave = parameters.slaveOctave;
        var octaveKnob = new Knob<>(octave, IntegerParameterTransform.of(octave), accentColor);
        pane.add(octaveKnob, 0, 2);

        var semi = parameters.slaveSemi;
        var semiKnob = new Knob<>(semi, IntegerParameterTransform.of(semi), accentColor);
        pane.add(semiKnob, 1, 2);

        var fine = parameters.slaveFine;
        var fineKnob = new Knob<>(fine, IntegerParameterTransform.of(fine), accentColor);
        pane.add(fineKnob, 2, 2);

        return pane;
    }

    private Node createUnisonEditor() {
        var pane = new GridPane();
        pane.setStyle(BASE_EDITOR_STYLE + "-fx-background-color: linear-gradient(#c4ffdf, #c5ffd4);");
        pane.getColumnConstraints().addAll(createColumns(1));
        pane.getRowConstraints().addAll(createRows(5));

        var accentColor = Color.valueOf("#35d295");

        var label = new Label("Unison");
        label.setStyle(BASE_LABEL_STYLE);
        label.setTextFill(accentColor.darker());
        pane.add(label, 0 ,0);

        var voices = parameters.unisonVoices;
        var voicesKnob = new Knob<>(voices, IntegerParameterTransform.of(voices), accentColor);
        pane.add(voicesKnob, 0, 1);

        var detune = parameters.unisonDetune;
        var detuneKnob = new Knob<>(detune, LinearParameterTransform.of(detune), accentColor);
        pane.add(detuneKnob, 0, 2);

        var width = parameters.unisonStereo;
        var widthKnob = new Knob<>(width, LinearParameterTransform.of(width), accentColor);
        pane.add(widthKnob, 0, 3);

        var drift = parameters.drift;
        var driftKnob = new Knob<>(drift, LinearParameterTransform.of(drift), accentColor);
        pane.add(driftKnob, 0, 4);

        return pane;
    }


    private Node createMixerEditor() {
        var pane = new GridPane();
        pane.setStyle(BASE_EDITOR_STYLE + "-fx-background-color: linear-gradient(#bdffee, #c3faff);");
        pane.getColumnConstraints().addAll(createColumns(1));
        pane.getRowConstraints().addAll(createRows(4));

        var accentColor = Color.valueOf("#00d5d2");

        var label = new Label("Mixer");
        label.setStyle(BASE_LABEL_STYLE);
        label.setTextFill(accentColor.darker());
        pane.add(label, 0 ,0);

        var master = parameters.masterAmplitude;
        var mixKnob = new Knob<>(master, LogarithmicParameterTransform.of(master), accentColor);
        pane.add(mixKnob, 0, 1);

        var slave = parameters.slaveAmplitude;
        var slaveKnob = new Knob<>(slave, LogarithmicParameterTransform.of(slave), accentColor);
        pane.add(slaveKnob, 0, 2);

        var noise = parameters.noiseAmplitude;
        var noiseKnob = new Knob<>(noise, LogarithmicParameterTransform.of(noise), accentColor);
        pane.add(noiseKnob, 0, 3);

        return pane;
    }

    private Node createFilterEditor() {
        var pane = new GridPane();
        pane.setStyle(BASE_EDITOR_STYLE + "-fx-background-color: linear-gradient(lightblue, lightskyblue);");
        pane.getColumnConstraints().addAll(createColumns(1));
        pane.getRowConstraints().addAll(createRows(5));

        var accentColor = Color.valueOf("#65B9FF");

        var label = new Label("SVF Filter");
        label.setStyle(BASE_LABEL_STYLE);
        label.setTextFill(accentColor.darker());
        pane.add(label, 0, 0);

        var cutoff = parameters.filterCutoff;
        var cutoffKnob = new Knob<>(cutoff, LogarithmicParameterTransform.of(cutoff), accentColor);
        pane.add(cutoffKnob, 0, 1);

        var q = parameters.filterQ;
        var qKnob = new Knob<>(q, LinearParameterTransform.of(q), accentColor);
        pane.add(qKnob, 0, 2);

        var mode = parameters.filterMode;
        var modeKnob = new Knob<>(mode, LinearParameterTransform.of(mode), accentColor);
        pane.add(modeKnob, 0, 3);

        var kb = parameters.filterKeyboardTracking;
        var kbKnob = new Knob<>(kb, LinearParameterTransform.of(kb), accentColor);
        pane.add(kbKnob, 0, 4);

        return pane;
    }

    private Node createAmpEgEditor() {
        var pane = new GridPane();
        pane.setStyle(BASE_EDITOR_STYLE + "-fx-background-color: linear-gradient(#ffccd2, #ffd3e4);");
        pane.getColumnConstraints().addAll(createColumns(5));
        pane.getRowConstraints().addAll(createRows(2));

        var accentColor = Color.valueOf("#ea747a");

        var label = new Label("Amp EG");
        label.setStyle(BASE_LABEL_STYLE);
        label.setTextFill(accentColor.darker());
        pane.add(label, 0 ,0, 5, 1);

        var attack = parameters.ampEgAttackRate;
        var attackKnob = new Knob<>(attack, LinearParameterTransform.of(attack).inverted(), accentColor);
        pane.add(attackKnob, 0, 1);

        var decay = parameters.ampEgDecayRate;
        var decayKnob = new Knob<>(decay, LinearParameterTransform.of(decay).inverted(), accentColor);
        pane.add(decayKnob, 1, 1);

        var sustain = parameters.ampEgSustainLevel;
        var sustainKnob = new Knob<>(sustain, LinearParameterTransform.of(sustain), accentColor);
        pane.add(sustainKnob, 2, 1);

        var release = parameters.ampEgReleaseRate;
        var releaseKnob = new Knob<>(release, LinearParameterTransform.of(release).inverted(), accentColor);
        pane.add(releaseKnob, 3, 1);

        var amp = parameters.ampAmplitude;
        var ampKnob = new Knob<>(amp, LinearParameterTransform.of(amp), accentColor);
        pane.add(ampKnob, 4, 1);

        return pane;
    }

    private Node createFilterEgEditor() {
        var pane = new GridPane();
        pane.setStyle(BASE_EDITOR_STYLE + "-fx-background-color: linear-gradient(#ffddbc, #ffd5c2);");
        pane.getColumnConstraints().addAll(createColumns(5));
        pane.getRowConstraints().addAll(createRows(2));

        var accentColor = Color.valueOf("#f08b66");

        var label = new Label("Filter EG");
        label.setStyle(BASE_LABEL_STYLE);
        label.setTextFill(accentColor.darker());
        pane.add(label, 0 ,0, 5, 1);

        var attack = parameters.filterEgAttackRate;
        var attackKnob = new Knob<>(attack, LinearParameterTransform.of(attack).inverted(), accentColor);
        pane.add(attackKnob, 0, 1);

        var decay = parameters.filterEgDecayRate;
        var decayKnob = new Knob<>(decay, LinearParameterTransform.of(decay).inverted(), accentColor);
        pane.add(decayKnob, 1, 1);

        var sustain = parameters.filterEgSustainLevel;
        var sustainKnob = new Knob<>(sustain, LinearParameterTransform.of(sustain), accentColor);
        pane.add(sustainKnob, 2, 1);

        var release = parameters.filterEgReleaseRate;
        var releaseKnob = new Knob<>(release, LinearParameterTransform.of(release).inverted(), accentColor);
        pane.add(releaseKnob, 3, 1);

        var amp = parameters.filterEgAmount;
        var ampKnob = new Knob<>(amp, LinearParameterTransform.of(amp), accentColor);
        pane.add(ampKnob, 4, 1);

        return pane;
    }
}
