package com.pema4.scalesynth.dsp.processors;

import com.pema4.scalesynth.base.KeyboardEvent;
import com.pema4.scalesynth.base.processors.Processor;

public class SvfFilter implements Processor {
    private final SvfFilterMono[] filters = {new SvfFilterMono(), new SvfFilterMono()};
    private final Envelope filterEnvelope;
    private double[][] filterEnvelopeOutput = new double[1][8096];
    private double envelopeAmount;
    private double cutoff = 44100;
    private double Q;
    private double mode;
    private double sampleRate;
    private double keyboardTracking;

    public SvfFilter(Envelope filterEnvelope) {
        this.filterEnvelope = filterEnvelope;
    }

    public void setEnvelopeAmount(double amount) {
        this.envelopeAmount = amount;
    }

    public void setCutoff(double cutoff) {
        this.cutoff = cutoff;
    }

    public void setMode(double mode) {
        this.mode = mode;
        for (int ch = 0; ch < filters.length; ++ch)
            filters[ch].setMode(mode);
    }

    public void setQ(double Q) {
        this.Q = Q;
        for (int ch = 0; ch < filters.length; ++ch)
            filters[ch].setQ(Q);
    }

    @Override
    public void process(double[][] inputs, int n) {
        filterEnvelope.generate(filterEnvelopeOutput, n);
        var modulation = filterEnvelopeOutput[0];
        for (int ch = 0; ch < inputs.length; ++ch) {
            var filter = filters[ch];
            for (int i = 0; i < n; ++i) {
                filter.setCutoff(cutoff * Math.pow(2, modulation[i] * envelopeAmount));
                filter.setCutoff(cutoff);
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
        this.sampleRate = sampleRate;
        for (int ch = 0; ch < filters.length; ++ch)
            filters[ch].setSampleRate(sampleRate);
    }

    /**
     * Called when the user presses or releases any key.
     * Useful for retriggering envelopes and LFOs.
     *
     * @param keyboardEvent represent a type of keyboard event.
     */
    @Override
    public void handleKeyboardEvent(KeyboardEvent keyboardEvent) {
        filterEnvelope.handleKeyboardEvent(keyboardEvent);
    }

    public void setKeyboardTracking(double keyboardTracking) {
        this.keyboardTracking = keyboardTracking;
    }

    static class SvfFilterMono {
        private double mode = 0;
        private double sampleRate = 44100;
        private double cutoff = 200;
        private double Q = 1;
        private double g = 0;
        private double k;
        private double a1;
        private double a2;
        private double a3;
        private double m0;
        private double m1;
        private double m2;
        private double ic2eq;
        private double ic1eq;

        double processOne(double v0) {
            double v3 = v0 - ic2eq;
            double v1 = a1 * ic1eq + a2 * v3;
            double v2 = ic2eq + a2 * ic1eq + a3 * v3;
            ic1eq = 2 * v1 - ic1eq;
            ic2eq = 2 * v2 - ic2eq;
            return m0 * v0 + m1 * v1 + m2 * v2;
        }

        void setMode(double mode) {
            this.mode = mode;
            calculateOutputs();
        }

        void calculateOutputs() {
            var right = mode / 100 % 1;
            var left = 1 - right;
            if (mode < 100) {
                m0 = 0;
                m1 = right;
                m2 = left;
            } else if (mode < 200) {
                m0 = right;
                m1 = left - k * right;
                m2 = -right;
            } else if (mode < 300) {
                m0 = 1;
                m1 = -k;
                m2 = -left;
            } else {
                m0 = left;
                m1 = -left * k;
                m2 = right;
            }
        }

        void calculateCoefficients() {
            g = Math.tan(Math.PI * cutoff / sampleRate);
            k = 1 / Q;
            a1 = 1 / (1 + g * (g + k));
            a2 = g * a1;
            a3 = g * a2;
        }

        void setSampleRate(double sampleRate) {
            this.sampleRate = sampleRate;
            calculateCoefficients();
        }

        void setQ(double Q) {
            this.Q = Q;
            calculateCoefficients();
        }

        void setCutoff(double cutoff) {
            this.cutoff = cutoff;
            calculateCoefficients();
        }
    }
}
