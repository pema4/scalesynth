package com.pema4.scalesynth.gui.views;

import com.pema4.scalesynth.ScaleSynthParameters;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;

import java.util.stream.Stream;

public class EditorView extends Parent {
    private final ScaleSynthParameters parameters;

    public EditorView(ScaleSynthParameters parameters) {
        this.parameters = parameters;
        var ui = createUI();
        getChildren().add(ui);
    }

    private Node createUI() {
        var master = new MasterOscillatorEditorView(parameters);
        var slave = new SlaveOscillatorEditorView(parameters);
        var unison = new UnisonEditorView(parameters);
        var mixer = new MixerEditorView(parameters);
        var filter = new FilterEditorView(parameters);
        var filterEg = new FilterEgEditorView(parameters);
        var ampEg = new AmpEgEditorView(parameters);

        var pane = new GridPane();
        pane.setStyle("-fx-vgap: 5px; -fx-hgap: 5px; -fx-padding: 5px;");

        pane.getRowConstraints().addAll(Stream.generate(RowConstraints::new).peek(x -> {
            x.setValignment(VPos.TOP);
        }).limit(2).toArray(RowConstraints[]::new));

        pane.getColumnConstraints().addAll(Stream.generate(ColumnConstraints::new).peek(x -> {
            x.setHalignment(HPos.LEFT);
        }).limit(5).toArray(ColumnConstraints[]::new));

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
