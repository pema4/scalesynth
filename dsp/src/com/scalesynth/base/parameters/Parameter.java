package com.scalesynth.base.parameters;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Represents a parameter wrapper.
 * <p>
 * Parameters are exposed settings of synthesis.
 * They can be modified from UI.
 */
public class Parameter<T> {
    private final String name;
    private final List<ParameterChangeListener<T>> listeners = new ArrayList<>();
    private final T defaultValue;
    private T value;

    public Parameter(String name, T defaultValue) {
        this.name = name;
        this.defaultValue = defaultValue;
    }

    /**
     * Name of the parameter.
     *
     * @return name of this parameter.
     */
    public String getName() {
        return name;
    }

    /**
     * Derived classes should override this method to perform any checks in the setter.
     *
     * @param value value to be checked.
     * @return {@code true} if value is correct, otherwise {@code false}.
     */
    protected boolean checkValue(T value) {
        return true;
    }

    /**
     * Returns the value of this parameter.
     *
     * @return the value of this parameter.
     */
    public T getValue() {
        return value;
    }

    /**
     * Sets a new value of this parameter. Also notifies all listeners if value changed
     *
     * @param value value to be set.
     */
    public void setValue(T value) {
        if (checkValue(value) && !Objects.equals(value, this.value)) {
            this.value = value;
            for (var listener : listeners)
                listener.valueChanged(value);
        }
    }

    /**
     * Attaches change listener to this parameter.
     *
     * @param listener listener to be attached.
     */
    public void addListener(ParameterChangeListener<T> listener) {
        listeners.add(listener);
    }

    /**
     * Detaches change listener from this parameter (if it was attached).
     *
     * @param listener listener to be detached.
     */
    public void removeListener(ParameterChangeListener<T> listener) {
        listeners.remove(listener);
    }

    /**
     * Returns a default value.
     *
     * @return a default value.
     */
    public T getDefault() {
        return defaultValue;
    }

    /**
     * Removes all listeners.
     */
    public void close() {
        listeners.clear();
    }
}
