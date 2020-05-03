package com.pema4.scalesynth.dsp.generators;

import com.pema4.scalesynth.base.KeyboardEvent;
import com.pema4.scalesynth.base.KeyboardEventType;
import com.pema4.scalesynth.base.generators.Generator;

public class DualOscillator implements Generator {
    private final MystranOscillator master = new MystranOscillator();
    private final MystranOscillator slave = new MystranOscillator();
    private double slaveOctave;
    private double slaveSemi;
    private double slaveFine;
    private double drift;
    //    private boolean ifSynced;
//    private double masterMix;
//    private double slaveMix;
//    private double masterPulseWidth;
//    private double slavePulseWidth;
    private double baseFreq;
    private double driftCoef;
    private double noiseAmplitude;
//    private double slaveAmplitude;
//    private double masterAmplitude;

    public void setNoiseAmplitude(double noiseAmplitude) {
        this.noiseAmplitude = noiseAmplitude;
    }

    public void setSlaveAmplitude(double slaveAmplitude) {
//        this.slaveAmplitude = slaveAmplitude;
        slave.setAmplitude(slaveAmplitude);
    }

    public void setMasterAmplitude(double masterAmplitude) {
//        this.masterAmplitude = masterAmplitude;
        master.setAmplitude(masterAmplitude);
    }

    public void setSlaveOctave(double slaveOctave) {
        this.slaveOctave = Math.round(slaveOctave);
        updateFrequencies();
    }

    public void setSlaveSemi(double slaveSemi) {
        this.slaveSemi = Math.round(slaveSemi);
        updateFrequencies();
    }

    public void setSlaveFine(double slaveFine) {
        this.slaveFine = Math.round(slaveFine);
        updateFrequencies();
    }

    public void setDrift(double drift) {
        this.drift = drift;
    }

    public void setIfSynced(boolean ifSynced) {
//        this.ifSynced = ifSynced;
        slave.setIfSynced(ifSynced);
    }

    public void setMasterMix(double masterMix) {
//        this.masterMix = masterMix;
        master.setMix(masterMix);
    }

    public void setSlaveMix(double slaveMix) {
//        this.slaveMix = slaveMix;
        slave.setMix(slaveMix);
    }

    public void setMasterPulseWidth(double masterPulseWidth) {
//        this.masterPulseWidth = masterPulseWidth;
        master.setPulseWidth(masterPulseWidth);
    }

    public void setSlavePulseWidth(double slavePulseWidth) {
//        this.slavePulseWidth = slavePulseWidth;
        slave.setPulseWidth(slavePulseWidth);
    }

    @Override
    public void generate(double[][] outputs, int n) {
        generateMono(outputs[0], n);

        for (int ch = 1; ch < outputs.length; ++ch) {
            System.arraycopy(outputs[0], 0, outputs[ch], 0, n);
            generateNoise(outputs[ch], n);
        }

        generateNoise(outputs[0], n);
    }

    private void generateNoise(double[] output, int n) {
        for (int i = 0; i < n; ++i)
            output[i] += noiseAmplitude * (2 * Math.random() - 1);
    }

    private void generateMono(double[] outputs, int n) {
        for (int i = 0; i < n; ++i)
            outputs[i] = 0;

        master.generateMono(outputs, n);
        slave.generateMono(outputs, n);
    }

    /**
     * Sets a sample rate used in processing.
     *
     * @param sampleRate new sample rate.
     */
    @Override
    public void setSampleRate(double sampleRate) {
        master.setSampleRate(sampleRate);
        slave.setSampleRate(sampleRate);
    }

    /**
     * Called when the user presses or releases any key.
     * Useful for retriggering envelopes and LFOs.
     *
     * @param event represent a type of keyboard event.
     */
    @Override
    public void handleKeyboardEvent(KeyboardEvent event) {
        if (event.getType() == KeyboardEventType.NOTE_ON) {
            baseFreq = event.getFreq();
            driftCoef = Math.pow(2, (Math.random() * 2 - 1) * drift / 48);
            updateFrequencies();
            slave.reset();
            master.reset();
        }
    }

    private void updateFrequencies() {
        var masterFreq = baseFreq * driftCoef;
        var slaveFreq = baseFreq * driftCoef * Math.pow(2, slaveOctave + slaveSemi / 12 + slaveFine / 1200);
        master.setFreq(masterFreq);
        slave.setMasterFreq(masterFreq);
        slave.setFreq(slaveFreq);
    }
}

class MystranOscillator {
    double phase;
    double masterPhase;
    double nextValue;
    int pulseStage;
    double freq;
    double mix;
    double pulseWidth;
    double normalizedMasterFreq;
    double currValue;
    double sampleRate;
    double normalizedFreq;
    double masterTune = 0.7123456;
    double amplitude = 1;
    private boolean synced;
    private double masterFreq;

    // convenience constructor
    public MystranOscillator() {
        reset();
    }

    // The first sample BLEP
    static double poly3blep0(double t) {
        // these are just sanity checks
        // correct code doesn't need them
        if (t < 0) return 0;
        if (t > 1) return 1;

        double t2 = t * t;
        return t * t2 - 0.5f * t2 * t2;
    }

    // And second sample as wrapper, optimize if you want.
    static double poly3blep1(double t) {
        return -poly3blep0(1 - t);
    }

