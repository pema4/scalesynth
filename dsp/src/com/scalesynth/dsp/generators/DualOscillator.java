package com.scalesynth.dsp.generators;

import com.scalesynth.base.KeyboardEvent;
import com.scalesynth.base.generators.Generator;

/**
 * <p>Represents a pair of oscillators, noise generator and a mixer.</p>
 * <p>Main features:
 * <p>1. For both oscillators there are pulse width, saw-pulse mix and amplitude parameters;</p>
 * <p>2. The second oscillator ("slave") can be synced to the first ("master");</p>
 * <p>3. Relative pitch of the second oscillator can be adjusted (in octaves, semitones and cents);</p>
 * <p>4. Support of oscillator drift. Each note is played with slightly different pitch and phase for more analog'ish sound;</p>
 * <p>5. Support of unison. Number of voices, detune and stereo width can be adjusted;</p>
 * <p>6. White noise generator (with adjustable amplitude).</p>
 */
public class DualOscillator implements Generator {
    private final double[] tempBuffer = new double[8096]; // probably will change it.
    private BlepOscillator[] masters = new BlepOscillator[0];
    private BlepOscillator[] slaves = new BlepOscillator[0];

    // Master oscillator controls.
    private double masterPulseWidth;
    private double masterMix;

    // Slave oscillator controls.
    private double slaveOctave;
    private double slaveSemi;
    private double slaveFine;
    private double slavePulseWidth;
    private double slaveMix;
    private boolean syncEnabled;

    // Drift controls.
    private double drift;

    // Mixer controls.
    private double masterAmplitude;
    private double slaveAmplitude;
    private double noiseAmplitude;

    // Unison controls.
    private int unisonVoices;
    private double unisonDetune;
    private double unisonStereo;

    // Other parameters and temporary variables.
    private double driftCoef;
    private double baseFreq;
    private double sampleRate;
    private double pitchBendCoef = 1;

    /**
     * Sets the master oscillator pulse pulse width.
     * <p>
     * This value is supposed to be in range [0.01, 0.99] (because at 0 and at 1 there is no sound).
     * <p>
     * <p>Pulse width is a relative length of the lower part of rectangle wave. Examples:</p>
     * <p>1. PW is 0.25, wave looks like "_---";</p>
     * <p>2. PW is 0.5, wave looks like "__--";</p>
     * <p>3. PW is 0.75, wave looks like "___-";</p>
     *
     * @param masterPulseWidth a new pulse width of the master oscillator.
     */
    public synchronized void setMasterPulseWidth(double masterPulseWidth) {
        this.masterPulseWidth = masterPulseWidth;
        for (var master : masters)
            master.setPulseWidth(masterPulseWidth);
    }

    /**
     * Sets the master oscillator saw-pulse mix.
     * <p>
     * This value is supposed to be in range [0, +1].
     * <p>
     * At 0 oscillator oscillator outputs sawtooth wave, at 1 - rectangle wave.
     *
     * @param masterMix a new saw-pulse mix of the master oscillator.
     */
    public synchronized void setMasterMix(double masterMix) {
        this.masterMix = masterMix;
        for (var master : masters)
            master.setMix(masterMix);
    }

    /**
     * Sets the slave oscillator relative pitch (in octaves).
     * <p>
     * This value is supposed to be in range [-3, +3].
     *
     * @param slaveOctave how many octaves the slave oscillator is higher than the master
     */
    public synchronized void setSlaveOctave(int slaveOctave) {
        this.slaveOctave = slaveOctave;
        updateFrequencies();
    }

    /**
     * Sets the slave oscillator relative pitch (in semitones).
     * <p>
     * This value is supposed to be in range [-12, +12].
     *
     * @param slaveSemi how many semitones the slave oscillator is higher than the master
     */
    public synchronized void setSlaveSemi(int slaveSemi) {
        this.slaveSemi = slaveSemi;
        updateFrequencies();
    }

    /**
     * Sets the slave oscillator relative pitch (in cents).
     * <p>
     * This value is supposed to be in range [-50, +50].
     *
     * @param slaveFine how many octaves the slave oscillator is higher than the master
     */
    public synchronized void setSlaveFine(int slaveFine) {
        this.slaveFine = slaveFine;
        updateFrequencies();
    }

