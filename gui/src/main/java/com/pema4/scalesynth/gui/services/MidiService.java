package com.pema4.scalesynth.gui.services;

import javax.sound.midi.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Service class containing some utils for working with MIDI
 */
public class MidiService {
    /**
     * Returns information about MIDI IN ports.
     *
     * @return list containing information about MIDI IN ports.
     */
    public List<String> getMidiInputs() {
        List<String> infos = new ArrayList<>();
        for (var info : MidiSystem.getMidiDeviceInfo())
            try {
                var device = MidiSystem.getMidiDevice(info);
                if (!(device instanceof Sequencer) && !(device instanceof Synthesizer) && device.getMaxTransmitters() != 0)
                    infos.add(info.getName());
            } catch (MidiUnavailableException ignored) {
                // ignored
            }
        return infos;
    }

    /**
     * Closes specified midi device if it exist and it is opened.
     *
     * @param name name of the device midi device
     */
    public void close(String name) {
        if (name == null)
            return;

        try {
            var infos = MidiSystem.getMidiDeviceInfo();
            var info = Arrays.stream(infos).filter(x -> x.getName().equals(name)).findFirst();
            if (info.isPresent())
                MidiSystem.getMidiDevice(info.get()).close();
        } catch (MidiUnavailableException ignored) {
        }
    }

    @SuppressWarnings("uncheck")
    public Transmitter open(String name) throws MidiUnavailableException, NullPointerException {
        var infos = MidiSystem.getMidiDeviceInfo();
        var info = Arrays.stream(infos).filter(x -> x.getName().equals(name)).findFirst();
        if (info.isEmpty())
            throw new IllegalArgumentException(String.format("Can't close device with name %s", name));

        var device = MidiSystem.getMidiDevice(info.get());
        device.open();
        return device.getTransmitter();
    }
}
