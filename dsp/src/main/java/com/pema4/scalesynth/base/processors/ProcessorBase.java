package com.pema4.scalesynth.base.processors;

import com.pema4.scalesynth.base.KeyboardEvent;

public abstract class ProcessorBase implements Processor {
    protected double sampleRate;

    /**
     * Sets a sample rate used in processing.
     *
     * @param sampleRate new sample rate.
     */
    @Override
    public void setSampleRate(double sampleRate) {
        this.sampleRate = sampleRate;
    }
}
