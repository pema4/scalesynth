package com.pema4.scalesynth.gui;

import com.pema4.scalesynth.gui.services.ScaleService;
import javafx.event.EventHandler;
import javafx.scene.input.KeyEvent;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.ShortMessage;
import java.util.HashSet;
import java.util.Set;

public class KeyEventFilter implements EventHandler<KeyEvent> {
    private static final String KEYS_ORDER = "AWSEDFTGYHUJKOLP;']";
    private final Set<Integer> pressedNotes = new HashSet<>();
    private final SynthMidiAdapter midiAdapter;
    private final ScaleService scaleService;
    private int currentVelocity = 80;
    private int currentOctave = 4;

    public KeyEventFilter(SynthMidiAdapter midiAdapter, ScaleService scaleService) {
        this.midiAdapter = midiAdapter;
        this.scaleService = scaleService;
    }

    /**
     * Invoked when a specific event of the type for which this handler is
     * registered happens.
     *
     * @param event the event which occurred
     */
    @Override
    public void handle(KeyEvent event) {
        int symbol = event.getCode().getCode();
        int note = KEYS_ORDER.indexOf(symbol);

        if (event.getEventType() == KeyEvent.KEY_PRESSED)
            switch (symbol) {
                case 'Z':
                    currentOctave = Math.max(0, currentOctave - 1);
                    break;
                case 'X':
                    currentOctave = Math.min(10, currentOctave + 1);
                    break;
                case 'C':
                    currentVelocity = Math.max(1, currentVelocity - 10);
                    break;
                case 'V':
                    currentVelocity = Math.min(127, currentVelocity + 10);
                    break;
                default:
                    if (note == -1)
                        return;
                    if (!pressedNotes.contains(note))
                        simulateKeyPressed(note);
                    break;
            }
        else if (event.getEventType() == KeyEvent.KEY_RELEASED && pressedNotes.contains(note)) {
            if (note == -1)
                return;
            simulateKeyReleased(note);
        }

        event.consume();
    }


    private void simulateKeyPressed(int note) {
        note = Math.min(127, Math.max(0, note));
        pressedNotes.add(note);
        ShortMessage message = null;
        try {
            message = new ShortMessage(ShortMessage.NOTE_ON, note + 12 * currentOctave, currentVelocity);
            midiAdapter.send(message, -1);
        } catch (InvalidMidiDataException e) {
            e.printStackTrace();
        }
    }

    private void simulateKeyReleased(int note) {
        note = Math.min(127, Math.max(0, note));
        pressedNotes.remove(note);
        ShortMessage message = null;
        try {
            message = new ShortMessage(ShortMessage.NOTE_OFF, note + 12 * currentOctave, currentVelocity);
            midiAdapter.send(message, -1);
        } catch (InvalidMidiDataException e) {
            e.printStackTrace();
        }
    }
}
