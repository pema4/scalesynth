package com.pema4.scalesynth.gui.views;

import com.pema4.scalesynth.gui.SynthAsioAdapter;
import com.synthbot.jasiohost.AsioDriver;
import com.synthbot.jasiohost.AsioException;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.VBox;

import java.util.Objects;

public class AsioSettingsView extends Parent {
    private final SynthAsioAdapter asioAdapter;
    private final ObservableList<String> outputNames;

    public AsioSettingsView(SynthAsioAdapter asioAdapter) {
        this.asioAdapter = Objects.requireNonNull(asioAdapter);
        var updateButton = createUpdateButton();
        var asioOutputNamesComboBox = createAsioOutputsComboBox();
        outputNames = asioOutputNamesComboBox.getItems();

        VBox vbox = new VBox(10, asioOutputNamesComboBox, updateButton);
        vbox.setAlignment(Pos.CENTER);
        getChildren().add(vbox);
    }

    private ComboBox<String> createAsioOutputsComboBox() {
        var comboBox = new ComboBox<String>();

        comboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            try {
                asioAdapter.stop();
                asioAdapter.start(newValue);
            } catch (AsioException e) {
                new Alert(Alert.AlertType.ERROR, e.getMessage()).showAndWait();
            }
        });

        var items = AsioDriver.getDriverNames();
        comboBox.getItems().setAll(items);
        comboBox.getSelectionModel().selectFirst();
        return comboBox;
    }

    private Button createUpdateButton() {
        var button = new Button("Update");
        button.setOnAction(event -> {
            outputNames.setAll(AsioDriver.getDriverNames());
        });
        return button;
    }
}
