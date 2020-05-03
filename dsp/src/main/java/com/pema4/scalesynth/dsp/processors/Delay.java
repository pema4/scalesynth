package com.pema4.scalesynth.dsp.processors;

import com.pema4.scalesynth.base.KeyboardEvent;
import com.pema4.scalesynth.base.processors.ProcessorBase;

class DelayLine {
    private static final float MAX_TIME = 1f;
    private double[] buffer;
    private int bufferLength;
    private int writePoint;
    private int readPoint;
    private double readOffset;
    private double feedback;
    private double delay;

    public void setDelay(double delay)
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

    public void setFeedback(double value)
    {
        feedback = value;
    }

    public double CalculateOutput()
    {
        double res = buffer[readPoint] * (1 - readOffset);
        var nextPoint = readPoint + 1;
        if (nextPoint < bufferLength)
            res += buffer[nextPoint] * readOffset;
        else
            res += buffer[0] * readOffset;
        return res;
    }

    public double processOne(double input)
    {
        buffer[writePoint] = input;

        double delayedSample = CalculateOutput();
        readPoint += 1;
        if (readPoint == bufferLength)
            readPoint = 0;

        buffer[writePoint] += delayedSample * feedback;
        writePoint += 1;
        if (writePoint == bufferLength)
            writePoint = 0;

        return delayedSample;
    }

    public void setSampleRate(double sampleRate) {
        bufferLength = (int)(sampleRate * MAX_TIME);
        buffer = new double[bufferLength];
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
    public void process(double[][] inputs, int n) {
        for (int ch = 0; ch < inputs.length; ++ch)
            for (int i = 0; i < n; ++i)
                inputs[ch][i] = 0.3f * delayLines[ch].processOne(inputs[ch][i]) + 0.7f * inputs[ch][i];
    }

    /**
     * Sets a sample rate used in processing.
     *
     * @param sampleRate new sample rate.
     */
    @Override
    public void setSampleRate(double sampleRate) {
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
    public void handleKeyboardEvent(KeyboardEvent keyboardEvent) {

    }
}


