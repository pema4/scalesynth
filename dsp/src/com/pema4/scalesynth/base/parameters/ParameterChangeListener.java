package com.pema4.scalesynth.base.parameters;

/**
 * Functional interface, implemented by listeners of {@code Parameter} instance.
 *
 * @param <T> value type of {@code Parameter}.
 */
public interface ParameterChangeListener<T> {
    void valueChanged(T value);
}
