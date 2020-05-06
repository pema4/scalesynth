module scalesynth.gui {
    requires JAsioHost;
    requires javafx.graphics;
    requires javafx.fxml;
    requires javafx.controls;
    requires java.desktop;
    requires dsp;
    requires scalesynth.scl;

    exports com.pema4.scalesynth.gui;
    opens com.pema4.scalesynth.gui.views to javafx.fxml;
}