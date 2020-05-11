package com.scalesynth.base.parameters;

/**
 * Represents a parameter wrapper of some bounded numeric value.
 *
 * @param <T> type of the value.
 */
public class NumericParameter<T extends Number> extends Parameter<T> {
    private final T min;
    private final T max;
    private final String unit;

    public NumericParameter(String name, T defaultValue, T min, T max, String unit) {
        super(name, defaultValue);
        this.min = min;
        this.max = max;
        this.unit = unit;
    }

    /**
     * Returns an unit name of this parameter (i.e. "octaves", "hz").
     *
     * @return an unit name of this parameter.
     */
    public String getUnit() {
        return unit;
    }

    /**
     * Returns minimum value of this parameter.
     *
     * @return minimum value of this parameter.
     */
    public T getMin() {
        return min;
    }

    /**
     * Return maximum value of this parameter.
     *
     * @return maximum value of this parameter.
     */
    public T getMax() {
        return max;
    }
}
