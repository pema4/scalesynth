package com.scalesynth.gui.models;

import com.scalesynth.ScaleSynth;
import com.synthbot.jasiohost.*;

import java.util.HashSet;
import java.util.Set;

/**
 * Enables audio output support using ASIO.
 */
public class SynthAsioAdapter implements AsioDriverListener {
    private final ScaleSynth synth;
    private AsioDriver driver;
    private AsioChannel leftOutput;
    private AsioChannel rightOutput;
    private int bufferSize;
    private float[][] outputs;
    private double[][] doubleOutputs;

    /**
     * Creates a new ASIO wrapper for ScaleSynth.
     *
     * @param synth wrapped synthesizer.
     */
    public SynthAsioAdapter(ScaleSynth synth) {
        this.synth = synth;
    }

    /**
     * Starts the driver with specified name.
     *
     * @param driverName name of the driver.
     * @throws AsioException thrown when device cannot be opened
     */
    public void start(String driverName) throws AsioException {
        driver = AsioDriver.getDriver(driverName);
        start(driver);
    }

    /**
     * Starts that driver.
     *
     * @param driver driver to start.
     * @throws AsioException exception thrown when driver can't be started.
     */
    private void start(AsioDriver driver) throws AsioException {
        driver.addAsioDriverListener(this);
        leftOutput = driver.getChannelOutput(0);
        rightOutput = driver.getChannelOutput(1);
        bufferSize = driver.getBufferPreferredSize();
        synth.setSampleRate((float) driver.getSampleRate());
        outputs = new float[2][bufferSize];
        doubleOutputs = new double[2][bufferSize];
        driver.createBuffers(new HashSet<>(Set.of(leftOutput, rightOutput)));
        driver.start();
    }

    /**
     * Stops current driver.
     */
    public void stop() {
        if (driver != null) {
            driver.shutdownAndUnloadDriver();
            leftOutput = null;
            rightOutput = null;
            driver = null;
        }
    }

    /**
     * The sample rate has changed. This may be due to a user initiated change, or a change in input/output
     * source.
     *
     * @param sampleRate The new sample rate.
     */
    @Override
    public void sampleRateDidChange(double sampleRate) {
        synth.setSampleRate((float) sampleRate);
    }

    /**
     * The driver requests a reset in the case of an unexpected failure or a device
     * reconfiguration. As this request is being made in a callback, the driver should
     * only be reset after this callback method has returned. The recommended way to reset
     * the driver is:
     * <pre><code>
     * public void resetRequest() {
     *   new Thread() {
     *     public void run() {
     * 	     AsioDriver.getDriver().returnToState(AsioDriverState.INITIALIZED);
     *     }
     *   }.start();
     * }
     * </code></pre>
     * Because all methods are synchronized, this approach will safely return the driver
     * to the <code>INITIALIZED</code> state as soon as possible. The buffers must then be recreated
     * and the driver restarted.
     */
    @Override
    public void resetRequest() {
        new Thread(() -> {
            driver.returnToState(AsioDriverState.INITIALIZED);
            start(driver);
        }).start();
    }

    /**
     * The driver detected audio buffer underruns and requires a resynchronization.
     * This function does nothing.
     */
    @Override
    public void resyncRequest() {
        // do nothing.
    }

    /**
     * The driver has a new preferred buffer size. The host should make an effort to
     * accommodate the driver by returning to the <code>INITIALIZED</code> state and calling
     * <code>AsioDriver.createBuffers()</code>.
     *
     * @param bufferSize The new preferred buffer size.
     */
    @Override
    public void bufferSizeChanged(int bufferSize) {
        // do nothing.
    }

    /**
     * The input or output latencies have changed. The host is updated with the new values.
     *
     * @param inputLatency  The new input latency in milliseconds.
     * @param outputLatency The new output latency in milliseconds.
     */
    @Override
    public void latenciesChanged(int inputLatency, int outputLatency) {
        // do nothing
    }

    /**
     * This method is called when driver requests new audio.
     *
     * @param sampleTime     System time related to sample position, in nanoseconds.
     * @param samplePosition Sample position since <code>start()</code> was called.
     * @param activeChannels The set of channels which are active and have allocated buffers. Retrieve
     *                       the buffers with <code>AsioChannel.getBuffer()</code>, or use <code>AsioChannel.read()</code>
     *                       and <code>AsioDriver.write()</code> in order to easily work with <code>float</code> arrays.
     */
    @Override
    public void bufferSwitch(long sampleTime, long samplePosition, Set<AsioChannel> activeChannels) {
        synth.generate(doubleOutputs, bufferSize);
        for (int ch = 0; ch < doubleOutputs.length; ++ch)
            for (int i = 0; i < doubleOutputs[0].length; ++i)
                outputs[ch][i] = (float) doubleOutputs[ch][i];
        leftOutput.write(outputs[0]);
        rightOutput.write(outputs[1]);
    }
}
