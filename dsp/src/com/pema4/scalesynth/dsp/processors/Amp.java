package com.pema4.scalesynth.dsp.processors;

import com.pema4.scalesynth.base.KeyboardEvent;
import com.pema4.scalesynth.base.KeyboardEventType;
import com.pema4.scalesynth.base.processors.Processor;
import com.pema4.scalesynth.dsp.generators.Envelope;

/**
 * A simple amplifier processor. It controls a volume of the output.
 */
public class Amp implements Processor {
    private final Envelope ampEnvelope;
    private final double[][] envelopeOutput = new double[1][8096]; // to not bother with buffer resizing.
    private double amplitude;
    private double velocity = 1;

    /**
     * Constructs a new amplifier instance.
     *
     * @param ampEnvelope amplitude envelope to use.
     */
    public Amp(Envelope ampEnvelope) {
        this.ampEnvelope = ampEnvelope;
    }

    /**
     * Sets a new amplitude (volume of the output).
     * <p>
     * This value is supposed to be in range [0, +1].
     *
     * @param amplitude amplitude coefficient.
     */
    public synchronized void setAmplitude(double amplitude) {
        this.amplitude = amplitude;
    }

    /**
     * Transforms incoming audio.
     * Note that content of {@code inputs} will be overwritten.
     *
     * @param inputs buffers to place generated audio into.
     * @param n      how many samples to generate.
     */
    @Override
    public synchronized void process(double[][] inputs, int n) {
        ampEnvelope.generate(envelopeOutput, n);
        var modulation = envelopeOutput[0];

        for (int ch = 0; ch < inputs.length; ++ch)
            for (int i = 0; i < inputs[ch].length; ++i)
                inputs[ch][i] = inputs[ch][i] * velocity * amplitude * modulation[i];
    }

    /**
     * Sets a sample rate used in processing.
     *
     * @param sampleRate new sample rate.
     */
    @Override
    public void setSampleRate(double sampleRate) {
        ampEnvelope.setSampleRate(sampleRate);
    }

    /**
     * Called when the user presses or releases any key.
     * Useful for retriggering envelopes and LFOs.
     *
     * @param keyboardEvent represent a type of keyboard event.
     */
    @Override
    public void handleKeyboardEvent(KeyboardEvent keyboardEvent) {
        if (keyboardEvent.getType() == KeyboardEventType.NOTE_ON)
            velocity = 0.2 + 0.8 * keyboardEvent.getValue() / 127;
        ampEnvelope.handleKeyboardEvent(keyboardEvent);
    }

    /**
     * Returns true if component is active (does something useful).
     * This is used mainly to shutdown inactive voices.
     *
     * @return true if component is active, otherwise false.
     */
    @Override
    public boolean isActive() {
        return ampEnvelope.isActive();
    }
}
