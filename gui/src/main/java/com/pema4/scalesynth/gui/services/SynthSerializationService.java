package com.pema4.scalesynth.gui.services;

import com.pema4.scalesynth.ScaleSynthParameters;
import com.pema4.scalesynth.base.parameters.Parameter;

import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.ParameterizedType;
import java.util.HashMap;
import java.util.Map;

public class SynthSerializationService {
    private final ScaleSynthParameters parameters;

    public SynthSerializationService(ScaleSynthParameters parameters) {
        this.parameters = parameters;
    }

    private static Map<String, Object> getState(ScaleSynthParameters parameters) throws ReflectiveOperationException {
        var fields = parameters.getClass().getDeclaredFields();
        var state = new HashMap<String, Object>();
        for (var field : fields) {
            field.setAccessible(true);
            if (Parameter.class.isAssignableFrom(field.getType())) {
                var fieldValue = field.get(parameters);
                var method = field.getType().getMethod("getValue");
                var value = method.invoke(fieldValue);
                state.put(field.getName(), value);
            }
        }
        return state;
    }

    @SuppressWarnings("unchecked")
    public void open(InputStream stream) throws ReflectiveOperationException {
        XMLDecoder decoder = new XMLDecoder(stream);
        Map<String, Object> state = (Map<String, Object>) decoder.readObject();
        decoder.close();
        setState(parameters, state);
    }

    private static void setState(ScaleSynthParameters parameters, Map<String, Object> state) throws ReflectiveOperationException {
        var fields = parameters.getClass().getDeclaredFields();
        for (var field : fields) {
            field.setAccessible(true);
            if (Parameter.class.isAssignableFrom(field.getType())) {
                var parameter = field.get(parameters);
                var value = state.get(field.getName());
                if (value == null) {
                    var valueType = field.getGenericType();
                    if (valueType instanceof ParameterizedType) {
                        var actualType = ((ParameterizedType) valueType).getActualTypeArguments()[0];
                        if (actualType instanceof Class) {
                            var actualClass = ((Class<?>) actualType);
                            var stringConstructor = actualClass.getConstructor(String.class);
                            value = stringConstructor.newInstance("0");
                        }
                    }
                }
                var method = field.getType().getMethod("setValue", Object.class);
                method.invoke(parameter, value);
            }
        }
    }

    public void save(OutputStream stream) throws ReflectiveOperationException {
        var state = getState(parameters);
        XMLEncoder encoder = new XMLEncoder(stream);
        encoder.writeObject(state);
        encoder.close();
        encoder.setExceptionListener(Throwable::printStackTrace);
    }
}
