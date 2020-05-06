package com.pema4.scalesynth;

import com.pema4.scalesynth.base.parameters.NumericParameter;
import com.pema4.scalesynth.base.parameters.Parameter;

/**
 * Exposed parameters of the synth.
 * Their names are self explanatory.
 */
public class ScaleSynthParameters {

    // oscillators and mixer

    public final NumericParameter<Integer> slaveOctave =
            new NumericParameter<>("octave", 1, -3, 3, "octave");

    public final NumericParameter<Integer> slaveSemi =
            new NumericParameter<>("semi", 0, -12, 12, "semitone");

    public final NumericParameter<Integer> slaveFine =
            new NumericParameter<>("fine", 0, -50, 50, "cents");

    public final NumericParameter<Double> drift =
            new NumericParameter<>("drift", 0.05, 0.0, 1.0, "%");

    public final Parameter<Boolean> syncEnabled =
            new Parameter<>("sync", true);

    public final NumericParameter<Double> masterMix =
            new NumericParameter<>("mix", 0.0, 0.0, 1.0, "%");

    public final NumericParameter<Double> slaveMix =
            new NumericParameter<>("mix", 1.0, 0.0, 1.0, "%");

    public final NumericParameter<Double> masterPW =
            new NumericParameter<>("pw", 0.5, 0.01, 0.99, "%");

    public final NumericParameter<Double> slavePW =
            new NumericParameter<>("pw", 0.5, 0.01, 0.99, "%");

    public final NumericParameter<Double> masterAmplitude =
            new NumericParameter<>("A", 1.0, 1e-3, 1.0, "");

    public final NumericParameter<Double> slaveAmplitude =
            new NumericParameter<>("B", 1e-3, 1e-3, 1.0, "");

    public final NumericParameter<Double> noiseAmplitude =
            new NumericParameter<>("noise", 1e-3, 1e-3, 1.0, "");

    public final NumericParameter<Double> unisonDetune =
            new NumericParameter<>("detune", 0.1, 0.0, 1.0, "%");

    public final NumericParameter<Double> unisonStereo =
            new NumericParameter<>("stereo", 0.1, 0.0, 1.0, "%");

    public final NumericParameter<Integer> unisonVoices =
            new NumericParameter<>("voices", 3, 1, 8, "%");

    // amp eg

    public final NumericParameter<Double> ampEgAttackRate =
            new NumericParameter<>("attack", 5.0, 1.0, 100.0, "");

    public final NumericParameter<Double> ampEgDecayRate =
            new NumericParameter<>("decay", 5.0, 1.0, 100.0, "");

    public final NumericParameter<Double> ampEgSustainLevel =
            new NumericParameter<>("sustain", 0.6, 0.0, 1.0, "%");

    public final NumericParameter<Double> ampEgReleaseRate =
            new NumericParameter<>("release", 5.0, 1.0, 100.0, "");

    public final NumericParameter<Double> ampAmplitude =
            new NumericParameter<>("volume", 1.0, 0.0, 1.0, "%");


    // filter eg

    public final NumericParameter<Double> filterEgAttackRate =
            new NumericParameter<>("attack", 5.0, 1.0, 100.0, "");

    public final NumericParameter<Double> filterEgDecayRate =
            new NumericParameter<>("decay", 5.0, 1.0, 100.0, "");

    public final NumericParameter<Double> filterEgSustainLevel =
            new NumericParameter<>("sustain", 0.6, 0.0, 1.0, "%");

    public final NumericParameter<Double> filterEgReleaseRate =
            new NumericParameter<>("release", 5.0, 1.0, 100.0, "");

    public final NumericParameter<Double> filterEgAmount =
            new NumericParameter<>("amount", 0.0, -10.0, 10.0, "octaves");


    // filter

    public final NumericParameter<Double> filterCutoff =
            new NumericParameter<>("cutoff", 22050.0, 20.0, 22050.0, "hz");

    public final NumericParameter<Double> filterQ =
            new NumericParameter<>("Q", 0.71, 0.71, 6.0, "%");

    public final NumericParameter<Double> filterMode =
            new NumericParameter<>("mode", 0.0, 0.0, 360.0, "degree");

    public final NumericParameter<Double> filterKeyboardTracking =
            new NumericParameter<>("tracking", 0.0, 0.0, 1.0, "%");
}
