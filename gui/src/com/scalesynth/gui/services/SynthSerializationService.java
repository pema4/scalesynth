package com.scalesynth.gui.services;

import com.scalesynth.ScaleSynthParameters;
import com.scalesynth.base.parameters.Parameter;

import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.ParameterizedType;
import java.util.HashMap;
import java.util.Map;

/**
 * Service with various tools for saving and loading state of parameters.
 */
public class SynthSerializationService {
    private final ScaleSynthParameters parameters;

    /**
     * Constructs a new instance of serialization service.
     *
     * @param parameters synth parameters to work with.
     */
    public SynthSerializationService(ScaleSynthParameters parameters) {
        this.parameters = parameters;
    }

    /**
     * Reads a state from that parameters object.
     * State is represented as pairs "field name" - "parameter value"
     *
     * @param parameters serialized parameters.
     * @return current parameters state.
     * @throws ReflectiveOperationException thrown when
     */
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

    /**
     * Updates parameters with values from state.
     *
     * @param parameters parameters to be updated.
     * @param state      new values.
     * @throws ReflectiveOperationException error in reading plugin parameters.
     */
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

    /**
     * Read state from the file and updates parameters with this.
     *
     * @param stream input stream to read from.
     * @throws ReflectiveOperationException error in reading plugin parameters.
     */
    @SuppressWarnings("unchecked")
    public void open(InputStream stream) throws ReflectiveOperationException {
        XMLDecoder decoder = new XMLDecoder(stream);
        Map<String, Object> state = (Map<String, Object>) decoder.readObject();
        decoder.close();
        setState(parameters, state);
    }

    /**
     * Saves current parameters values to the file.
     *
     * @param stream output stream to save to.
     * @throws ReflectiveOperationException error in reading plugin parameters.
     */
    public void save(OutputStream stream) throws ReflectiveOperationException {
        var state = getState(parameters);
        XMLEncoder encoder = new XMLEncoder(stream);
        encoder.writeObject(state);
        encoder.close();
        encoder.setExceptionListener(Throwable::printStackTrace);
    }
}
