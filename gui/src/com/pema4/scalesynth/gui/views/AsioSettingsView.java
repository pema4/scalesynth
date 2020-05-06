package com.pema4.scalesynth.gui.views;

import com.pema4.scalesynth.gui.models.SynthAsioAdapter;
import com.synthbot.jasiohost.AsioDriver;
import com.synthbot.jasiohost.AsioException;
import javafx.application.Platform;
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
        comboBox.setMaxWidth(150);
        comboBox.setMinWidth(150);

        getChildren().add(comboBox);
    }

    private void handleSelectionChanged(ObservableValue<? extends String> observable, String oldValue, String newValue) {
        try {
            if (newValue == null)
                return;

            switch (newValue) {
                case "Update":
                    updateComboBoxItems();
                    if (comboBox.getItems().contains(oldValue))
                        comboBox.getSelectionModel().select(oldValue);
                    else
                        comboBox.getSelectionModel().clearSelection();
                    break;
                case "Control Panel...":
                    var currentDriver = AsioDriver.getCurrentDriver();
                    if (currentDriver != null)
                        currentDriver.openControlPanel();
                    comboBox.getSelectionModel().select(oldValue);
                    break;
                default:
                    asioAdapter.stop();
                    asioAdapter.start(newValue);
                    break;
            }
        } catch (AsioException | NullPointerException e) {
            new Alert(Alert.AlertType.ERROR, e.getMessage()).showAndWait();
            Platform.runLater(() -> comboBox.getSelectionModel().clearSelection());
        }
    }

    private void updateComboBoxItems() {
        var items = comboBox.getItems();
        items.setAll(AsioDriver.getDriverNames());
        items.addAll("Update", "Control Panel...");
    }
}
