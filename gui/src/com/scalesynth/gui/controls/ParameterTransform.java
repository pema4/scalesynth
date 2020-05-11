package com.scalesynth.gui.controls;

/**
 * ParameterTransform is used to map parameter value to knob position and vise versa.
 */
public interface ParameterTransform<T> {
    /**
     * Maps parameter value (in range [min; max]) to knob position (in range [0; 1]).
     *
     * @param parameter parameter value to be mapped.
     * @return position.
     */
    double toPosition(T parameter);

    /**
     * Maps knob position (in range [0; 1]) to parameter value (in range [min; max]).
     *
     * @param position knob position to be mapped.
     * @return parameter value.
     */
    T toParameter(double position);

    /**
     * Returns a ParameterTransformer instance that used reversed knob position.
     *
     * @return a ParameterTransformer instance that used reversed knob position.
     */
    default ParameterTransform<T> inverted() {
        var base = this;
        return new ParameterTransform<>() {
            @Override
            public double toPosition(T parameter) {
                return 1 - base.toPosition(parameter);
            }

            @Override
            public T toParameter(double position) {
                return base.toParameter(1 - position);
            }
        };
    }
}
