package com.pema4.scalesynth.base.generators;

public abstract class OneChannelGeneratorBase implements Generator {
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

    @Override
    public void generate(float[][] outputs, int n) {
        var output = outputs[0];
        generate(output, n);
        for (int ch = 1; ch < outputs.length; ++ch)
            outputs[ch] = output;
    }

    protected abstract void generate(float[] output, int n);
}
