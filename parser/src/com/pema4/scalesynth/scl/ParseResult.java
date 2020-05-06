package com.pema4.scalesynth.scl;

import java.util.List;

/**
 * Represents parse results of .scl file.
 * It contains a description of .scl file and a list of ratios.
 */
public class ParseResult {
    private final String description;
    private final List<Double> ratios;

    /**
     * Constructs a new ParseResult instance.
     *
     * @param description a description of .scl file.
     * @param ratios      ratios read from file.
     */
    public ParseResult(String description, List<Double> ratios) {
        this.description = description;
        this.ratios = ratios;
    }

    /**
     * Returns the description of read file.
     *
     * @return the description.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Returns rations stored in read file.
     * Note that ratio of the first note in a scale is not in this list (an implicitly equals to 1).
     *
     * @return ratios list.
     */
    public List<Double> getRatios() {
        return ratios;
    }

    @Override
    public String toString() {
        return "ParseResult{" +
                "description='" + description + '\'' +
                ", pitches=" + ratios +
                '}';
    }
}
