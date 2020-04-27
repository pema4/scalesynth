package com.pema4.scalesynth.base.parameters;

import com.pema4.scalesynth.base.generators.Generator;

/**
 * Parameters are used to control synthesis.
 * Also they can be saved to file and restored later.
 */
public interface Parameter<T> extends Generator {

    /**
     * Name of the parameter. Must be unique.
     * @return identifier of this parameter.
     */
    String getId();

    /**
     * Sets the value of this parameter.
     * @param newValue new value of this parameter.
     */
    void setValue(T newValue);

    /**
     * Returns the value of this parameter.
     * @return the value of this parameter.
     */
    T getValue();

    public static Parameter<Float> floatParameter(String id) {
        return new ParameterImpl<Float>(id);
    }

    public static Parameter<Integer> integerParameter(String id) {
        return new ParameterImpl<Integer>(id);
    }

    public static Parameter<Boolean> booleanParameter(String id) {
        return new ParameterImpl<Boolean>(id);
    }
}
