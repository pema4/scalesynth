package com.scalesynth;

import com.scalesynth.base.KeyboardEvent;
import com.scalesynth.base.generators.Generator;
import com.scalesynth.base.generators.PolyGenerator;
import com.scalesynth.base.parameters.Parameter;
import com.scalesynth.dsp.generators.DualOscillator;
import com.scalesynth.dsp.generators.Envelope;
import com.scalesynth.dsp.processors.Amp;
import com.scalesynth.dsp.processors.SvfFilter;

/**
 * Main synthesizer class.
 * It is used to fill buffers with n samples.
 */
public class ScaleSynth implements Generator {
    private final ScaleSynthParameters parameters = new ScaleSynthParameters();
    private final Generator generator = new PolyGenerator(8, this::createVoice);

    /**
     * Returns a new generator, representing one voice.
     *
     * @return a new voice.
     */
    private Generator createVoice() {
        Envelope ampEnvelope = createAmpEnvelope();
        Envelope filterEnvelope = createFilterEnvelope();
        SvfFilter filter = createFilter(filterEnvelope);
        DualOscillator dualOscillator = createOscillators();
        Amp amp = createAmp(ampEnvelope);

        return dualOscillator.then(filter).then(amp);
    }

    /**
     * Sets up and returns an amplifier.
     *
     * @param ampEnvelope an envelope to use.
     * @return new amplifier processor.
     */
    private Amp createAmp(Envelope ampEnvelope) {
        var amp = new Amp(ampEnvelope);
        amp.setAmplitude(parameters.ampAmplitude.getDefault());
        parameters.ampAmplitude.addListener(amp::setAmplitude);
        return amp;
    }

    /**
     * Sets up and returns a new oscillator.
     *
     * @return a new oscillator.
     */
    private DualOscillator createOscillators() {
        var osc = new DualOscillator();

        osc.setSlaveOctave(parameters.slaveOctave.getDefault());
        parameters.slaveOctave.addListener(osc::setSlaveOctave);

        osc.setSlaveSemi(parameters.slaveSemi.getDefault());
        parameters.slaveSemi.addListener(osc::setSlaveSemi);

        osc.setSlaveFine(parameters.slaveFine.getDefault());
        parameters.slaveFine.addListener(osc::setSlaveFine);

        osc.setDrift(parameters.drift.getDefault());
        parameters.drift.addListener(osc::setDrift);

        osc.setSyncEnabled(parameters.syncEnabled.getDefault());
        parameters.syncEnabled.addListener(osc::setSyncEnabled);

        osc.setMasterMix(parameters.masterMix.getDefault());
        parameters.masterMix.addListener(osc::setMasterMix);

        osc.setSlaveMix(parameters.slaveMix.getDefault());
        parameters.slaveMix.addListener(osc::setSlaveMix);

        osc.setMasterPulseWidth(parameters.masterPW.getDefault());
        parameters.masterPW.addListener(osc::setMasterPulseWidth);

        osc.setSlavePulseWidth(parameters.slavePW.getDefault());
        parameters.slavePW.addListener(osc::setSlavePulseWidth);

        osc.setMasterAmplitude(parameters.masterAmplitude.getDefault());
        parameters.masterAmplitude.addListener(osc::setMasterAmplitude);

        osc.setSlaveAmplitude(parameters.slaveAmplitude.getDefault());
        parameters.slaveAmplitude.addListener(osc::setSlaveAmplitude);

        osc.setNoiseAmplitude(parameters.noiseAmplitude.getDefault());
        parameters.noiseAmplitude.addListener(osc::setNoiseAmplitude);

        osc.setUnisonVoices(parameters.unisonVoices.getDefault());
        parameters.unisonVoices.addListener(osc::setUnisonVoices);

        osc.setUnisonDetune(parameters.unisonDetune.getDefault());
        parameters.unisonDetune.addListener(osc::setUnisonDetune);

        osc.setUnisonStereo(parameters.unisonStereo.getDefault());
        parameters.unisonStereo.addListener(osc::setUnisonStereo);

        return osc;
    }