    /**
     * Sets the slave oscillator pulse pulse width.
     * <p>
     * This value is supposed to be in range [0.01, 0.99] (because at 0 and at 1 there is no sound).
     * <p>
     * <p>Pulse width is a relative length of the lower part of rectangle wave. Examples:</p>
     * <p>1. PW is 0.25, wave looks like "_---";</p>
     * <p>2. PW is 0.5, wave looks like "__--";</p>
     * <p>3. PW is 0.75, wave looks like "___-";</p>
     *
     * @param slavePulseWidth a new pulse width of the slave oscillator.
     */
    public synchronized void setSlavePulseWidth(double slavePulseWidth) {
        this.slavePulseWidth = slavePulseWidth;
        for (var slave : slaves)
            slave.setPulseWidth(slavePulseWidth);
    }

    /**
     * Sets the slave oscillator saw-pulse mix.
     * <p>
     * This value is supposed to be in range [0, +1].
     * <p>
     * At 0 oscillator oscillator outputs sawtooth wave, at 1 - rectangle wave.
     *
     * @param slaveMix a new saw-pulse mix of the slave oscillator.
     */
    public synchronized void setSlaveMix(double slaveMix) {
        this.slaveMix = slaveMix;
        for (var slave : slaves)
            slave.setMix(slaveMix);
    }

    /**
     * Enables or disables the slave oscillator hard sync to the master oscillator.
     * <p>
     * When oscillator A is hard synced to oscillator B,
     * A's phase is reset whenever B's phase is reset.
     *
     * @param syncEnabled is hard sync enabled.
     */
    public synchronized void setSyncEnabled(boolean syncEnabled) {
        this.syncEnabled = syncEnabled;
        for (var slave : slaves)
            slave.setSyncEnabled(syncEnabled);
    }

    /**
     * Sets the master oscillator amplitude.
     * <p>
     * This value is supposed to be in range [0, +1].
     *
     * @param masterAmplitude a new noise amplitude of the master oscillator.
     */
    public synchronized void setMasterAmplitude(double masterAmplitude) {
        this.masterAmplitude = masterAmplitude;
        for (var master : masters)
            master.setAmplitude(masterAmplitude);
    }

    /**
     * Sets the slave oscillator amplitude.
     * <p>
     * This value is supposed to be in range [0, 1].
     *
     * @param slaveAmplitude a new noise amplitude of the slave oscillator.
     */
    public synchronized void setSlaveAmplitude(double slaveAmplitude) {
        this.slaveAmplitude = slaveAmplitude;
        for (var slave : slaves)
            slave.setAmplitude(slaveAmplitude);
    }

    /**
     * Sets the noise generator amplitude.
     * <p>
     * This value is supposed to be in range [0, +1].
     *
     * @param noiseAmplitude a new noise amplitude of the noise generator.
     */
    public synchronized void setNoiseAmplitude(double noiseAmplitude) {
        this.noiseAmplitude = noiseAmplitude;
    }

    /**
     * Sets a drift coefficient of the oscillators.
     * <p>
     * This value is supposed to be in range [0, +1].
     * <p>
     * Drifting is a small fluctuation of oscillator's pitch that can be heard in analogue gear.
     * In this implementation every played note is slightly off tune.
     * So this parameter controls the amplitude of that pitch fluctuations (with maximum amplitude of 25 cents).
     *
     * @param drift a new drift coefficient of the oscillators.
     */
    public synchronized void setDrift(double drift) {
        this.drift = drift;
    }

    /**
     * Sets a number of subvoices playing in unison for one note.
     * <p>
     * This value is supposed to be in range [1, 8].
     * <p>
     * Each unison subvoice has slightly different pitch and panning.
     * That sound can be described as "smooth" and "fat".
     *
     * @param voices a new number of unison voices.
     */
    public synchronized void setUnisonVoices(int voices) {
        this.unisonVoices = voices;
        createVoices();
    }

    /**
     * Sets the maximum pitch offset of unison voices.
     * <p>
     * This value is supposed to be in range [0, 1].
     * <p>
     * This parameter controls how much not "in tune" are unison voices.
     * At lower values sound can be described as "sharp" and "flanger-like",
     * at higher - as "fat" and "rich".
     *
     * @param detune a new amount of detune of unison voices.
     */
    public synchronized void setUnisonDetune(double detune) {
        this.unisonDetune = detune;
        updateFrequencies();
    }

    /**
     * Sets the maximum pitch offset of unison voices.
     * <p>
     * This value is supposed to be in range [0, 1].
     * <p>
     * Unison voices playing at lower pitches are moved to the left channel, and vice versa.
     * This parameter controls stereo width of the output.
     * At 0 sound is fully mono, at 1 each channel - fully stereo.
     *
     * @param unisonStereo a new amount of unison stereo width.
     */
    public synchronized void setUnisonStereo(double unisonStereo) {
        this.unisonStereo = unisonStereo;
    }

