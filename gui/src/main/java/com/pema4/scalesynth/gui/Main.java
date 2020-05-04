package com.pema4.scalesynth.gui;

import com.pema4.scalesynth.ScaleSynth;
import com.pema4.scalesynth.gui.models.KeyEventFilter;
import com.pema4.scalesynth.gui.models.SynthAsioAdapter;
import com.pema4.scalesynth.gui.models.SynthMidiAdapter;
import com.pema4.scalesynth.gui.services.ScaleService;
import com.pema4.scalesynth.gui.services.MidiService;
import com.pema4.scalesynth.gui.views.*;
import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class Main extends Application {
    private final ScaleSynth synth = new ScaleSynth();
    private final ScaleService scaleService = new ScaleService();
    private final SynthMidiAdapter midiAdapter = new SynthMidiAdapter(synth, scaleService);
    private final SynthAsioAdapter asioAdapter = new SynthAsioAdapter(synth);

    public static void main(String[] args) {
        System.setProperty("java.library.path", "C:\\javalibs\\jasiohost\\lib");
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        Parent root = createUI();
        primaryStage.setTitle("Hello World");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }

    private Parent createUI() {
        var pane = new BorderPane();

        var settings = new VBox(5,
                new MidiSettingsView(midiAdapter, new MidiService()),
                new AsioSettingsView(asioAdapter),
                new ScaleSettingsView(scaleService)
        );
        pane.setRight(settings);
        pane.setCenter(new EditorView(synth.getParameters()));
        pane.setBottom(new KeyboardView(midiAdapter));

        pane.addEventFilter(KeyEvent.ANY, new KeyEventFilter(midiAdapter, scaleService));
        return pane;
    }

    /**
     * This method is called when the application should stop, and provides a
     * convenient place to prepare for application exit and destroy resources.
     *
     * <p>
     * The implementation of this method provided by the Application class does nothing.
     * </p>
     *
     * <p>
     * NOTE: This method is called on the JavaFX Application Thread.
     * </p>
     */
    @Override
    public void stop() {
        asioAdapter.stop();
        midiAdapter.close();
    }
}
