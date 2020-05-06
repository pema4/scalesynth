package com.pema4.scalesynth.base;

/**
 * Represents a Midi Event.
 */
public class KeyboardEvent {
    private final KeyboardEventType type;
    private final double freq;
    private final int note;
    private final int value;

    /**
     * Constructs a new KeyboardEvent instance.
     *
     * @param type  type of this event.
     * @param note  note of this event.
     * @param freq  frequency value of this event (or other floating point value).
     * @param value integer value of this event (typically it is velocity).
     */
    public KeyboardEvent(KeyboardEventType type, int note, double freq, int value) {
        this.type = type;
        this.note = note;
        this.freq = freq;
        this.value = value;
    }

    /**
     * A factory method for the Note On messages.
     *
     * @param note  note that is on.
     * @param freq  frequency of that note.
     * @param value velocity of that note.
     * @return a corresponding KeyboardEvent.
     */
    public static KeyboardEvent noteOn(int note, double freq, int value) {
        return new KeyboardEvent(KeyboardEventType.NOTE_ON, note, freq, value);
    }

    /**
     * A factory method for the Note Off messages.
     *
     * @param note note that is off.
     * @return a corresponding KeyboardEvent.
     */
    public static KeyboardEvent noteOff(int note) {
        return new KeyboardEvent(KeyboardEventType.NOTE_OFF, note, -1, -1);
    }

    /**
     * A factory method for the Pitch Bend messages.
     *
     * @param lsb least significant byte of the data (data[1])
     * @param msb most significant byte of the data (data[2])
     * @return a corresponding KeyboardEvent.
     */
    public static KeyboardEvent pitchBend(byte lsb, byte msb) {
        double bendAmount = (msb * 128) + lsb;
        var semitones = 4 * bendAmount / (1 << 14) - 2; // +-2 range
        var coefficient = Math.pow(2, semitones / 12);
        return new KeyboardEvent(KeyboardEventType.PITCH_BEND, -1, coefficient, -1);
    }

    /**
     * Returns the type of this event.
     *
     * @return the type of this event.
     */
    public KeyboardEventType getType() {
        return type;
    }

    /**
     * Returns the frequency (or another double value) of this event.
     *
     * @return the frequency of this event.
     */
    public double getFreq() {
        return freq;
    }

    /**
     * Returns the value (typically it is velocity) of this event.
     *
     * @return the value of this event.
     */
    public int getValue() {
        return value;
    }

    /**
     * Returns the note of this event (or -1)
     *
     * @return the note of this event.
     */
    public int getNote() {
        return note;
    }
}
