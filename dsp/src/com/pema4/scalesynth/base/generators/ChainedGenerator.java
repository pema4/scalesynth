package com.pema4.scalesynth.base.generators;

import com.pema4.scalesynth.base.KeyboardEvent;
import com.pema4.scalesynth.base.processors.Processor;

import java.util.Arrays;

/**
 * Represents a chained generator (it is returned by method "then" of {@link Generator}).
 */
class ChainedGenerator implements Generator {
    private final Generator generator;
    private final Processor[] processors;

    /**
     * Constructs a new ChainedGenerator instance.
     *
     * @param generator  original generator
     * @param processors a processors to be used after generator (in same order).
     */
    private ChainedGenerator(Generator generator, Processor... processors) {
        this.generator = generator;
        this.processors = processors;
    }

    /**
     * Returns an instance of generator class.
     * If generator is already instance of {@link ChainedGenerator}, it is modified and returned.
     *
     * @param generator  original generator.
     * @param processors a processors to be used after generator (in same order).
     * @return a ChainedGenerator instance.
     */
    static ChainedGenerator of(Generator generator, Processor... processors) {
        if (!(generator instanceof ChainedGenerator))
            return new ChainedGenerator(generator, processors);

        var nestedGenerator = ((ChainedGenerator) generator).generator;
        var nestedProcessors = ((ChainedGenerator) generator).processors;
        return ChainedGenerator.of(nestedGenerator, concatArrays(nestedProcessors, processors));
    }

    /**
     * Helper function to concatenate first array with second array.
     *
     * @param a   first array
     * @param b   second array.
     * @param <T> type of the array.
     * @return a new array, containing all elements from the first and the second.
     */
    private static <T> T[] concatArrays(T[] a, T[] b) {
        T[] c = Arrays.copyOf(a, a.length + b.length);
        System.arraycopy(b, 0, c, a.length, b.length);
        return c;
    }

    /**
     * Generates of the audio.
     * Note that content of {@code outputs} can be overwritten (this behaviour depends on a subclass).
     *
     * @param outputs buffers to place generated audio into.
     * @param n       how many samples to generate.
     */
    @Override
    public final void generate(double[][] outputs, int n) {
        generator.generate(outputs, n);

        for (var processor : processors) {
            processor.process(outputs, n);
        }
    }

    @Override
    public void setSampleRate(double sampleRate) {
        generator.setSampleRate(sampleRate);
        for (var processor : processors)
            processor.setSampleRate(sampleRate);
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
        for (var processor : processors)
            processor.handleKeyboardEvent(keyboardEvent);
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
