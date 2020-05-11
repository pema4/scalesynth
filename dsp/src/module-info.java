module scalesynth.dsp {
    exports com.scalesynth;
    exports com.scalesynth.base;
    exports com.scalesynth.base.parameters;

    // for serialization
    opens com.scalesynth to scalesynth.gui;
}