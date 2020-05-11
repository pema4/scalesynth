package com.scalesynth.dsp.generators;

import com.scalesynth.base.KeyboardEvent;
import com.scalesynth.base.KeyboardEventType;
import com.scalesynth.base.generators.Generator;

/**
 * Represents an ADSR-envelope - main modulation source in this synthesizer.
 * <p>
 * It has 4 stages:
 * <p>
 * 1. In attack stage output rises from 0 to 1. This stage is started with Note On message;
 * <p>
 * 2. In decay stage output decreases from 1 to sustain level;
 * <p>
 * 3. At sustain stage output is contant;
 * <p>
 * 4. At release stage outputs decreases from current envelope level to 0.
 * This stage is started with Note Off message.
 */
public class Envelope implements Generator {
    private double sampleRate = 1; // чтобы не делить на 0
    private double attackCoef;
    private double sustainLevel;
    private double decayCoef;
    private double releaseCoef;
    private double value;
    private State state = State.DISABLED;
    private double attackRate;
    private double decayRate;
    private double releaseRate;

    /**
     * Sets a rate of the attack stage. Lower rate - faster attack stage.
     * <p>
     * This value is supposed to be in range [5, 100].
     *
     * @param attackRate a new attack rate.
     */
    public synchronized void setAttackRate(double attackRate) {
        this.attackRate = attackRate;
        attackCoef = Math.pow(1 - 0.01 * attackRate * attackRate / 10000, 44100 / sampleRate);
    }

    /**
     * Sets a rate of the decay stage. Lower rate - faster decay stage.
     * <p>
     * This value is supposed to be in range [5, 100].
     *
     * @param decayRate a new decay rate.
     */
    public synchronized void setDecayRate(double decayRate) {
        this.decayRate = decayRate;
        decayCoef = Math.pow(1 - 0.001 * decayRate / 100, 44100 / sampleRate);
    }

    /**
     * Sets a level of the sustain stage.
     * <p>
     * This value is supposed to be in range [0, 1].
     *
     * @param sustainLevel a new sustain level.
     */
    public synchronized void setSustainLevel(double sustainLevel) {
        this.sustainLevel = sustainLevel;
    }

    /**
     * Sets a rate of the release stage. Lower rate - faster release stage.
     * <p>
     * This value is supposed to be in range [5, 100].
     *
     * @param releaseRate a new release rate.
     */
    public synchronized void setReleaseRate(double releaseRate) {
        this.releaseRate = releaseRate;
        releaseCoef = Math.pow(1 - 0.001 * releaseRate / 100, 44100 / sampleRate);
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
        var output = outputs[0];
        for (int i = 0; i < n; ++i)
            switch (state) {
                case ATTACK:
                    value = 1.05 * (1 - (1 - value / 1.05) * attackCoef);
                    if (value >= 1) {
                        output[i] = 1;
                        state = State.DECAY;
                        value = 1;
                    } else
                        output[i] = value;
                    break;
                case DECAY:
                    value *= decayCoef;
                    if (value - sustainLevel < 0) {
                        output[i] = sustainLevel;
                        state = State.SUSTAIN;
                    } else
                        output[i] = value;
                    break;
                case SUSTAIN:
                    output[i] = sustainLevel;
                    break;
                case RELEASE:
                    value *= releaseCoef;
                    output[i] = value;
                    if (value > -0.00000001 && value < 0.00000001) {
                        state = State.DISABLED;
                        value = 0;
                    }
                    break;
                case DISABLED:
                    output[i] = 0;
                    break;
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
        setAttackRate(attackRate);
        setDecayRate(decayRate);
        setReleaseRate(releaseRate);
    }

    /**
     * Called when the user presses or releases any key.
     * Useful for retriggering envelopes and LFOs.
     *
     * @param keyboardEvent represent a type of keyboard event.
     */
    @Override
    public void handleKeyboardEvent(KeyboardEvent keyboardEvent) {
        if (keyboardEvent.getType() == KeyboardEventType.NOTE_ON) {
            state = State.ATTACK;
            value = 0;
        } else if (keyboardEvent.getType() == KeyboardEventType.NOTE_OFF) {
            state = State.RELEASE;
        }
    }

    /**
     * Returns true if component is active (does something useful).
     * This is used mainly to shutdown inactive voices.
     *
     * @return true if component is active, otherwise false.
     */
    @Override
    public boolean isActive() {
        return state != State.DISABLED;
    }

    /**
     * Represents current state of the envelope.
     */
    private enum State {
        ATTACK,
        DECAY,
        SUSTAIN,
        RELEASE,
        DISABLED
    }
}
