package com.pema4.scalesynth.gui.views;

import com.pema4.scalesynth.base.parameters.NumericParameter;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.HBox;

public class DoubleParameterSlider extends Parent {
    private final NumericParameter<Double> parameter;

    public DoubleParameterSlider(NumericParameter<Double> parameter) {
        this.parameter = parameter;
        var ui = createUI();
        getChildren().add(ui);
    }

    private Node createUI() {
        var name = new Label(parameter.getName());

        var unit = new Label(parameter.getUnit());

        var slider = new Slider(parameter.getMin(), parameter.getMax(), parameter.getDefault());
        slider.valueProperty().addListener((observable, oldValue, newValue) -> parameter.setValue(newValue.doubleValue()));
        parameter.addListener(slider::setValue);

        return new HBox(5, name, slider, unit);
    }
}