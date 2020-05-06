package com.pema4.scalesynth.gui.models;

import javafx.event.EventHandler;
import javafx.scene.input.KeyEvent;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.ShortMessage;
import java.util.HashSet;
import java.util.Set;

/**
 * Keyboard event handler that implements virtual midi keyboard.
 * <p>
 * Keys A, S, D, F, G, H, J, K, L are white keys.
 * <p>
 * Keys W, E, T, Y, U, O, P are black keys.
 * <p>
 * Key Z moves current octave down, X - up.
 * <p>
 * Key C decreases velocity, V - increases.
 */
public class KeyEventFilter implements EventHandler<KeyEvent> {
    private static final String KEYS_ORDER = "AWSEDFTGYHUJKOLP;']";
    private final Set<Integer> pressedNotes = new HashSet<>();
    private final SynthMidiAdapter midiAdapter;
    private int currentVelocity = 80;
    private int currentOctave = 4;

    /**
     * Constructs a new instance of KeyEventFilter to work with that SynthMidiAdapter.
     *
     * @param midiAdapter a SynthMidiAdapter instance to use.
     */
    public KeyEventFilter(SynthMidiAdapter midiAdapter) {
        this.midiAdapter = midiAdapter;
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
                    releasePlayingNotes();
                    currentOctave = Math.max(0, currentOctave - 1);
                    break;
                case 'X':
                    releasePlayingNotes();
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

    /**
     * Releases all playing notes.
     */
    private void releasePlayingNotes() {
        for (var note : pressedNotes)
            simulateKeyReleased(note);
    }

    /**
     * Simulates a NOTE ON event with note {@code note} and current velocity.
     *
     * @param note a note to be simulated.
     */
    private void simulateKeyPressed(int note) {
        note = Math.min(127, Math.max(0, note));
        pressedNotes.add(note);
        try {
            ShortMessage message = new ShortMessage(ShortMessage.NOTE_ON, note + 12 * currentOctave, currentVelocity);
            midiAdapter.send(message, -1);
        } catch (InvalidMidiDataException e) {
            e.printStackTrace();
        }
    }

    /**
     * Simulates a NOTE OFF event with note {@code note} and current velocity.
     *
     * @param note a note to be simulated.
     */
    private void simulateKeyReleased(int note) {
        note = Math.min(127, Math.max(0, note));
        pressedNotes.remove(note);
        try {
            ShortMessage message = new ShortMessage(ShortMessage.NOTE_OFF, note + 12 * currentOctave, currentVelocity);
            midiAdapter.send(message, -1);
        } catch (InvalidMidiDataException e) {
            e.printStackTrace();
        }
    }
}
