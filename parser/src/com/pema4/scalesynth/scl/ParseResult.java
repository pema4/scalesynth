package com.pema4.scalesynth.scl;

import java.util.List;

public class ParseResult {
    public ParseResult(String description, List<Double> ratios) {
        this.description = description;
        this.ratios = ratios;
    }

    private final String description;

    public String getDescription() {
        return description;
    }

    public List<Double> getRatios() {
        return ratios;
    }

    private final List<Double> ratios;

    @Override
    public String toString() {
        return "ParseResult{" +
                "description='" + description + '\'' +
                ", pitches=" + ratios +
                '}';
    }
}
