package com.pema4.scalesynth.gui.views;

import com.pema4.scalesynth.gui.models.SynthAsioAdapter;
import com.synthbot.jasiohost.AsioDriver;
import javafx.beans.value.ObservableValue;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;

import java.util.Objects;

public class AsioSettingsView extends Parent {
    private final SynthAsioAdapter asioAdapter;
    private final ComboBox<String> comboBox;

    public AsioSettingsView(SynthAsioAdapter asioAdapter) {
        this.asioAdapter = Objects.requireNonNull(asioAdapter);

        comboBox = new ComboBox<>();
        comboBox.getSelectionModel().selectedItemProperty().addListener(this::handleSelectionChanged);
        updateComboBoxItems();
        comboBox.getSelectionModel().selectFirst();
        comboBox.setMaxWidth(200);
        comboBox.setMinWidth(200);

        getChildren().add(comboBox);
    }

    private void handleSelectionChanged(ObservableValue<? extends String> observable, String oldValue, String newValue) {
        try {
            switch (newValue) {
                case "No audio":
                    asioAdapter.stop();
                    break;
                case "Update":
                    updateComboBoxItems();
                    if (comboBox.getItems().contains(oldValue))
                        comboBox.getSelectionModel().select(oldValue);
                    else
                        comboBox.getSelectionModel().clearSelection();
                    break;
                default:
                    asioAdapter.stop();
                    asioAdapter.start(newValue);
                    break;
            }
        } catch (NullPointerException e) {
            new Alert(Alert.AlertType.ERROR, e.getMessage()).showAndWait();
        }
    }

    private void updateComboBoxItems() {
        var items = comboBox.getItems();
        items.setAll(AsioDriver.getDriverNames());
        items.add("Update");
    }
}
