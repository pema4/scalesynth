package com.pema4.scalesynth.dsp.processors;

import com.pema4.scalesynth.base.KeyboardEvent;
import com.pema4.scalesynth.base.KeyboardEventType;
import com.pema4.scalesynth.base.generators.Generator;

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

    public void setAttackRate(double attackRate) {
        this.attackRate = attackRate;
        attackCoef = Math.pow(1 - 0.01 * attackRate * attackRate / 10000, 44100 / sampleRate);
    }

    public void setDecayRate(double decayRate) {
        this.decayRate = decayRate;
        decayCoef = Math.pow(1 - 0.001 * decayRate / 100, 44100 / sampleRate);
    }

    public void setSustainLevel(double sustainLevel) {
        this.sustainLevel = sustainLevel;
    }

    public void setReleaseRate(double releaseRate) {
        this.releaseRate = releaseRate;
        releaseCoef = Math.pow(1 - 0.001 * releaseRate / 100, 44100 / sampleRate);
    }

    @Override
    public void generate(double[][] outputs, int n) {
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
                    if (closeToZero(value)) {
                        state = State.DISABLED;
                        value = 0;
                    }
                    break;
                case DISABLED:
                    output[i] = 0;
                    break;
            }
    }

    private boolean closeToZero(double value) {
        return value > -0.00000001 && value < 0.00000001;
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

    private enum State {
        ATTACK,
        DECAY,
        SUSTAIN,
        RELEASE,
        DISABLED
    }
}
