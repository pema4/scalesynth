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
import javafx.scene.control.Label;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

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

    @Override
    public void start(Stage primaryStage) {
        Parent root = createUI();
        primaryStage.setTitle("ScaleSynth");
        primaryStage.setResizable(false);
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }

    private Parent createUI() {
        var pane = new BorderPane();

        pane.addEventFilter(KeyEvent.ANY, new KeyEventFilter(midiAdapter, scaleService));

        var settings = new HBox(5,
                new Label("  Settings:"),
                new SynthSerializationView(serializationService),
                new Label("  Scale:"),
                new ScaleSettingsView(scaleService),
                new Label("  Midi inputs:"),
                new MidiSettingsView(midiAdapter, new MidiService()),
                new Label("  Audio outputs:"),
                new AsioSettingsView(asioAdapter)
        );
        settings.setStyle("-fx-alignment: center-left; -fx-background-color: #e6e0dc; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.01), 4, 0.2, 0, 8)");
        settings.setPadding(new Insets(5));
        pane.setTop(settings);

        var editor = new EditorView(synth.getParameters());
        pane.setCenter(editor);

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
