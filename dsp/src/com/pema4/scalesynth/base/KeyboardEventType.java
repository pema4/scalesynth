package com.pema4.scalesynth.base;

/**
 * Represents a KeyboardEvent type.
 */
public enum KeyboardEventType {
    /**
     * Event caused by key press.
     * In this case note is the number of pressed note, freq - frequency of pressed note, value - velocity.
     */
    NOTE_ON,

    /**
     * Event caused by key release.
     * In this case note is the number of released note.
     */
    NOTE_OFF,

    /**
     * Event caused by pitch bend.
     * In this case freq is a frequency coefficient (from -1 semitone to 1 semitone)
     */
    PITCH_BEND
}
