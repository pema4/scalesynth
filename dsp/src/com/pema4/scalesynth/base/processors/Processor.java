package com.pema4.scalesynth.base.processors;

import com.pema4.scalesynth.base.Component;

/**
 * Represent inplace transformation of audio signal.
 */
public interface Processor extends Component {
    /**
     * Transforms incoming audio.
     * Note that content of {@code inputs} will be overwritten.
     *
     * @param inputs buffers to place generated audio into.
     * @param n      how many samples to generate.
     */
    void process(double[][] inputs, int n);
}
