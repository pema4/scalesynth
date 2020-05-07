package com.pema4.scalesynth.gui.controls;

import com.pema4.scalesynth.base.parameters.NumericParameter;

/**
 * ParameterTransform implementation for integer parameters.
 */
public class IntegerParameterTransform implements ParameterTransform<Integer> {
    private final int min;
    private final int max;

    /**
     * Construct a new IntegerParameterTransform instance.
     *
     * @param min a minimum value of the parameter.
     * @param max a maximum value of the parameter.
     */
    public IntegerParameterTransform(int min, int max) {
        this.min = min;
        this.max = max;
    }

    /**
     * Returns a new IntegerParameterTransform for that parameter.
     *
     * @param parameter a parameter for which transformations will be made.
     * @return an IntegerParameterTransform for that parameter.
     */
    public static IntegerParameterTransform of(NumericParameter<Integer> parameter) {
        return new IntegerParameterTransform(parameter.getMin(), parameter.getMax());
    }

    /**
     * Maps parameter value (in range [min; max]) to knob position (in range [0; 1]).
     *
     * @param parameter parameter value to be mapped.
     * @return position.
     */
    @Override
    public double toPosition(Integer parameter) {
        return (double)(parameter - min) / (max - min);
    }

    /**
     * Maps knob position (in range [0; 1]) to parameter value (in range [min; max]).
     *
     * @param position knob position to be mapped.
     * @return parameter value.
     */
    @Override
    public Integer toParameter(double position) {
        return min + (int)Math.round((max - min) * position);
    }
}
