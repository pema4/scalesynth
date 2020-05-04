package com.pema4.scalesynth.gui.views;

import com.pema4.scalesynth.gui.models.SynthMidiAdapter;
import com.pema4.scalesynth.gui.services.MidiService;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.VBox;

import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiUnavailableException;
import java.util.List;
import java.util.Objects;

public class MidiSettingsView extends Parent {
    private final SynthMidiAdapter midiAdapter;
    private final ObservableList<MidiDevice.Info> inputInfos;
    private final MidiService midiService;

    public MidiSettingsView(SynthMidiAdapter midiAdapter,
                            MidiService midiService) {
        this.midiAdapter = Objects.requireNonNull(midiAdapter);
        this.midiService = Objects.requireNonNull(midiService);

        var updateButton = createUpdateButton();
        var midiInputsComboBox = createMidiInputsComboBox();
        inputInfos = midiInputsComboBox.getItems();

        VBox vbox = new VBox(midiInputsComboBox, updateButton);
        vbox.setAlignment(Pos.CENTER);
        getChildren().add(vbox);
    }

    private Button createUpdateButton() {
        Button button = new Button("Update");
        button.setOnAction(event -> inputInfos.setAll(midiService.getMidiInputInfos()));
        return button;
    }

    private ComboBox<MidiDevice.Info> createMidiInputsComboBox() {
        ComboBox<MidiDevice.Info> comboBox = new ComboBox<>();

        comboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            try {
                midiService.close(oldValue);
                midiService.open(newValue).setReceiver(midiAdapter);
            } catch (MidiUnavailableException | NullPointerException e) {
                new Alert(Alert.AlertType.ERROR, e.getMessage()).showAndWait();
            }
        });

        List<MidiDevice.Info> infos = midiService.getMidiInputInfos();
        comboBox.getItems().setAll(infos);
        comboBox.getSelectionModel().selectFirst();

        return comboBox;
    }
}
