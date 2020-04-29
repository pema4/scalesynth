package com.pema4.scalesynth;

import com.pema4.scalesynth.base.parameters.NumericParameter;

/**
 * Exposed parameters of the synth.
 */
public class ScaleSynthParameters {
    private final NumericParameter<Float> octave =
            new NumericParameter<>("octave", 0f, -2f, 2f, "oct.");

    private final NumericParameter<Float> volume =
            new NumericParameter<>("volume", 1.0f, 0f, 1f, "volume");

    public NumericParameter<Float> getOctave() {
        return octave;
    }

    public NumericParameter<Float> getVolume() {
        return volume;
    }
}
