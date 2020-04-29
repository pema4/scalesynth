package com.pema4.scalesynth.dsp.processors;

import com.pema4.scalesynth.base.KeyboardEvent;
import com.pema4.scalesynth.base.KeyboardEventType;
import com.pema4.scalesynth.base.processors.ProcessorBase;

public class DecayAmpEnvelope extends ProcessorBase {
    private float amplitude;
    private float coef;
    private float velocity;
    private float volume;

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
            velocity = 0.3f + 0.7f * keyboardEvent.getVelocity() / 127.0f;
            amplitude = 1f;
            coef = 0.9999f;
        } else {
            coef = 0.9992f;
        }
    }

    @Override
    public void process(float[][] inputs, float[][] outputs, int n) {
        float initialAmplitude = amplitude;
        for (int ch = 0; ch < inputs.length; ++ch) {
            amplitude = initialAmplitude;
            for (int i = 0; i < inputs[0].length; ++i) {
                outputs[ch][i] = inputs[ch][i] * velocity * amplitude * volume;
                amplitude *= coef;
            }
        }
    }

    public void setVolume(float volume) {
        this.volume = volume;
    }
}
