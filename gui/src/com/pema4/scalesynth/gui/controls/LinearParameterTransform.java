package com.pema4.scalesynth.gui.controls;

import com.pema4.scalesynth.base.parameters.NumericParameter;

public class LinearParameterTransform implements ParameterTransform<Double> {
    private final double min;
    private final double max;

    public LinearParameterTransform(double min, double max) {
        this.min = min;
        this.max = max;
    }

    public static LinearParameterTransform of(NumericParameter<Double> parameter) {
        return new LinearParameterTransform(parameter.getMin(), parameter.getMax());
    }

    @Override
    public double toPosition(Double parameter) {
        return (parameter - min) / (max - min);
    }

    @Override
    public Double toParameter(double position) {
        return min + (max - min) * position;
    }
}
