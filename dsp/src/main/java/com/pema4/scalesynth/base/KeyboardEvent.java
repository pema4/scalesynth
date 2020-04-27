package com.pema4.scalesynth.base;

public class KeyboardEvent {
    private final KeyboardEventType type;
    private final int note;
    private final int velocity;

    public KeyboardEvent(KeyboardEventType type, int note, int velocity) {
        this.type = type;
        this.note = note;
        this.velocity = velocity;
    }

    public static KeyboardEvent noteOn(int note, int velocity) {
        return new KeyboardEvent(KeyboardEventType.NOTE_ON, note, velocity);
    }

    public static KeyboardEvent noteOff(int note, int velocity) {
        return new KeyboardEvent(KeyboardEventType.NOTE_OFF, note, velocity);
    }

    public KeyboardEventType getType() {
        return type;
    }

    public int getNote() {
        return note;
    }

    public int getVelocity() {
        return velocity;
    }
}
