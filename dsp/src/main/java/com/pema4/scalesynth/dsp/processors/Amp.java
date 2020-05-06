package com.pema4.scalesynth.dsp.processors;

import com.pema4.scalesynth.base.KeyboardEvent;
import com.pema4.scalesynth.base.processors.Processor;

public class Amp implements Processor {
    private final Envelope ampEnvelope;
    private double[][] envelopeOutput = new double[1][8096];
    private double amplitude;
    private double sampleRate;

    public Amp(Envelope ampEnvelope) {
        this.ampEnvelope = ampEnvelope;
    }

    public synchronized void setAmplitude(double amplitude) {
        this.amplitude = amplitude;
    }

    @Override
    public synchronized void process(double[][] inputs, int n) {
        ampEnvelope.generate(envelopeOutput, n);
        var modulation = envelopeOutput[0];

        for (int ch = 0; ch < inputs.length; ++ch)
            for (int i = 0; i < inputs[ch].length; ++i)
                inputs[ch][i] = inputs[ch][i] * amplitude * modulation[i];
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