    public void setAmplitude(double amplitude) {
        this.amplitude = amplitude;
    }

    // the initialization code
    public void reset() {
        // set current phase to 0
        phase = 0;
        masterPhase = 0;
        // blep delay "buffer" to zero
        nextValue = 0;
        // only 2 stages here, set to first
        pulseStage = 0;
    }

    // the freq should be normalized realFreq/samplerate
    // the mix parameter is 0 for saw, 1 for pulse
    // the pwm is audio-rate buffer, since that needs treatment
    // rest are pretty trivial to turn into audio-rate sources
    void generateMono(double[] output, int n) {
        for (int i = 0; i < n; ++i) {
            // the BLEP latency is 1 sample, so first
            // take the delayed part from previous sample
            currValue = nextValue;

            // then reset the delay so we can build into it
            nextValue = 0;

            // then proceed like a trivial oscillator
            phase += normalizedFreq;
            masterPhase += normalizedMasterFreq;

            // Then replace the reset logic: loop until we
            // can't find the next discontinuity, so during
            // one sample we can process any number of them!
            while (true) {
                // Now in order of the stages of the wave-form
                // check for discontinuity during this sample.

                // Very firstly, process master oscillator reset.
                if (synced && masterPhase > 1) {
                    double exactResetTime = (masterPhase - 1) / normalizedMasterFreq;
                    double exactSlavePhase = phase - normalizedFreq * (exactResetTime);
                    double exactSlavePhaseCopy = exactSlavePhase;
                    while (true) {
                        if (pulseStage == 0) {
                            if (exactSlavePhaseCopy < pulseWidth)
                                break;
                            handleUpwardsDiscontinuity(exactSlavePhaseCopy);
                        }

                        if (pulseStage == 1) {
                            if (exactSlavePhaseCopy < 1)
                                break;
                            handleDownwardsDiscontinuity(exactSlavePhaseCopy);
                            exactSlavePhaseCopy -= 1;
                        }
                    }

                    double blepAmp = ((1 - mix) * exactSlavePhaseCopy + mix * ((pulseStage == 1 ? 1 : 0)));

                    currValue -= blepAmp * poly3blep0(exactResetTime);
                    nextValue -= blepAmp * poly3blep1(exactResetTime);

                    masterPhase -= 1;
                    phase -= exactSlavePhase;
                    pulseStage = 0;
                }

                // First is the "pulse-width" transition.
                if (pulseStage == 0) {
                    if (phase < pulseWidth)
                        break;

                    handleUpwardsDiscontinuity(phase);
                }

                if (pulseStage == 1) {
                    if (phase < 1)
                        break;

                    handleDownwardsDiscontinuity(phase);
                    phase -= 1;
                }

                // and if we are here, then there are possibly
                // more transitions to process, so keep going
            }

            // When the loop breaks (and it'll always break)
            // we have collected all the various BLEPs into our
            // output and delay buffer, so add the trivial wave
            // into the buffer, so it's properly delayed
            //
            // note: using pulseStage instead of pw-comparison
            // avoids inconsistencies from numerical inaccuracy
            nextValue += (1 - mix) * phase
                    + mix * (pulseStage != 0 ? 1 : 0);

            // and output is just what we collected, but
            // let's make it range -1 to 1 instead
            output[i] += amplitude * (2 * currValue - 1);
        }
    }

    private void handleDownwardsDiscontinuity(double slavePhase) {
        // otherwise same as the pw, except threshold 1
        double t = (slavePhase - 1) / normalizedFreq;

        // and negative transition.. normally you would
        // calculate step-sizes for all mixed waves, but
        // here both saw and pulse go from 1 to 0.. so
        // it's always the same transition size!
        currValue -= poly3blep0(t);
        nextValue -= poly3blep1(t);

        // and then we do a reset (just one!)
        pulseStage = 0;
    }

    private void handleUpwardsDiscontinuity(double slavePhase) {
        // otherwise solve transition: when during
        // this sample did we hit the pw-border..
        // t = (1-x) from: phase + (x-1)*freq = pw
        double t = (slavePhase - pulseWidth) / normalizedFreq;

        // so then scale by pulse mix
        // and add the first sample to output
        currValue += mix * poly3blep0(t);

        // and second sample to delay
        nextValue += mix * poly3blep1(t);

        // and then we proceed to next stage
        pulseStage = 1;
    }

    /**
     * Sets a sample rate used in processing.
     *
     * @param sampleRate new sample rate.
     */
    public void setSampleRate(double sampleRate) {
        this.sampleRate = sampleRate;
        normalizedFreq = freq / sampleRate;
        normalizedMasterFreq = masterFreq / sampleRate;
        reset();
    }

    public void setMix(double mix) {
        this.mix = mix;
    }

    public void setPulseWidth(double pulseWidth) {
        this.pulseWidth = pulseWidth;
    }

    public void setMasterTune(double masterTune) {
        this.masterTune = masterTune;
        normalizedMasterFreq = normalizedFreq * masterTune;
    }

    public void setFreq(double freq) {
        this.freq = freq;
        this.normalizedFreq = freq / sampleRate;
    }

    public void setMasterFreq(double masterFreq) {
        this.masterFreq = masterFreq;
        this.normalizedMasterFreq = masterFreq / sampleRate;
    }

    public void setIfSynced(boolean isSynced) {
        this.synced = isSynced;
    }
}