    /**
     * Reallocates unison voices.
     */
    private void createVoices() {
        masters = new BlepOscillator[unisonVoices];
        slaves = new BlepOscillator[unisonVoices];
        for (int i = 0; i < unisonVoices; ++i) {
            masters[i] = new BlepOscillator();
            masters[i].setPulseWidth(masterPulseWidth);
            masters[i].setMix(masterMix);
            masters[i].setAmplitude(masterAmplitude);
            masters[i].setSampleRate(sampleRate);

            slaves[i] = new BlepOscillator();
            slaves[i].setPulseWidth(slavePulseWidth);
            slaves[i].setMix(slaveMix);
            slaves[i].setSyncEnabled(syncEnabled);
            slaves[i].setAmplitude(slaveAmplitude);
            slaves[i].setSampleRate(sampleRate);
        }
        updateFrequencies();
        reset();
    }

    /**
     * Generates of the audio.
     * Note that content of {@code outputs} is overwritten.
     *
     * @param outputs buffers to place generated audio into.
     * @param n       how many samples to generate.
     */
    @Override
    public synchronized void generate(double[][] outputs, int n) {
        var left = outputs[0];
        var right = outputs[1];


        for (int ch = 0; ch < outputs.length; ++ch)
            for (int i = 0; i < n; ++i)
                outputs[ch][i] = noiseAmplitude * (2 * Math.random() - 1);

        for (int voice = 0; voice < unisonVoices; ++voice) {
            // calculate left and right channels volumes for the voice.
            double rightAmp;
            if (unisonVoices == 1)
                rightAmp = 0.5;
            else
                rightAmp = 0.5 * (1 - unisonStereo) + unisonStereo * voice / (unisonVoices - 1);
            double leftAmp = (1 - rightAmp);

            // also scale volumes by 1 / unisonVoices, so unisonVoices doesn't affect volumes.
            rightAmp /= unisonVoices;
            leftAmp /= unisonVoices;

            // fill buffers with some noise.
            for (int i = 0; i < n; ++i)
                tempBuffer[i] = noiseAmplitude / unisonVoices * (2 * Math.random() - 1);

            // render master and slave parts on top of the noise.
            masters[voice].generate(tempBuffer, n);
            slaves[voice].generate(tempBuffer, n);

            for (int i = 0; i < n; ++i) {
                var x = tempBuffer[i];
                left[i] += x * leftAmp;
                right[i] += x * rightAmp;
            }
        }
    }

    /**
     * Sets a sample rate used in processing.
     *
     * @param sampleRate new sample rate.
     */
    @Override
    public void setSampleRate(double sampleRate) {
        this.sampleRate = sampleRate;

        for (var master : masters)
            master.setSampleRate(sampleRate);

        for (var slave : slaves)
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
        switch (event.getType()) {
            case NOTE_ON:
                reset();
                baseFreq = event.getFreq();
                driftCoef = Math.pow(2, (Math.random() * 2 - 1) * drift / 48);
                updateFrequencies();
                break;
            case PITCH_BEND:
                pitchBendCoef = event.getFreq();
                updateFrequencies();
                break;
        }
    }

    /**
     * Updates frequencies of oscillator based on current oscillator state.
     */
    private void updateFrequencies() {
        var masterFreq = baseFreq * driftCoef * pitchBendCoef;
        var slaveFreq = baseFreq * driftCoef * Math.pow(2, slaveOctave + slaveSemi / 12 + slaveFine / 1200) * pitchBendCoef;

        for (int i = 0; i < unisonVoices; ++i) {
            // how much i'th voice is distanced from the center (from -0.5 to 0.5 in semitones).
            double voiceOffset;
            if (unisonVoices == 1)
                voiceOffset = 0;
            else
                voiceOffset = (2.0 * i / (unisonVoices - 1) - 1) / 24;

            // freq multiplier for i'th voice.
            var unisonCoef = Math.pow(2, unisonDetune * voiceOffset);

            // and finally update frequencies
            masters[i].setFreq(masterFreq * unisonCoef);
            slaves[i].setMasterFreq(masterFreq * unisonCoef);
            slaves[i].setFreq(slaveFreq * unisonCoef);
        }
    }

    /**
     * Resets all oscillator to some random phase.
     */
    private void reset() {
        for (var master : masters)
            master.reset(Math.random(), Math.random());
        for (var slave : slaves)
            slave.reset(Math.random(), Math.random());
    }

