package com.pema4.scalesynth.dsp.processors;

import com.pema4.scalesynth.base.KeyboardEvent;
import com.pema4.scalesynth.base.processors.Processor;

public class SvfFilter implements Processor {
    private final SvfFilterMono[] filters = {new SvfFilterMono(), new SvfFilterMono()};
    private final Envelope filterEnvelope;
    private final double[][] filterEnvelopeOutput = new double[1][8096]; // A bit of shot in my leg (because buffer size can change)
    private double envelopeAmount;
    private double cutoff = 44100;
    private double keyboardTracking;
    private double oscFreq;
    private double pitchBendCoef = 1;

    /**
     * Constructs a new SvfFilter based on Andrew Simpler implementation.
     *
     * @param filterEnvelope filter envelope to use
     */
    public SvfFilter(Envelope filterEnvelope) {
        this.filterEnvelope = filterEnvelope;
    }

    /**
     * Sets how much envelope influences filter cutoff in the end of attack stage.
     * For example, if envelope amount is -2 octave, cutoff will be moved down by 2 octaves.
     *
     * @param amount keyboard tracking amount.
     */
    public void setEnvelopeAmount(double amount) {
        this.envelopeAmount = amount;
    }

    /**
     * Sets new filter base cutoff.
     * That base cutoff value will be modulated by envelope and keyboard tracking.
     * Note that all values lesser than 5 will be replaced by 0,
     * values greater than sampleRate / 2 - 1 will be replaced by sampleRate / 2 - 1
     *
     * @param cutoff new filter cutoff.
     */
    public void setCutoff(double cutoff) {
        this.cutoff = cutoff;
    }

    /**
     * Sets filter mode.
     * In this implementation different filter outputs can be mixed in specified proportion.
     * Values from 0 to 90 - low pass + band pass;
     * Values from 90 to 180 - band pass + high pass;
     * Values from 180 to 270 - high pass + band stop;
     * Values from 270 to 360 - band stop + low pass.
     *
     * @param mode new filter mode.
     */
    public void setMode(double mode) {
        for (SvfFilterMono filter : filters)
            filter.setMode(mode);
    }

    /**
     * Sets new Q factor value.
     *
     * @param Q Q factor value.
     */
    public void setQ(double Q) {
        for (SvfFilterMono filter : filters)
            filter.setQ(Q);
    }

    /**
     * Performs the replace processing of incoming audio.
     *
     * @param inputs input audio to be replaced (multiple channels)
     * @param n      how many samples needs processing.
     */
    @Override
    public void process(double[][] inputs, int n) {
        filterEnvelope.generate(filterEnvelopeOutput, n);
        var modulation = filterEnvelopeOutput[0];
        for (int ch = 0; ch < inputs.length; ++ch) {
            var filter = filters[ch];
            for (int i = 0; i < n; ++i) {
                var envelopeModulation = Math.pow(2, modulation[i] * envelopeAmount);
                var kbModulation = (1 - keyboardTracking) + keyboardTracking / 440 * oscFreq * pitchBendCoef;
                filter.setCutoff(cutoff * envelopeModulation * kbModulation);
                inputs[ch][i] = filters[ch].processOne(inputs[ch][i]);
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
        filterEnvelope.setSampleRate(sampleRate);
        for (SvfFilterMono filter : filters)
            filter.setSampleRate(sampleRate);
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
                filterEnvelope.handleKeyboardEvent(event);
                this.oscFreq = event.getFreq();
                break;
            case PITCH_BEND:
                pitchBendCoef = event.getFreq();
        }
    }

    public void setKeyboardTracking(double keyboardTracking) {
        this.keyboardTracking = keyboardTracking;
    }

    /**
     * Decent quality svf filter implementation based on the Andrew Simper work (link in the documentation)
     */
    static class SvfFilterMono {
        private double mode = 0;
        private double sampleRate = 44100;
        private double cutoff = 200;
        private double Q = 1;
        private double k;
        private double a1;
        private double a2;
        private double a3;
        private double m0;
        private double m1;
        private double m2;
        private double ic2eq;
        private double ic1eq;

        /**
         * Process another sample of audio.
         *
         * @param v0 new sample.
         * @return another sample of filtered audio.
         */
        double processOne(double v0) {
            double v3 = v0 - ic2eq;
            double v1 = a1 * ic1eq + a2 * v3;
            double v2 = ic2eq + a2 * ic1eq + a3 * v3;
            ic1eq = 2 * v1 - ic1eq;
            ic2eq = 2 * v2 - ic2eq;
            return m0 * v0 + m1 * v1 + m2 * v2;
        }

        /**
         * Sets filter mode.
         *
         * @param mode new filter mode.
         */
        void setMode(double mode) {
            this.mode = mode;
            calculateOutputs();
        }

        /**
         * Helper function to recalculate filter outputs based on current mode value.
         */
        void calculateOutputs() {
            var right = mode / 90 % 1;
            var left = 1 - right;
            if (mode < 90 || mode == 360) {
                m0 = 0;
                m1 = right;
                m2 = left;
            } else if (mode < 180) {
                m0 = right;
                m1 = left - k * right;
                m2 = -right;
            } else if (mode < 270) {
                m0 = 1;
                m1 = -k;
                m2 = -left;
            } else {
                m0 = left;
                m1 = -left * k;
                m2 = right;
            }
        }

        /**
         * Recalculates filter coefficients based on current cutoff, sampleRate and Q factor
         */
        void calculateCoefficients() {
            double g = Math.tan(Math.PI * cutoff / sampleRate);
            k = 1 / Q;
            a1 = 1 / (1 + g * (g + k));
            a2 = g * a1;
            a3 = g * a2;
        }

        /**
         * Sets new sample rate and recalculates coefficients.
         *
         * @param sampleRate new filter sample rate.
         */
        void setSampleRate(double sampleRate) {
            this.sampleRate = sampleRate;
            calculateCoefficients();
        }

        /**
         * Sets new Q factor value.
         *
         * @param Q Q factor value.
         */
        void setQ(double Q) {
            this.Q = Q;
            calculateCoefficients();
        }

        /**
         * Sets new filter cutoff.
         *
         * @param cutoff new filter cutoff.
         */
        void setCutoff(double cutoff) {
            // Sanity checks.
            if (cutoff < 5)
                cutoff = 5;
            else if (cutoff >= sampleRate / 2)
                cutoff = sampleRate / 2 - 1;

            this.cutoff = cutoff;
            calculateCoefficients();
        }
    }
}
