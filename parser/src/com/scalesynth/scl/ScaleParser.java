package com.scalesynth.scl;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Pattern;

/**
 * Represent a parser for .scl (Scala) tuning files
 *
 * This format is an easy way to define custom tuning.
 */
public class ScaleParser {
    private enum State {
        DESCRIPTION,
        PITCHES_AMOUNT,
        PITCHES
    }

    /**
     * Regex used to parse a ratio.
     */
    private static final Pattern PITCH_PATTERN = Pattern.compile("\\s*(\\d+)\\s*([./]?)\\s*(\\d*).*");

    /**
     * Parses .scl file stored in InputStream.
     * This parser works line finite state machine.
     *
     * @param input input stream with .scl format text.
     * @return an instance of ParseResult with parsed information.
     */
    public ParseResult parse(InputStream input) {
        var scanner = new Scanner(input);
        var state = State.DESCRIPTION;
        String description = "";
        List<Double> pitches = new ArrayList<>();

        while (scanner.hasNext()) {
            switch (state) {
                case DESCRIPTION:
                    if (scanner.hasNextLine()) {
                        var line = scanner.nextLine();
                        if (!line.startsWith("!")) {
                            description = line;
                            state = State.PITCHES_AMOUNT;
                        }
                    }
                    break;

                case PITCHES_AMOUNT:
                    if (scanner.hasNextInt()) {
                        scanner.nextInt();
                        scanner.nextLine();
                        state = State.PITCHES;
                    } else {
                        var line = scanner.nextLine();
                        if (line.startsWith("!"))
                            break;

                        var message = String.format("Expected number of notes, found: \"%s\"", line);
                        throw new IllegalArgumentException(message);
                    }
                    break;

                case PITCHES:
                    if (scanner.hasNextLine()) {
                        var line = scanner.nextLine();
                        if (line.startsWith("!"))
                            break;

                        var matcher = PITCH_PATTERN.matcher(line);
                        if (matcher.matches()) {
                            var left = matcher.group(1);
                            var delimiter = matcher.group(2);
                            var right = matcher.group(3);
                            if (delimiter.equals("/") || delimiter.isEmpty()) {
                                var numerator = Double.parseDouble(left);
                                var denominator = delimiter.isEmpty() || right.isEmpty() ? 1 : Double.parseDouble(right);
                                pitches.add(numerator / denominator);
                            } else {
                                var cents = Double.parseDouble(left + "." + right);
                                pitches.add(1 + cents / 1200);
                            }

                        } else {
                            var message = String.format("Expected pitch definition, found: \"%s\"", line);
                            throw new IllegalArgumentException(message);
                        }
                    }
                    break;
            }
        }

        return new ParseResult(description, pitches);
    }
}
