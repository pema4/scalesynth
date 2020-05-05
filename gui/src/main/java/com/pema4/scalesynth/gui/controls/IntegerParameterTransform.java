package com.pema4.scalesynth.gui.controls;

import com.pema4.scalesynth.base.parameters.NumericParameter;

public class IntegerParameterTransform implements ParameterTransform<Integer> {
    private final int min;
    private final int max;

    public IntegerParameterTransform(int min, int max) {
        this.min = min;
        this.max = max;
    }

    public static IntegerParameterTransform of(NumericParameter<Integer> parameter) {
        return new IntegerParameterTransform(parameter.getMin(), parameter.getMax());
    }

    @Override
    public double toPosition(Integer parameter) {
        return (double)(parameter - min) / (max - min);
    }

    @Override
    public Integer toParameter(double position) {
        return min + (int)Math.round((max - min) * position);
    }
}
