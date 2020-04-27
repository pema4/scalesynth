package com.pema4.scalesynth.gui.services;

import javax.sound.midi.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Service class containing some utils for working with MIDI
 */
public class MidiService {
    /**
     * Returns information about MIDI IN ports.
     * @return list containing information about MIDI IN ports.
     */
    public List<MidiDevice.Info> getMidiInputInfos() {
        List<MidiDevice.Info> infos = new ArrayList<>();
        for (var info : MidiSystem.getMidiDeviceInfo())
            try {
                var device = MidiSystem.getMidiDevice(info);
                if (!(device instanceof Sequencer) && !(device instanceof Synthesizer) && device.getMaxTransmitters() != 0)
                    infos.add(info);
            } catch (MidiUnavailableException ignored) {
                // ignored
            }
        return infos;
    }

    /**
     * Closes specified midi device if it exist and it is opened.
     * @param info information about midi device
     */
    public void close(MidiDevice.Info info) {
        if (info == null)
            return;

        try {
            MidiSystem.getMidiDevice(info).close();
        } catch (MidiUnavailableException ignored) {
        }
    }

    public Transmitter open(MidiDevice.Info info) throws MidiUnavailableException, NullPointerException {
        var device = MidiSystem.getMidiDevice(info);
        device.open();
        return device.getTransmitter();
    }
}
