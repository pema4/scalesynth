package com.pema4.scalesynth.base.processors;

import com.pema4.scalesynth.base.Component;

public interface Processor extends Component {
    void process(double[][] inputs, int n);
}
