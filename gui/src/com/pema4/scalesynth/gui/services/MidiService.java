package com.pema4.scalesynth.gui.services;

import javax.sound.midi.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service class containing some utils for working with MIDI
 */
public class MidiService {
    private MidiDevice lastOpenDevice;

    /**
     * Returns information about MIDI IN ports.
     *
     * @return list containing information about MIDI IN ports.
     */
    public List<String> getMidiInputs() {
        return getMidiInputInfo().stream().map(MidiDevice.Info::getName).collect(Collectors.toList());
    }

    /**
     * Returns a list of MIDI IN ports.
     *
     * @return a list of MIDI IN ports.
     */
    private List<MidiDevice.Info> getMidiInputInfo() {
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
     *
     * @param name name of the device midi device
     */
    public void close(String name) {
        if (name == null)
            return;

        try {
            var info = getMidiInputInfo();
            var requested = info.stream().filter(x -> x.getName().equals(name)).findFirst();
            if (requested.isPresent())
                MidiSystem.getMidiDevice(requested.get()).close();
        } catch (MidiUnavailableException ignored) {
        }
    }

    /**
     * Opens a MidiDevice with that name.
     *
     * @param name MidiDevice name.
     * @return opened MidiDevice.
     * @throws MidiUnavailableException thrown when MidiDevice is unavailable.
     */
    public Transmitter open(String name) throws MidiUnavailableException {
        var info = getMidiInputInfo();
        var requested = info.stream().filter(x -> x.getName().equals(name)).findFirst();
        if (requested.isEmpty())
            throw new IllegalArgumentException(String.format("Can't close device with name %s", name));

        var device = MidiSystem.getMidiDevice(requested.get());
        device.open();
        return device.getTransmitter();
    }

    /**
     * Closes last open MidiDevice.
     */
    public void close() {
        if (lastOpenDevice != null && lastOpenDevice.isOpen())
            lastOpenDevice.close();
    }
}
