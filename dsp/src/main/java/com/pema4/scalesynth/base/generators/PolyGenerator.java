package com.pema4.scalesynth.base.generators;

import com.pema4.scalesynth.base.KeyboardEvent;
import com.pema4.scalesynth.base.KeyboardEventType;

import java.util.*;
import java.util.function.Supplier;

public class PolyGenerator implements Generator {
    private final int maxPolyphony;
    //private final Parameter<Boolean> retrigger = Parameter.booleanParameter("shouldRetrigger");
    private final Supplier<Generator> voiceSupplier;
    private final Map<Integer, Generator> noteMapping = new HashMap<>();
    private final Map<Generator, Integer> activeVoices = new LinkedHashMap<>();
    private final Set<Generator> freeVoices = new HashSet<>();
    private final Generator[] voices;

    public PolyGenerator(int maxPolyphony, Supplier<Generator> voiceSupplier) {
        this.voiceSupplier = voiceSupplier;
        this.maxPolyphony = maxPolyphony;

        voices = new Generator[maxPolyphony];
        for (int i = 0; i < maxPolyphony; ++i) {
            var newVoice = voiceSupplier.get();
            voices[i] = newVoice;
            freeVoices.add(newVoice);
        }
    }

    @Override
    public synchronized void generate(double[][] outputs, int n) {
        int channelCount = outputs.length;

        for (double[] output : outputs)
            Arrays.fill(output, 0.0);

        double[][] voiceResult = new double[channelCount][n];
        var iterator = activeVoices.keySet().iterator();
        while (iterator.hasNext()) {
            var voice = iterator.next();
            voice.generate(voiceResult, n);
            for (int ch = 0; ch < channelCount; ++ch)
                for (int i = 0; i < n; ++i)
                    outputs[ch][i] += voiceResult[ch][i];
            if (!voice.isActive()) {
                iterator.remove();
                freeVoices.add(voice);
            }
        }
    }

    /**
     * Sets a sample rate used in processing.
     *
     * @param sampleRate new sample rate.
     */
    @Override
    public void setSampleRate(double sampleRate) {
        for (Generator generator : voices)
            generator.setSampleRate(sampleRate);

    }

    /**
     * Called when the user presses or releases any key.
     * Useful for retriggering envelopes and LFOs.
     *
     * In this implementation new note is assigned to any free voice.
     * If there is no free voice, the oldest voice is used.
     *
     * @param event represent a type of keyboard event.
     */
    @Override
    public synchronized void handleKeyboardEvent(KeyboardEvent event) {
        int note = event.getNote();
        KeyboardEventType type = event.getType();

        switch (type) {
            case NOTE_OFF:
                for (var entry : activeVoices.entrySet()) {
                    if (entry.getValue() == note)
                        entry.getKey().handleKeyboardEvent(event);
                }
                break;
            case NOTE_ON:
                if (freeVoices.size() != 0) {
                    var freeVoice = freeVoices.iterator().next();
                    freeVoices.remove(freeVoice);
                    activeVoices.put(freeVoice, note);
                    freeVoice.handleKeyboardEvent(event);
                } else {
                    var oldestGenerator = activeVoices.keySet().iterator().next();
                    activeVoices.remove(oldestGenerator);
                    activeVoices.put(oldestGenerator, note);
                    oldestGenerator.handleKeyboardEvent(event);
                }
                break;
            case PITCH_BEND:
                for (var voice : voices)
                    voice.handleKeyboardEvent(event);
                break;
        }
    }

    /**
     * Returns true if component is active (does something useful).
     * This is used mainly to shutdown inactive voices.
     *
     * @return true if component is active, otherwise false.
     */
    @Override
    public boolean isActive() {
        return noteMapping.size() == 0;
    }
}
