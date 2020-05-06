package com.pema4.scalesynth.gui.controls;

import com.pema4.scalesynth.base.parameters.NumericParameter;

public class LogarithmicParameterTransform implements ParameterTransform<Double> {
    private final double min;
    private final double max;

    public LogarithmicParameterTransform(double min, double max) {
        this.min = min;
        this.max = max;
    }

    public static LogarithmicParameterTransform of(NumericParameter<Double> parameter) {
        return new LogarithmicParameterTransform(parameter.getMin(), parameter.getMax());
    }

    @Override
    public double toPosition(Double parameter) {
        if (min == 0)
            return Math.log(parameter / 1e-3) / Math.log(max / 1e-3);

        return Math.log(parameter / min) / Math.log(max / min);
    }

    @Override
    public Double toParameter(double position) {
        if (min == 0)
            return 1e-3 * Math.exp(position * Math.log(max / 1e-3));

        return min * Math.exp(position * Math.log(max / min));
    }
}