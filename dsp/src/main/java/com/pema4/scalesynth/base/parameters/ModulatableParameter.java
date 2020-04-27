package com.pema4.scalesynth.base.parameters;

import java.util.List;

public interface ModulatableParameter extends Parameter {
    List<Modulation> getModulations();
}
