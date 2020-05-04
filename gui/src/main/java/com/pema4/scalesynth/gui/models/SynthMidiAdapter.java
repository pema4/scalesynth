package com.pema4.scalesynth.gui.models;

import com.pema4.scalesynth.ScaleSynth;
import com.pema4.scalesynth.base.KeyboardEvent;
import com.pema4.scalesynth.gui.services.ScaleService;

import javax.sound.midi.*;

public class SynthMidiAdapter implements Receiver {
    private final ScaleSynth synth;
    private final ScaleService scaleService;

    public SynthMidiAdapter(ScaleSynth synth, ScaleService scaleService) {
        this.synth = synth;
        this.scaleService = scaleService;
    }

    /**
     * Sends a MIDI message and time-stamp to this receiver. If time-stamping is
     * not supported by this receiver, the time-stamp value should be -1.
     *
     * @param message   the MIDI message to send
     * @param timeStamp the time-stamp for the message, in microseconds
     * @throws IllegalStateException if the receiver is closed
     */
    @Override
    public void send(MidiMessage message, long timeStamp) {
        if (message.getStatus() == ShortMessage.NOTE_ON) {
            int note = message.getMessage()[1];
            double freq = scaleService.getFreq(note);
            int velocity = message.getMessage()[2];
            synth.handleKeyboardEvent(KeyboardEvent.noteOn(note, freq, velocity));
        }
        else if (message.getStatus() == ShortMessage.NOTE_OFF) {
            int note = message.getMessage()[1];
            synth.handleKeyboardEvent(KeyboardEvent.noteOff(note));
        } else if (message.getStatus() == ShortMessage.PITCH_BEND) {
            var lsb = message.getMessage()[1];
            var msb = message.getMessage()[2];
            synth.handleKeyboardEvent(KeyboardEvent.pitchBend(lsb, msb));
        }
    }

    /**
     * Indicates that the application has finished using the receiver, and that
     * limited resources it requires may be released or made available.
     * <p>
     * If the creation of this {@code Receiver} resulted in implicitly opening
     * the underlying device, the device is implicitly closed by this method.
     * This is true unless the device is kept open by other {@code Receiver} or
     * {@code Transmitter} instances that opened the device implicitly, and
     * unless the device has been opened explicitly. If the device this
     * {@code Receiver} is retrieved from is closed explicitly by calling
     * {@link MidiDevice#close MidiDevice.close}, the {@code Receiver} is
     * closed, too. For a detailed description of open/close behaviour see the
     * class description of {@link MidiDevice MidiDevice}.
     *
     * @see MidiSystem#getReceiver
     */
    @Override
    public void close() {

    }
}
