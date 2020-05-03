package com.pema4.scalesynth.base.generators;

import com.pema4.scalesynth.base.Component;
import com.pema4.scalesynth.base.processors.Processor;

public interface Generator extends Component {
    void generate(double[][] outputs, int n);

    default Generator then(Processor processor) {
        return ChainedGenerator.of(this, processor);
    }
}

