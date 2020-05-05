package com.pema4.scalesynth.gui.controls;

public interface ParameterTransform<T> {
    double toPosition(T parameter);
    T toParameter(double position);
}
