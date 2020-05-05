package com.pema4.scalesynth.gui.controls;

import com.pema4.scalesynth.base.parameters.NumericParameter;
import com.pema4.scalesynth.base.parameters.ParameterChangeListener;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
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

public class Knob<T extends Number> extends Parent implements ParameterChangeListener<T> {
    private static final double MOVEMENT_LENGTH = 100;
    protected final NumericParameter<T> parameter;
    protected final ParameterTransform<T> transform;
    private final Pane knob;
    private final Color accentColor;
    private double currentPos; // current position of the knob (in range [0;1])
    private double startPos; // position of the knob before drag (in range [0;1])
    private double startY;

    public Knob(NumericParameter<T> parameter, ParameterTransform<T> transform, Color accentColor) {
        this.parameter = parameter;
        this.transform = transform;
        this.accentColor = accentColor;

        knob = createKnob();
        var clipPane = new StackPane(knob);
        clipPane.setAlignment(Pos.CENTER);
        var clipRectangle = new Rectangle(42, 42);
        clipPane.setClip(clipRectangle);

        var tooltip = new Tooltip(String.format("Knob %s", parameter.getName()));
        Tooltip.install(knob, tooltip);

        var label = new Label(parameter.getName());
        var vbox = new VBox(5, clipPane, label);
        vbox.setAlignment(Pos.TOP_CENTER);
        vbox.setFillWidth(false);
        getChildren().add(vbox);

        parameter.addListener(this);
        parameter.setValue(parameter.getDefault());
    }

    private Pane createKnob() {
        var body = new Circle(20, Color.valueOf("#dfd8d8"));
        var bodyStroke = new LinearGradient(0, 0, 1, 1, true, CycleMethod.NO_CYCLE,
                new Stop(0, Color.valueOf("#FFFFFF")),
                new Stop(0.25, Color.valueOf("#4f4f4f")));
        body.setStroke(bodyStroke);
        body.setStrokeType(StrokeType.INSIDE);
        body.setStrokeWidth(2);
        var bodyShadow = new DropShadow(2, Color.valueOf("#00000089"));
        body.setEffect(bodyShadow);

        var indicator = new Circle(3, accentColor);
        var indicatorGlow = new InnerShadow(4, accentColor.deriveColor(0, 1, 0.5, 0.4));
        indicator.setEffect(indicatorGlow);

        var knob = new Pane(body, indicator);
        body.setCenterX(21);
        body.setCenterY(21);
        indicator.setCenterX(21);
        indicator.setCenterY(9);

        knob.setOnMouseDragged(this::mouseDragged);
        knob.setOnMouseReleased(this::mouseReleased);
        knob.setOnMousePressed(this::mousePressed);

        return knob;
    }

    private void mousePressed(MouseEvent event) {
        // remember knob position before drag
        this.startPos = currentPos;
        this.startY = event.getSceneY();
    }

    private void mouseReleased(MouseEvent event) {
        // do nothing?
    }

    private void mouseDragged(MouseEvent event) {
        var yDelta = (event.getSceneY() - startY);
        var posDelta = yDelta / MOVEMENT_LENGTH; // position delta
        var newPos = startPos - posDelta; // negative delta means greater value.
        newPos = Math.max(0, Math.min(newPos, 1)); // fit in range [0; 1]
        currentPos = newPos;
        updateParameter(transform.toParameter(currentPos));
    }

    private void updatePosition(double currentPos) {
        this.currentPos = currentPos;
        var rotation = 300 * currentPos - 150;
        knob.setRotate(rotation);
    }

    private void updateParameter(T parameterValue) {
        parameter.setValue(parameterValue);
    }

    @Override
    public void valueChanged(T value) {
        var newPos = transform.toPosition(value);
        updatePosition(newPos);
    }
}
