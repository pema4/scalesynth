package com.pema4.scalesynth.gui.services;

import com.pema4.scalesynth.scl.ScaleParser;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service with various tools for handling custom scales.
 */
public class ScaleService {
    private List<Double> ratios;
    private boolean enabled;
    private int octaveSize;
    private double octaveMultiplier;

    /**
     * Reads a scale in .scl format from {@code input} and starts using it.
     *
     * @param input an input stream with text in .scl format.
     */
    public void enable(InputStream input) {
        var parser = new ScaleParser();
        var result = parser.parse(input);

        octaveSize = result.getRatios().size();

        // it is easier to have ratios stored in this format.
        ratios = new ArrayList<>();
        ratios.add(1.0);
        for (int i = 0; i < octaveSize - 1; ++i)
            ratios.add(result.getRatios().get(i));

        octaveMultiplier = result.getRatios().get(octaveSize - 1);
        enabled = true;
    }

    /**
     * Convert given MIDI note numbers to frequency based on selected scale.
     *
     * @param note a MIDI note number.
     * @return a frequency of the note.
     */
    public double getFreq(int note) {
        if (enabled) {
            var baseFreq = 440 * Math.pow(octaveMultiplier, (60 - 69) / 12.0); // base frequency
            var wholeOctaves = Math.floorDiv((note - 60), octaveSize);
            var noteNum = Math.floorMod((note - 60), octaveSize);
            return baseFreq * Math.pow(2, wholeOctaves) * ratios.get(noteNum);
        } else {
            return 440 * Math.pow(2, (note - 69) / 12.0);
        }
    }

    /**
     * Disables current custom sample (and returns to normal 12 equal tempered).
     */
    public void disable() {
        this.enabled = false;
    }
}
