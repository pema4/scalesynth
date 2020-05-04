module dsp {
    exports com.pema4.scalesynth;
    exports com.pema4.scalesynth.base;
    exports com.pema4.scalesynth.base.parameters;

    opens com.pema4.scalesynth to scalesynth.gui;
}