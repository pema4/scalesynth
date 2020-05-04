package com.pema4.scalesynth;

import com.pema4.scalesynth.base.parameters.NumericParameter;
import com.pema4.scalesynth.base.parameters.Parameter;

/**
 * Exposed parameters of the synth.
 */
public class ScaleSynthParameters {

    // oscillators and mixer

    public final NumericParameter<Double> slaveOctave =
            new NumericParameter<>("slaveOctave", 1.0, -3.0, 3.0, "octave");

    public final NumericParameter<Double> slaveSemi =
            new NumericParameter<>("slaveSemi", 0.0, -12.0, 12.0, "semitone");

    public final NumericParameter<Double> slaveFine =
            new NumericParameter<>("slaveFine", 0.0, -50.0, 50.0, "cents");

    public final NumericParameter<Double> drift =
            new NumericParameter<>("drift", 0.05, 0.0, 1.0, "%");

    public final Parameter<Boolean> ifSynced =
            new Parameter<>("ifSynced", true);

    public final NumericParameter<Double> masterMix =
            new NumericParameter<>("masterMix", 0.0, 0.0, 1.0, "%");

    public final NumericParameter<Double> slaveMix =
            new NumericParameter<>("slaveMix", 1.0, 0.0, 1.0, "%");

    public final NumericParameter<Double> masterPW =
            new NumericParameter<>("masterPW", 0.5, 0.01, 0.99, "%");

    public final NumericParameter<Double> slavePW =
            new NumericParameter<>("slavePW", 0.5, 0.01, 0.99, "%");

    public final NumericParameter<Double> masterAmplitude =
            new NumericParameter<>("masterAmplitude", 1.0, 0.0, 1.0, "");

    public final NumericParameter<Double> slaveAmplitude =
            new NumericParameter<>("slaveAmplitude", 0.0, 0.0, 1.0, "");

    public final NumericParameter<Double> noiseAmplitude =
            new NumericParameter<>("noiseAmplitude", 0.0, 0.0, 1.0, "");

    public final NumericParameter<Double> unisonDetune =
            new NumericParameter<>("unisonDetune", 0.1, 0.0, 1.0, "%");

    public final NumericParameter<Double> unisonStereo =
            new NumericParameter<>("unisonStereo", 0.1, 0.0, 1.0, "%");

    public final NumericParameter<Integer> unisonVoices =
            new NumericParameter<>("unisonVoices", 3, 1, 8, "%");

    // amp eg

    public final NumericParameter<Double> ampEgAttackRate =
            new NumericParameter<>("ampEgAttackRate", 5.0, 1.0, 100.0, "");

    public final NumericParameter<Double> ampEgDecayRate =
            new NumericParameter<>("ampEgDecayRate", 5.0, 1.0, 100.0, "");

    public final NumericParameter<Double> ampEgSustainLevel =
            new NumericParameter<>("ampEgSustainLevel", 0.6, 0.0, 1.0, "%");

    public final NumericParameter<Double> ampEgReleaseRate =
            new NumericParameter<>("ampEgReleaseRate", 5.0, 1.0, 100.0, "");

    public final NumericParameter<Double> ampAmplitude =
            new NumericParameter<>("ampAmplitude", 1.0, 0.0, 1.0, "%");


    // filter eg

    public final NumericParameter<Double> filterEgAttackRate =
            new NumericParameter<>("filterEgAttackRate", 5.0, 1.0, 100.0, "");

    public final NumericParameter<Double> filterEgDecayRate =
            new NumericParameter<>("filterEgDecayRate", 5.0, 1.0, 100.0, "");

    public final NumericParameter<Double> filterEgSustainLevel =
            new NumericParameter<>("filterEgSustainLevel", 0.6, 0.0, 1.0, "%");

    public final NumericParameter<Double> filterEgReleaseRate =
            new NumericParameter<>("filterEgReleaseRate", 5.0, 1.0, 100.0, "");

    public final NumericParameter<Double> filterEgAmount =
            new NumericParameter<>("filterEgAmount", 0.0, -10.0, 10.0, "octaves");


    // filter

    public final NumericParameter<Double> filterCutoff =
            new NumericParameter<>("filterCutoff", 22050.0, 20.0, 22050.0, "hz");

    public final NumericParameter<Double> filterQ =
            new NumericParameter<>("filterQ", 0.71, 0.71, 6.0, "%");

    public final NumericParameter<Double> filterMode =
            new NumericParameter<>("filterMode", 0.0, 0.0, 360.0, "degree");

    public final NumericParameter<Double> filterKeyboardTracking =
            new NumericParameter<>("filterKeyboardTracking", 0.0, 0.0, 1.0, "%");
}
