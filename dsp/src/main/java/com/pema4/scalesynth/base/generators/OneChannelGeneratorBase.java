package com.pema4.scalesynth.base.generators;

public abstract class OneChannelGeneratorBase implements Generator {
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

    @Override
    public void generate(double[][] outputs, int n) {
        var output = outputs[0];
        generate(output, n);
        for (int ch = 1; ch < outputs.length; ++ch)
            outputs[ch] = output;
    }

    protected abstract void generate(double[] output, int n);
}
