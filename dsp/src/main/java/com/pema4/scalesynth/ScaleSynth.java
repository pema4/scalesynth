package com.pema4.scalesynth;

import com.pema4.scalesynth.base.KeyboardEvent;
import com.pema4.scalesynth.base.generators.Generator;
import com.pema4.scalesynth.base.generators.PolyGenerator;
import com.pema4.scalesynth.dsp.generators.DualOscillator;
import com.pema4.scalesynth.dsp.processors.Amp;
import com.pema4.scalesynth.dsp.processors.Envelope;
import com.pema4.scalesynth.dsp.processors.SvfFilter;

/**
 * Main synthesizer class.
 * It is used to fill buffers with n samples.
 */
public class ScaleSynth implements Generator {
    private final ScaleSynthParameters parameters = new ScaleSynthParameters();
    private final Generator generator = new PolyGenerator(8, this::createVoice)//.then(new Delay());
    ;

    private Generator createVoice() {
        Envelope ampEnvelope = createAmpEnvelope();
        Envelope filterEnvelope = createFilterEnvelope();
        SvfFilter filter = createFilter(filterEnvelope);
        DualOscillator dualOscillator = createOscillators();
        Amp amp = createAmp(ampEnvelope);

        return dualOscillator.then(filter).then(amp);
    }

    private Amp createAmp(Envelope ampEnvelope) {
        var amp = new Amp(ampEnvelope);
        amp.setAmplitude(parameters.ampAmplitude.getDefault());
        parameters.ampAmplitude.addListener(amp::setAmplitude);
        return amp;
    }

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

        osc.setSyncEnabled(parameters.ifSynced.getDefault());
        parameters.ifSynced.addListener(osc::setSyncEnabled);

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

    public ScaleSynthParameters getParameters() {
        return parameters;
    }

    @Override
    public void generate(double[][] outputs, int n) {
        try {
        generator.generate(outputs, n);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

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
}
