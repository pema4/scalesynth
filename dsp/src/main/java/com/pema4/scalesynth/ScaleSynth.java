package com.pema4.scalesynth;

import com.pema4.scalesynth.base.KeyboardEvent;
import com.pema4.scalesynth.base.generators.Generator;
import com.pema4.scalesynth.base.generators.PolyGenerator;
import com.pema4.scalesynth.dsp.generators.TestSineGenerator;
import com.pema4.scalesynth.dsp.processors.DecayAmpEnvelope;
import com.pema4.scalesynth.dsp.processors.Delay;

/**
 * Main synthesizer class.
 * It is used to fill buffers with n samples.
 */
public class ScaleSynth implements Generator {
    private final ScaleSynthParameters parameters = new ScaleSynthParameters();
    private final Generator generator = new PolyGenerator(8, this::createVoice).then(new Delay());

    private Generator createVoice() {
        var sineGenerator = new TestSineGenerator();
        sineGenerator.setOctave(parameters.getOctave().getDefault());
        parameters.getOctave().addListener(sineGenerator::setOctave);

        var decayAmpEnvelope = new DecayAmpEnvelope();
        decayAmpEnvelope.setVolume(parameters.getVolume().getDefault());
        parameters.getVolume().addListener(decayAmpEnvelope::setVolume);

        return sineGenerator.then(decayAmpEnvelope);
    }

    public ScaleSynthParameters getParameters() {
        return parameters;
    }

    @Override
    public void generate(float[][] outputs, int n) {
        generator.generate(outputs, n);
    }

    @Override
    public float getSampleRate() {
        return generator.getSampleRate();
    }

    @Override
    public void setSampleRate(float sampleRate) {
        generator.setSampleRate(sampleRate);
    }

    /**
     * Reports a latency of this component (in samples)
     *
     * @return a latency of this component (in samples)
     */
    @Override
    public float getLatency() {
        return generator.getLatency();
    }

    /**
     * Called when the user presses or releases any key.
     * Useful for retriggering envelopes and LFOs.
     *
     * @param keyboardEvent represent a type of keyboard event.
     */
    @Override
    public void onKeyboardEvent(KeyboardEvent keyboardEvent) {
        generator.onKeyboardEvent(keyboardEvent);
    }
}
