package com.pema4.scalesynth.gui.controls;

public interface ParameterTransform<T> {
    double toPosition(T parameter);
    T toParameter(double position);
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
