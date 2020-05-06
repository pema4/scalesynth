package com.pema4.scalesynth.gui.services;

import com.pema4.scalesynth.scl.ScaleParser;

import java.io.InputStream;
import java.util.List;

public class ScaleService {
    private List<Double> ratios;
    private boolean enabled;
    private int octaveSize;

    public void enable(InputStream input) {
        var parser = new ScaleParser();
        var result = parser.parse(input);
        octaveSize = result.getRatios().size();
        ratios = result.getRatios();
        enabled = true;
    }

    public double getFreq(int note) {
        if (enabled) {
            var baseFreq = 440 * Math.pow(2, (60 - 69) / 12.0); // base frequency
            var wholeOctaves = Math.floorDiv((note - 60), octaveSize);
            var noteNum = Math.floorMod((note - 60), octaveSize);
            return baseFreq * Math.pow(2, wholeOctaves) * ratios.get(noteNum);
        } else {
            return 440 * Math.pow(2, (note - 69) / 12.0);
        }
    }

    public void disable() {
        this.enabled = false;
    }


    public int getOctaveNotes() {
        return enabled ? ratios.size() : 12;
    }
}
