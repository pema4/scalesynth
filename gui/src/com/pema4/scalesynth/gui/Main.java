package com.pema4.scalesynth.gui;

import com.pema4.scalesynth.ScaleSynth;
import com.pema4.scalesynth.gui.models.KeyEventFilter;
import com.pema4.scalesynth.gui.models.SynthAsioAdapter;
import com.pema4.scalesynth.gui.models.SynthMidiAdapter;
import com.pema4.scalesynth.gui.services.MidiService;
import com.pema4.scalesynth.gui.services.ScaleService;
import com.pema4.scalesynth.gui.services.SynthSerializationService;
import com.pema4.scalesynth.gui.views.*;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

/**
 * Entry point for the program.
 */
public class Main extends Application {
    private final ScaleSynth synth = new ScaleSynth();
    private final ScaleService scaleService = new ScaleService();
    private final SynthMidiAdapter midiAdapter = new SynthMidiAdapter(synth, scaleService);
    private final SynthAsioAdapter asioAdapter = new SynthAsioAdapter(synth);
    private final SynthSerializationService serializationService = new SynthSerializationService(synth.getParameters());

    public static void main(String[] args) {
        System.setProperty("java.library.path", "C:\\javalibs\\jasiohost\\lib");
        launch(args);
    }

    /**
     * Starts an application.
     *
     * @param primaryStage primary stage for our scene.
     */
    @Override
    public void start(Stage primaryStage) {
        Parent root = createUI();
        primaryStage.setTitle("ScaleSynth");
        primaryStage.setScene(new Scene(root));
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    /**
     * Composes and returns program UI.
     *
     * @return a node representing program UI.
     */
    private Parent createUI() {
        var pane = new BorderPane();

        pane.addEventFilter(KeyEvent.ANY, new KeyEventFilter(midiAdapter));

        var settings = new HBox(15,
                new SynthSerializationView(serializationService),
                new ScaleSettingsView(scaleService),
                new MidiSettingsView(midiAdapter, new MidiService()),
                new AsioSettingsView(asioAdapter));
        settings.setStyle("-fx-background-color: #e6e0dc; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.01), 4, 0.2, 0, 8);" +
                "-fx-alignment: center; -fx-padding: 5px");
        pane.setTop(settings);

        var editor = new EditorView(synth.getParameters());
        pane.setCenter(editor);
        BorderPane.setAlignment(editor, Pos.TOP_CENTER);

        var keyboard = new KeyboardView(midiAdapter);
        pane.setBottom(keyboard);
        BorderPane.setAlignment(keyboard, Pos.BOTTOM_CENTER);

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
