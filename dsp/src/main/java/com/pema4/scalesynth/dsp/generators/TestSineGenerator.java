package com.pema4.scalesynth.dsp.generators;

import com.pema4.scalesynth.base.KeyboardEvent;
import com.pema4.scalesynth.base.KeyboardEventType;
import com.pema4.scalesynth.base.generators.OneChannelGeneratorBase;

public class TestSineGenerator extends OneChannelGeneratorBase {
    private float freq;
    private float phase = 0;

    @Override
    protected void generate(float[] output, int n) {
        for (int i = 0; i < n; ++i) {
            output[i] = 0.1f *(float)(Math.atan((Math.sin(phase) + 10 * Math.pow(Math.sin(phase), 5))) / Math.PI * 2);
            phase += 2 * Math.PI * freq / sampleRate;
        }
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
        if (keyboardEvent.getType() == KeyboardEventType.NOTE_ON) {
            phase = 0;
            freq = 440 * (float)Math.pow(2, (keyboardEvent.getNote() - 69) / 12.0);
        }
    }
}
