package com.pema4.scalesynth.gui.views;

import com.pema4.scalesynth.gui.models.SynthMidiAdapter;
import com.pema4.scalesynth.gui.services.MidiService;
import javafx.beans.value.ObservableValue;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;

import javax.sound.midi.MidiUnavailableException;
import java.util.Objects;

public class MidiSettingsView extends Parent {
    private final SynthMidiAdapter midiAdapter;
    private final MidiService midiService;
    private final ComboBox<String> comboBox;

    public MidiSettingsView(SynthMidiAdapter midiAdapter,
                            MidiService midiService) {
        this.midiAdapter = Objects.requireNonNull(midiAdapter);
        this.midiService = Objects.requireNonNull(midiService);

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
                case "Computer Keyboard":
                    midiService.close(oldValue);
                    break;
                case "Update":
                    updateComboBoxItems();
                    if (comboBox.getItems().contains(oldValue))
                        comboBox.getSelectionModel().select(oldValue);
                    else
                        comboBox.getSelectionModel().selectFirst();
                    break;
                default:
                    midiService.close(oldValue);
                    midiService.open(newValue).setReceiver(midiAdapter);
                    break;
            }
        } catch (MidiUnavailableException | NullPointerException e) {
            new Alert(Alert.AlertType.ERROR, e.getMessage()).showAndWait();
        }
    }

    private void updateComboBoxItems() {
        var items = comboBox.getItems();
        items.setAll("Computer Keyboard");
        items.addAll(midiService.getMidiInputs());
        items.add("Update");
    }
}
