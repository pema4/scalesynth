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
    public final void generate(double[][] outputs, int n) {
        generator.generate(outputs, n);

        for (int i = 0, processorsLength = processors.length; i < processorsLength; i++) {
            processors[i].process(outputs, n);
        }
    }

    @Override
    public void setSampleRate(double sampleRate) {
        generator.setSampleRate(sampleRate);
        for (int i = 0, processorsLength = processors.length; i < processorsLength; i++)
            processors[i].setSampleRate(sampleRate);
    }

    /**
     * Called when the user presses or releases any key.
     * Useful for retriggering envelopes and LFOs.
     *
     * @param keyboardEvent represent a type of keyboard event.
     */
    @Override
    public void handleKeyboardEvent(KeyboardEvent keyboardEvent) {
        generator.handleKeyboardEvent(keyboardEvent);
        for (int i = 0; i < processors.length; ++i)
            processors[i].handleKeyboardEvent(keyboardEvent);
    }

    /**
     * Returns true if component is active (does something useful).
     * This is used mainly to shutdown inactive voices.
     *
     * @return true if component is active, otherwise false.
     */
    @Override
    public boolean isActive() {
        return processors[processors.length - 1].isActive();
    }
}
