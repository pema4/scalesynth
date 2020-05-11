package com.scalesynth.gui.controls;

import com.scalesynth.base.parameters.Parameter;
import com.scalesynth.base.parameters.ParameterChangeListener;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Rectangle;

/**
 * Represent a toggle button controlling Boolean Parameter
 */
public class OnOffButton extends Parent implements ParameterChangeListener<Boolean> {
    private final Color accentColor;
    private final Parameter<Boolean> parameter;
    private final BooleanProperty enabled = new SimpleBooleanProperty();

    /**
     * Constructs a new instance of OnOffButton linked to that parameter and colored with accent color.
     *
     * @param parameter   a parameter to be wrapped.
     * @param accentColor an accent color to use.
     */
    public OnOffButton(Parameter<Boolean> parameter, Color accentColor) {
        this.parameter = parameter;
        this.accentColor = accentColor;

        var ui = createUI();
        getChildren().add(ui);
        enabled.addListener((observable, oldValue, newValue) -> parameter.setValue(newValue));

        parameter.addListener(this);
        parameter.setValue(parameter.getDefault());
    }

    /**
     * Creates UI of the button.
     *
     * @return button's UI.
     */
    private Node createUI() {
        var button = new Rectangle(50, 50);
        button.setStyle("-fx-stroke-width: 2; -fx-arc-height: 8px; -fx-arc-width: 8px; -fx-stroke-type: inside;");
        button.setStroke(new LinearGradient(0, 0, 1, 1, true, CycleMethod.NO_CYCLE,
                new Stop(0, Color.valueOf("#c4beba")),
                new Stop(0.25, Color.valueOf("#4f4f4f"))));
        button.setEffect(new DropShadow(2, Color.valueOf("#00000089")));
        button.setOnMousePressed(event -> enabled.setValue(!enabled.getValue()));
        enabled.addListener((observable, oldValue, newValue) -> {
            if (newValue)
                button.setFill(accentColor);
            else
                button.setFill(Color.valueOf("#dfd8d8"));
        });

        var label = new Label(parameter.getName());

        var vbox = new VBox(5, button, label);
        vbox.setAlignment(Pos.TOP_CENTER);
        vbox.setFillWidth(false);
        return vbox;
    }

    /**
     * Parameter change listener.
     *
     * @param value a new value of the parameter.
     */
    @Override
    public void valueChanged(Boolean value) {
        enabled.setValue(value);
    }
}
