package com.pema4.scalesynth.base.generators;

import com.pema4.scalesynth.base.KeyboardEvent;
import com.pema4.scalesynth.base.KeyboardEventType;

import java.util.*;
import java.util.function.Supplier;

public class PolyGenerator implements Generator {
    private final int maxPolyphony;
    //private final Parameter<Boolean> retrigger = Parameter.booleanParameter("shouldRetrigger");
    private final Supplier<Generator> voiceSupplier;
    private final LinkedHashMap<Integer, Generator> noteMapping = new LinkedHashMap<>();
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
    public void generate(float[][] outputs, int n) {
        int channelCount = outputs.length;

        for (int ch = 0; ch < channelCount; ++ch)
            Arrays.fill(outputs[ch], 0f);

        float[][] voiceResult = new float[channelCount][n];
        for (Generator voice : noteMapping.values()) {
            voice.generate(voiceResult, n);
            for (int ch = 0; ch < channelCount; ++ch)
                for (int i = 0; i < n; ++i)
                    outputs[ch][i] += voiceResult[ch][i];
        }
    }

    /**
     * Sets a sample rate used in processing.
     *
     * @param sampleRate new sample rate.
     */
    @Override
    public void setSampleRate(float sampleRate) {
        for (Generator generator : voices)
            generator.setSampleRate(sampleRate);

    }

    /**
     * Returns a sample rate used in processing.
     *
     * @return a sample rate used in processing.
     */
    @Override
    public float getSampleRate() {
        return voices[0].getSampleRate();
    }

    /**
     * Reports a latency of this component (in samples).
     *
     * Actually it returns a latency of the first created voice (every voice has the same latency)
     *
     * @return a latency of this component (in samples)
     */
    @Override
    public float getLatency() {
        return voices[0].getLatency();
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
    public void onKeyboardEvent(KeyboardEvent event) {
        int note = event.getNote();
        KeyboardEventType type = event.getType();

        switch (type) {
            case NOTE_OFF:
                if (noteMapping.containsKey(note))
                    noteMapping.get(note).onKeyboardEvent(event);
                break;
            case NOTE_ON:
                if (freeVoices.size() != 0) {
                    var freeVoice = freeVoices.iterator().next();
                    freeVoices.remove(freeVoice);
                    noteMapping.put(note, freeVoice);
                    freeVoice.onKeyboardEvent(event);
                } else if (noteMapping.containsKey(note)) {
                    var generator = noteMapping.get(note);
                    noteMapping.remove(note);
                    noteMapping.put(note, generator);
                    generator.onKeyboardEvent(event);
                } else {
                    var arr = noteMapping.entrySet().toArray();
                    //var oldestVoiceEntry = (Map.Entry<Integer, Generator>)arr[0];
                    var oldestVoiceEntry = noteMapping.entrySet().iterator().next();
                    var oldestNote = oldestVoiceEntry.getKey();
                    var oldestGenerator = oldestVoiceEntry.getValue();
                    noteMapping.remove(oldestNote);
                    noteMapping.put(note, oldestGenerator);
                    oldestGenerator.onKeyboardEvent(event);
                }
                break;
        }
    }
}
