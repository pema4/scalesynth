package com.pema4.scalesynth.gui.controls;

import com.pema4.scalesynth.base.parameters.NumericParameter;

/**
 * ParameterTransform implementation for integer parameters.
 * This implementation is useful for cutoff/volume knobs.
 */
public class LogarithmicParameterTransform implements ParameterTransform<Double> {
    private final double min;
    private final double max;

    public LogarithmicParameterTransform(double min, double max) {
        this.min = min;
        this.max = max;
    }

    /**
     * Returns a new LogarithmicParameterTransform for that parameter.
     *
     * @param parameter a parameter for which transformations will be made.
     * @return an LogarithmicParameterTransform for that parameter.
     */
    public static LogarithmicParameterTransform of(NumericParameter<Double> parameter) {
        return new LogarithmicParameterTransform(parameter.getMin(), parameter.getMax());
    }

    /**
     * Maps parameter value (in range [min; max]) to knob position (in range [0; 1]).
     *
     * @param parameter parameter value to be mapped.
     * @return position.
     */
    @Override
    public double toPosition(Double parameter) {
        if (min == 0)
            return Math.log(parameter / 1e-3) / Math.log(max / 1e-3);

        return Math.log(parameter / min) / Math.log(max / min);
    }

    /**
     * Maps knob position (in range [0; 1]) to parameter value (in range [min; max]).
     *
     * @param position knob position to be mapped.
     * @return parameter value.
     */
    @Override
    public Double toParameter(double position) {
        if (min == 0)
            return 1e-3 * Math.exp(position * Math.log(max / 1e-3));

        return min * Math.exp(position * Math.log(max / min));
    }
}