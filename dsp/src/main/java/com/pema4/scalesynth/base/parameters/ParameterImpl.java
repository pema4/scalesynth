package com.pema4.scalesynth.base.parameters;

import com.pema4.scalesynth.base.KeyboardEvent;

public class ParameterImpl<T> implements Parameter<T> {
    ParameterImpl(String id) {
    }

    /**
     * Name of the parameter. Must be unique.
     *
     * @return identifier of this parameter.
     */
    @Override
    public String getId() {
        return null;
    }

    /**
     * Sets the value of this parameter.
     *
     * @param newValue new value of this parameter.
     */
    @Override
    public void setValue(T newValue) {

    }

    /**
     * Returns the value of this parameter.
     *
     * @return the value of this parameter.
     */
    @Override
    public T getValue() {
        return null;
    }

    @Override
    public void generate(float[][] outputs, int n) {

    }

    /**
     * Sets a sample rate used in processing.
     *
     * @param sampleRate new sample rate.
     */
    @Override
    public void setSampleRate(float sampleRate) {

    }

    /**
     * Returns a sample rate used in processing.
     *
     * @return a sample rate used in processing.
     */
    @Override
    public float getSampleRate() {
        return 0;
    }

    /**
     * Reports a latency of this component (in samples)
     *
     * @return a latency of this component (in samples)
     */
    @Override
    public float getLatency() {
        return 0;
    }

    /**
     * Called when the user presses or releases any key.
     * Useful for retriggering envelopes and LFOs.
     *
     * @param keyboardEvent represent a type of keyboard event.
     */
    @Override
    public void onKeyboardEvent(KeyboardEvent keyboardEvent) {

    }
}