    /**
     * Sets up and returns a new filter.
     *
     * @param filterEnvelope an envelope to modulate new filter.
     * @return a new filter.
     */
    private SvfFilter createFilter(Envelope filterEnvelope) {
        var filter = new SvfFilter(filterEnvelope);

        filter.setCutoff(parameters.filterCutoff.getDefault());
        parameters.filterCutoff.addListener(filter::setCutoff);

        filter.setQ(parameters.filterQ.getDefault());
        parameters.filterQ.addListener(filter::setQ);

        filter.setKeyboardTracking(parameters.filterKeyboardTracking.getDefault());
        parameters.filterKeyboardTracking.addListener(filter::setKeyboardTracking);

        filter.setEnvelopeAmount(parameters.filterEgAmount.getDefault());
        parameters.filterEgAmount.addListener(filter::setEnvelopeAmount);

        filter.setMode(parameters.filterMode.getDefault());
        parameters.filterMode.addListener(filter::setMode);
        return filter;
    }

    /**
     * Sets up and returns a new filter envelope.
     *
     * @return a new filter envelope.
     */
    private Envelope createFilterEnvelope() {
        var filterEnvelope = new Envelope();

        filterEnvelope.setAttackRate(parameters.filterEgAttackRate.getDefault());
        parameters.filterEgAttackRate.addListener(filterEnvelope::setAttackRate);

        filterEnvelope.setDecayRate(parameters.filterEgDecayRate.getDefault());
        parameters.filterEgDecayRate.addListener(filterEnvelope::setDecayRate);

        filterEnvelope.setSustainLevel(parameters.filterEgSustainLevel.getDefault());
        parameters.filterEgSustainLevel.addListener(filterEnvelope::setSustainLevel);

        filterEnvelope.setReleaseRate(parameters.filterEgReleaseRate.getDefault());
        parameters.filterEgReleaseRate.addListener(filterEnvelope::setReleaseRate);
        return filterEnvelope;
    }

    /**
     * Sets up and returns a new amplifier envelope.
     *
     * @return a new amplifier envelope.
     */
    private Envelope createAmpEnvelope() {
        var ampEnvelope = new Envelope();

        ampEnvelope.setAttackRate(parameters.ampEgAttackRate.getDefault());
        parameters.ampEgAttackRate.addListener(ampEnvelope::setAttackRate);

        ampEnvelope.setDecayRate(parameters.ampEgDecayRate.getDefault());
        parameters.ampEgDecayRate.addListener(ampEnvelope::setDecayRate);

        ampEnvelope.setSustainLevel(parameters.ampEgSustainLevel.getDefault());
        parameters.ampEgSustainLevel.addListener(ampEnvelope::setSustainLevel);

        ampEnvelope.setReleaseRate(parameters.ampEgReleaseRate.getDefault());
        parameters.ampEgReleaseRate.addListener(ampEnvelope::setReleaseRate);
        return ampEnvelope;
    }

    /**
     * Returns an object containing all parameters of this synth.
     *
     * @return an instance of the ScaleSynthParameters class used in this synth.
     */
    public ScaleSynthParameters getParameters() {
        return parameters;
    }

    /**
     * Generates of the audio.
     * Note that content of {@code outputs} can be overwritten (this behaviour depends on a subclass).
     *
     * @param outputs buffers to place generated audio into.
     * @param n       how many samples to generate.
     */
    @Override
    public void generate(double[][] outputs, int n) {
        try {
            generator.generate(outputs, n);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    /**
     * Sets a sample rate used in processing.
     *
     * @param sampleRate new sample rate.
     */
    @Override
    public void setSampleRate(double sampleRate) {
        generator.setSampleRate(sampleRate);
    }

    /**
     * Called when the user presses or releases any key.
     * Useful for retriggering envelopes and LFOs.
     *
     * @param keyboardEvent represent a type of keyboard event.
     */
    @Override
    public void handleKeyboardEvent(KeyboardEvent keyboardEvent) {
        generator.handleKeyboardEvent(keyboardEvent);
    }

    /**
     * Removes all listeners of ScalSynthParameter instance.
     */
    public void stop() {
        var fields = parameters.getClass().getDeclaredFields();
        try {
            for (var field : fields) {
                var parameter = field.get(parameters);
                if (Parameter.class.isAssignableFrom(field.getType())) {
                    var method = field.getType().getMethod("close");
                    method.invoke(parameter);
                }
            }
        } catch (ReflectiveOperationException e) {
            e.printStackTrace();
        }
    }
}
