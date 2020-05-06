package com.pema4.scalesynth.base;

import java.io.Serializable;

/**
 * Component of a synthesizer.
 * <p>
 * There are two kinds of components: generator and processors.
 * Generators generate new audio signal, processors transform signals that already exist.
 */
public interface Component extends Serializable {
    /**
     * Sets a sample rate used in processing.
     *
     * @param sampleRate new sample rate.
     */
    void setSampleRate(double sampleRate);

    /**
     * Called when the user presses or releases any key.
     * Useful for retriggering envelopes and LFOs.
     *
     * @param keyboardEvent represent a type of keyboard event.
     */
    default void handleKeyboardEvent(KeyboardEvent keyboardEvent) {

    }

    /**
     * Returns true if component is active (does something useful).
     * This is used mainly to shutdown inactive voices.
     *
     * @return true if component is active, otherwise false.
     */
    default boolean isActive() {
        return true;
    }
}
