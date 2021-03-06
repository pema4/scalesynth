package com.scalesynth.gui.views;

import com.scalesynth.gui.models.SynthAsioAdapter;
import com.synthbot.jasiohost.AsioDriver;
import com.synthbot.jasiohost.AsioException;
import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

import java.util.Objects;

/**
 * ASIO settings view.
 */
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

        var hbox = new HBox(5, new Label("Audio outputs:"), comboBox);
        hbox.setAlignment(Pos.CENTER);
        getChildren().add(hbox);
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
