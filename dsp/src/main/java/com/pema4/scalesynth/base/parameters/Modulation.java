package com.pema4.scalesynth.base.parameters;

public interface Modulation<T> {
    float getAmount();
    void setAmount(float value);
    boolean getBypass();
    void setBypass(boolean bypass);
}
