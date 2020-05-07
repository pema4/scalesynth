package com.pema4.scalesynth.gui.controls;

import com.pema4.scalesynth.base.parameters.NumericParameter;

/**
 * ParameterTransform implementation for floating point parameters.
 */
public class LinearParameterTransform implements ParameterTransform<Double> {
    private final double min;
    private final double max;

    /**
     * Construct a new LinearParameterTransform instance.
     *
     * @param min a minimum value of the parameter.
     * @param max a maximum value of the parameter.
     */
    public LinearParameterTransform(double min, double max) {
        this.min = min;
        this.max = max;
    }

    /**
     * Returns a new LinearParameterTransform for that parameter.
     *
     * @param parameter a parameter for which transformations will be made.
     * @return an instance of LinearParameterTransform for that parameter.
     */
    public static LinearParameterTransform of(NumericParameter<Double> parameter) {
        return new LinearParameterTransform(parameter.getMin(), parameter.getMax());
    }

    /**
     * Maps parameter value (in range [min; max]) to knob position (in range [0; 1]).
     *
     * @param parameter parameter value to be mapped.
     * @return position.
     */
    @Override
    public double toPosition(Double parameter) {
        return (parameter - min) / (max - min);
    }

    /**
     * Maps knob position (in range [0; 1]) to parameter value (in range [min; max]).
     *
     * @param position knob position to be mapped.
     * @return parameter value.
     */
    @Override
    public Double toParameter(double position) {
        return min + (max - min) * position;
    }
}
