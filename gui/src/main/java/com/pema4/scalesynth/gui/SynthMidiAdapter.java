package com.pema4.scalesynth.gui;

import com.pema4.scalesynth.ScaleSynth;
import com.pema4.scalesynth.base.KeyboardEvent;

import javax.sound.midi.*;

public class SynthMidiAdapter implements Receiver {
    private final ScaleSynth synth;

    public SynthMidiAdapter(ScaleSynth synth) {
        this.synth = synth;
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
        //System.out.println(message.getMessage()[0] + " " + message.getMessage()[1]);
        if (message.getStatus() == ShortMessage.NOTE_ON) {
            int note = message.getMessage()[1];
            double freq = 440 * (float)Math.pow(2, (note - 69) / 12.0);
            int velocity = message.getMessage()[2];
            synth.handleKeyboardEvent(KeyboardEvent.noteOn(note, freq, velocity));
        }
        else if (message.getStatus() == ShortMessage.NOTE_OFF) {
            int note = message.getMessage()[1];
            double freq = 440 * (float)Math.pow(2, (note - 69) / 12.0);
            int velocity = message.getMessage()[2];
            synth.handleKeyboardEvent(KeyboardEvent.noteOff(note, freq, velocity));
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
