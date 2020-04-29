package com.pema4.scalesynth.base;

import java.io.Serializable;
import java.util.List;

/**
 * Component of a synthesizer.
 *
 * There are two kinds of components: generator and processors.
 * Generators generate new audio signal, processors transform signals that already exist.
 *
 * Components can be serialized - this is useful for saving current state.
 */
public interface Component extends Serializable {
    /**
     * Sets a sample rate used in processing.
     * @param sampleRate new sample rate.
     */
    void setSampleRate(float sampleRate);

    /**
     * Returns a sample rate used in processing.
     * @return a sample rate used in processing.
     */
    float getSampleRate();

    /**
     * Reports a latency of this component (in samples)
     * @return a latency of this component (in samples)
     */
    float getLatency();

    /**
     * Called when the user presses or releases any key.
     * Useful for retriggering envelopes and LFOs.
     * @param keyboardEvent represent a type of keyboard event.
     */
    void onKeyboardEvent(KeyboardEvent keyboardEvent);
}