    /**
     * The implementation of VA oscillator based on BLEPs.
     * <p>
     * In short: resulting audio is the sum of naive aliased waveform and some correcting signal.
     * Result is an approximation of bandlimited waveform (without aliasing).
     */
    static class BlepOscillator {
        private double phase;
        private double masterPhase;
        private double nextValue;
        private int pulseStage;
        private double freq;
        private double mix;
        private double pulseWidth;
        private double normalizedMasterFreq;
        private double currValue;
        private double sampleRate;
        private double normalizedFreq;
        private double amplitude = 1;
        private boolean syncEnabled;
        private double masterFreq;

        /**
         * Constructs a new instance of the MystranOscillator class.
         */
        public BlepOscillator() {
            reset(0, 0);
        }

        /**
         * Returns a value of the 2-sample PolyBLEP function. This function is for the first sample.
         *
         * @param t an inter-sample time when upwards discontinuity happened.
         * @return value of that PolyBLEP function (positive value).
         */
        private static double poly3blep0(double t) {
            // these are just sanity checks
            // correct code doesn't need them
            if (t < 0) return 0;
            if (t > 1) return 1;

            double t2 = t * t;
            return t * t2 - 0.5f * t2 * t2;
        }

        /**
         * Returns a value of the 2-sample PolyBLEP function. This function is for the second sample.
         *
         * @param t an inter-sample time when upwards discontinuity happened.
         * @return value of that PolyBLEP function (negative value).
         */
        private static double poly3blep1(double t) {
            return -poly3blep0(1 - t);
        }

        /**
         * Resets a state of this oscillator (use zero phase for the best results).
         *
         * @param phase       a new phase of the slave oscillator.
         * @param masterPhase a new phase of the master oscillator.
         */
        public void reset(double phase, double masterPhase) {
            this.phase = phase;
            this.masterPhase = masterPhase;
            nextValue = 0;
            pulseStage = 0;
        }

        /**
         * Generates a new audio and adds it to the buffer.
         *
         * @param output buffer to add generated audio into.
         * @param n      how many samples to generate.
         */
        public void generate(double[] output, int n) {
            for (int i = 0; i < n; ++i) {
                // take the delayed part from previous sample
                currValue = nextValue;

                // reset the delay so we can build into it
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

                    // Very firstly, process master oscillator reset. This part is buggy.
                    if (syncEnabled && masterPhase > 1) {
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

                    // First is the upwards transition.
                    if (pulseStage == 0) {
                        if (phase < pulseWidth)
                            break;

                        handleUpwardsDiscontinuity(phase);
                    }

                    // Second is the downwards transition.
                    if (pulseStage == 1) {
                        if (phase < 1)
                            break;

                        handleDownwardsDiscontinuity(phase);
                        phase -= 1;
                    }
                }

                // add naively generated value to the buffer.
                nextValue += (1 - mix) * phase + mix * (pulseStage != 0 ? 1 : 0);

                // and output is just what we collected, but
                // let's make it range [-amplitude, amplitude]
                output[i] += amplitude * (2 * currValue - 1);
            }
        }

        private void handleDownwardsDiscontinuity(double slavePhase) {
            // inter-sample time when transition happened.
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
            // inter-sample time when transition happened.
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
            reset(0, 0);
        }

        /**
         * Sets a new value of this oscillator saw-pulse mix (described in {@link DualOscillator})
         *
         * @param mix a new value of saw-pulse mix.
         */
        public void setMix(double mix) {
            this.mix = mix;
        }

        /**
         * Sets a new value of this oscillator pulse width (described in {@link DualOscillator})
         *
         * @param pulseWidth a new value of pulse width.
         */
        public void setPulseWidth(double pulseWidth) {
            this.pulseWidth = pulseWidth;
        }

        /**
         * Sets a new value of slave oscillator's frequency.
         *
         * @param freq a new value of slave oscillator's frequency.
         */
        public void setFreq(double freq) {
            this.freq = freq;
            this.normalizedFreq = freq / sampleRate; // this algorithm works with normalized frequencies.
        }

        /**
         * Sets a new value of master oscillator's frequency.
         *
         * @param masterFreq a new value of master oscillator's frequency.
         */
        public void setMasterFreq(double masterFreq) {
            this.masterFreq = masterFreq;
            this.normalizedMasterFreq = masterFreq / sampleRate;
        }

        /**
         * Sets if the slave oscillator is hard synced to the master oscillator frequency.
         *
         * @param syncEnabled if the slave oscillator is hard synced to the master oscillator frequency.
         */
        public void setSyncEnabled(boolean syncEnabled) {
            this.syncEnabled = syncEnabled;
        }

        /**
         * Sets a new value of the output amplitude.
         *
         * @param amplitude a new value of the output amplitude.
         */
        public void setAmplitude(double amplitude) {
            this.amplitude = amplitude;
        }
    }
}

