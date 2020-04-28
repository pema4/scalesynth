package com.pema4.scalesynth.base.generators;

import com.pema4.scalesynth.base.KeyboardEvent;
import com.pema4.scalesynth.base.Component;
import com.pema4.scalesynth.base.processors.Processor;

import java.util.Arrays;

class ChainedGenerator implements Generator {
    private final Generator generator;
    private final Processor[] processors;

    private ChainedGenerator(Generator generator, Processor... processors) {
        this.generator = generator;
        this.processors = processors;
    }

    static ChainedGenerator of(Generator generator, Processor... processors) {
        if (!(generator instanceof ChainedGenerator))
            return new ChainedGenerator(generator, processors);

        var nestedGenerator = ((ChainedGenerator) generator).generator;
        var nestedProcessors = ((ChainedGenerator) generator).processors;
        return ChainedGenerator.of(nestedGenerator, concatArrays(nestedProcessors, processors));
    }

    private static <T> T[] concatArrays(T[] a, T[] b) {
        T[] c = Arrays.copyOf(a, a.length + b.length);
        System.arraycopy(b, 0, c, a.length, b.length);
        return c;
    }

    @Override
    public final void generate(float[][] channels, int n) {
        generator.generate(channels, n);

        // buffers used for processors
        var inputs = new float[channels.length][channels[0].length];
        var outputs = channels;

        for (int i = 0, processorsLength = processors.length; i < processorsLength; i++) {
            // swap inputs and outputs
            var temp = inputs;
            inputs = outputs;
            outputs = temp;

            processors[i].process(inputs, outputs, n);
        }

        if (outputs != channels)
            for (int i = 0; i < channels.length; ++i)
                System.arraycopy(outputs[i], 0, channels[i], 0, channels[0].length);
    }

    @Override
    public void setSampleRate(float sampleRate) {
        generator.setSampleRate(sampleRate);
        for (int i = 0, processorsLength = processors.length; i < processorsLength; i++)
            processors[i].setSampleRate(sampleRate);
    }

    @Override
    public float getSampleRate() {
        return generator.getSampleRate();
    }

    /**
     * Reports a latency of this component (in samples)
     *
     * @return a latency of this component (in samples)
     */
    @Override
    public float getLatency() {
        var processorsLatency = Arrays.stream(processors).mapToDouble(Component::getLatency).sum();
        return generator.getLatency() + (float)processorsLatency;
    }

    /**
     * Called when the user presses or releases any key.
     * Useful for retriggering envelopes and LFOs.
     *
     * @param keyboardEvent represent a type of keyboard event.
     */
    @Override
    public void onKeyboardEvent(KeyboardEvent keyboardEvent) {
        generator.onKeyboardEvent(keyboardEvent);
        for (int i = 0; i < processors.length; ++i)
            processors[i].onKeyboardEvent(keyboardEvent);
    }
}
