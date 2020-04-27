package com.pema4.scalesynth.dsp.processors;

import com.pema4.scalesynth.base.KeyboardEvent;
import com.pema4.scalesynth.base.processors.ProcessorBase;

class DelayLine {
    private static final float MAX_TIME = 1f;
    private float[] buffer;
    private int bufferLength;
    private int writePoint;
    private int readPoint;
    private float readOffset;
    private float feedback;
    private float delay;

    public void setDelay(float delay)
    {
        if (this.delay == delay)
            return;

        this.delay = delay;
        double delayedPoint = (double)writePoint - delay;
        if (delayedPoint < 0)
            delayedPoint += bufferLength;

        readPoint = (int)delayedPoint;
        if (readPoint == bufferLength)
            readPoint -= 1;
        readOffset = (float)(delayedPoint - readPoint);
    }

    public void setFeedback(float value)
    {
        feedback = value;
    }

    public float CalculateOutput()
    {
        float res = buffer[readPoint] * (1 - readOffset);
        var nextPoint = readPoint + 1;
        if (nextPoint < bufferLength)
            res += buffer[nextPoint] * readOffset;
        else
            res += buffer[0] * readOffset;
        return res;
    }

    public float processOne(float input)
    {
        buffer[writePoint] = input;

        float delayedSample = CalculateOutput();
        readPoint += 1;
        if (readPoint == bufferLength)
            readPoint = 0;

        buffer[writePoint] += delayedSample * feedback;
        writePoint += 1;
        if (writePoint == bufferLength)
            writePoint = 0;

        return delayedSample;
    }

    public void setSampleRate(float sampleRate) {
        bufferLength = (int)(sampleRate * MAX_TIME);
        buffer = new float[bufferLength];
    }
}

public class Delay extends ProcessorBase {
    private final DelayLine[] delayLines = new DelayLine[2];

    public Delay() {
        for (int i = 0; i < delayLines.length; ++i) {
            var delayLine = new DelayLine();
            delayLine.setDelay(sampleRate / 2);
            delayLine.setFeedback(0.7f);
            delayLines[i] = delayLine;
        }
    }

    @Override
    public void process(float[][] inputs, float[][] outputs, int n) {
        for (int ch = 0; ch < inputs.length; ++ch)
            for (int i = 0; i < n; ++i)
                outputs[ch][i] = 0.3f * delayLines[ch].processOne(inputs[ch][i]) + 0.7f * inputs[ch][i];
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
     * Sets a sample rate used in processing.
     *
     * @param sampleRate new sample rate.
     */
    @Override
    public void setSampleRate(float sampleRate) {
        for (DelayLine delayLine : delayLines) {
            delayLine.setSampleRate(sampleRate);
            delayLine.setDelay(sampleRate / 2);
        }
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


