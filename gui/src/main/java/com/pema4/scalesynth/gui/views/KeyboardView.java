package com.pema4.scalesynth.gui.views;

import com.pema4.scalesynth.gui.SynthMidiAdapter;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.ShortMessage;

/**
 * Represents virtual keyboard of the synth.
 * It can be played using mouse.
 */
public class KeyboardView extends Parent {
    private static final int START_NOTE = 36;
    private static final int NOTES_COUNT = 36;
    private static final int KEY_HEIGHT = 100;
    private static final int KEY_WIDTH = 30;
    private final SynthMidiAdapter synth;

    /**
     * Creates a new instance of keyboard view that belongs to given synth.
     * @param synth
     */
    public KeyboardView(SynthMidiAdapter synth) {
        this.synth = synth;
        Node keyboard = createKeyboard();
        getChildren().add(keyboard);
    }

    /**
     * Creates a node containing all keys.
     * @return a node representing a keyboard.
     */
    private Node createKeyboard() {
        HBox keyboard = new HBox();
        for (int i = 0; i < NOTES_COUNT; ++i) {
            var note = createKey(START_NOTE + i);
            keyboard.getChildren().add(note);
        }

        return keyboard;
    }

    /**
     * Returns single key with the given note number.
     * Style of returned node is based on this note position on traditional piano.
     * @param note number of the note.
     * @return a node representing single key.
     */
    private Node createKey(int note) {
        Node key;

        switch (note % 12) {
            case 0:
                key = createFirstWhiteKey();
                break;
            case 1: case 3: case 6: case 8: case 10:
                key = createBlackKey();
                break;
            default:
                key = createWhiteKey();
                break;
        }

        key.setOnMousePressed(event -> {
            var message = createShortMessage(ShortMessage.NOTE_ON, note, 80);
            synth.send(message, -1);
        });

        key.setOnMouseReleased(event -> {
            var message = createShortMessage(ShortMessage.NOTE_OFF, note, 80);
            synth.send(message, -1);
        });

        return key;
    }

    /**
     * Returns an instance of black key.
     * @return a node representing black key.
     */
    private Node createBlackKey() {
        Rectangle key = new Rectangle(KEY_WIDTH * 0.75, KEY_HEIGHT * 0.6);
        key.getStyleClass().add("black-key");
        key.setFill(Color.BLACK);

        // black keys overlap with others
        HBox.setMargin(key, new Insets(0, -key.getWidth() / 2, 0, -key.getWidth() / 2));

        // also blask keys are on top of others
        key.setViewOrder(Double.NEGATIVE_INFINITY);
        return key;
    }

    /**
     * Returns an instance of black key.
     * @return a node representing black key.
     */
    private Node createWhiteKey() {
        Rectangle key = new Rectangle(KEY_WIDTH, KEY_HEIGHT);
        key.getStyleClass().add("white-key");
        key.setFill(Color.LIGHTGRAY);
        return key;
    }

    private Node createFirstWhiteKey() {
        Rectangle key = new Rectangle(KEY_WIDTH, KEY_HEIGHT);
        key.getStyleClass().add("first-white-key");
        key.setFill(Color.LIGHTGRAY.darker());
        return key;
    }

    private ShortMessage createShortMessage(int status, int data1, int data2) {
        try {
            return new ShortMessage(status, data1, data2);
        } catch (InvalidMidiDataException e) {
            // IllegalArgumentException is not checked, so it is used there.
            throw new IllegalArgumentException(e);
        }
    }
}
