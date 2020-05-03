package com.pema4.scalesynth.base;

public class KeyboardEvent {
    private final KeyboardEventType type;
    private final double freq;
    private final int note;
    private final int velocity;

    public KeyboardEvent(KeyboardEventType type, int note, double freq, int velocity) {
        this.type = type;
        this.note = note;
        this.freq = freq;
        this.velocity = velocity;
    }

    public static KeyboardEvent noteOn(int note, double freq, int velocity) {
        return new KeyboardEvent(KeyboardEventType.NOTE_ON, note, freq, velocity);
    }

    public static KeyboardEvent noteOff(int note, double freq, int velocity) {
        return new KeyboardEvent(KeyboardEventType.NOTE_OFF, note, freq, velocity);
    }

    public KeyboardEventType getType() {
        return type;
    }

    public double getFreq() {
        return freq;
    }

    public int getVelocity() {
        return velocity;
    }

    public int getNote() {
        return note;
    }
}
