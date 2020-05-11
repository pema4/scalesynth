package com.scalesynth.gui.controls;

import com.scalesynth.base.parameters.NumericParameter;
import com.scalesynth.base.parameters.ParameterChangeListener;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.InnerShadow;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeType;

/**
 * Represent a toggle button controlling Numeric Parameter.
 * Parameter value is mapped to knob position using ParameterTransformer.
 */
public class Knob<T extends Number> extends Parent implements ParameterChangeListener<T> {
    private static final double MOVEMENT_LENGTH = 100;
    protected final NumericParameter<T> parameter;
    protected final ParameterTransform<T> transform;
    private final Pane knob;
    private final Color accentColor;
    private double currentPos; // current position of the knob (in range [0;1])
    private double startPos; // position of the knob before drag (in range [0;1])
    private double startY;

    /**
     * Constructs a new instance of Knob linked to that parameter and colored with accent color.
     * Parameter value is mapped to knob position using ParameterTransformer.
     *
     * @param parameter   a parameter to be wrapped.
     * @param transform   object that maps parameter value to knob position.
     * @param accentColor an accent color to use.
     */
    public Knob(NumericParameter<T> parameter, ParameterTransform<T> transform, Color accentColor) {
        this.parameter = parameter;
        this.transform = transform;
        this.accentColor = accentColor;

        knob = createKnob();
        var clipPane = new StackPane(knob);
        clipPane.setAlignment(Pos.CENTER);
        var clipRectangle = new Rectangle();
        clipRectangle.widthProperty().bind(knob.widthProperty());
        clipRectangle.heightProperty().bind(knob.heightProperty());
        clipPane.setClip(clipRectangle);

        var label = new Label(parameter.getName());
        var vbox = new VBox(5, clipPane, label);
        vbox.setAlignment(Pos.TOP_CENTER);
        vbox.setFillWidth(false);
        getChildren().add(vbox);

        parameter.addListener(this);
        parameter.setValue(parameter.getDefault());
    }

    /**
     * Returns a knob (basically two circles wrapped in Pane).
     *
     * @return a knob.
     */
    private Pane createKnob() {
        var body = new Circle(25, Color.valueOf("#dfd8d8"));
        var bodyStroke = new LinearGradient(0, 0, 1, 1, true, CycleMethod.NO_CYCLE,
                new Stop(0, Color.valueOf("#FFFFFF")),
                new Stop(0.25, Color.valueOf("#4f4f4f")));
        body.setStroke(bodyStroke);
        body.setStrokeType(StrokeType.INSIDE);
        body.setStrokeWidth(2);
        var bodyShadow = new DropShadow(3, Color.valueOf("#00000089"));
        body.setEffect(bodyShadow);

        var indicator = new Circle(3, accentColor);
        var indicatorGlow = new InnerShadow(4, accentColor.deriveColor(0, 1, 0.5, 0.4));
        indicator.setEffect(indicatorGlow);

        var knob = new Pane(body, indicator);
        body.setCenterX(26);
        body.setCenterY(26);
        indicator.setCenterX(26);
        indicator.setCenterY(9);

        knob.setOnMouseDragged(this::mouseDragged);
        knob.setOnMousePressed(this::mousePressed);

        return knob;
    }

    /**
     * Handler of mouse press events.
     * <p>
     * It updates startPos and startY fields to perform dragging. Double clicks and Ctrl + click resets the parameter.
     *
     * @param event mouse pressed event.
     */
    private void mousePressed(MouseEvent event) {
        // remember knob position before drag
        this.startPos = currentPos;
        this.startY = event.getSceneY();
        if (event.getClickCount() > 1 || event.isControlDown())
            parameter.setValue(parameter.getDefault());
    }

    /**
     * Handles mouse drag events.
     * It calculates how much mouse moved from startY position and updates parameter value.
     *
     * @param event mouse dragged event.
     */
    private void mouseDragged(MouseEvent event) {
        var yDelta = (event.getSceneY() - startY);
        var posDelta = yDelta / MOVEMENT_LENGTH; // position delta
        var newPos = startPos - posDelta; // negative delta means greater value.
        newPos = Math.max(0, Math.min(newPos, 1)); // fit in range [0; 1]
        currentPos = newPos;
        updateParameter(transform.toParameter(currentPos));
    }

    /**
     * Updates a rotation of this knob.
     * This function is called by parameter change listener.
     *
     * @param currentPos a new knob position.
     */
    private void updatePosition(double currentPos) {
        this.currentPos = currentPos;
        var rotation = 300 * currentPos - 150;
        knob.setRotate(rotation);
    }

    /**
     * Updates parameter value.
     *
     * @param parameterValue a new parameter value.
     */
    private void updateParameter(T parameterValue) {
        parameter.setValue(parameterValue);
    }

    /**
     * Parameter change listener.
     *
     * @param value a new value of the parameter.
     */
    @Override
    public void valueChanged(T value) {
        var newPos = transform.toPosition(value);
        updatePosition(newPos);
    }
}
