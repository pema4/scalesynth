package com.pema4.scalesynth.base.processors;

import com.pema4.scalesynth.base.KeyboardEvent;

public abstract class ProcessorBase implements Processor {
    protected float sampleRate;

    /**
     * Sets a sample rate used in processing.
     *
     * @param sampleRate new sample rate.
     */
    @Override
    public void setSampleRate(float sampleRate) {
        this.sampleRate = sampleRate;
    }

    /**
     * Returns a sample rate used in processing.
     *
     * @return a sample rate used in processing.
     */
    @Override
    public float getSampleRate() {
        return sampleRate;
    }
}
