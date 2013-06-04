package org.framework.model.validation;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import org.framework.model.ResourceBundle;
import org.framework.model.validation.annotations.Lenght;

public class LenghtImpl implements Validation<Lenght> {

    public static synchronized LenghtImpl create() {
        return new LenghtImpl().initialize((Lenght) Proxy.newProxyInstance(Lenght.class.getClassLoader(),
                                                                           new Class[] { Lenght.class },
                                                                           new InvocationHandler() {
                                                                               @Override
                                                                               public Object invoke(final Object proxy,
                                                                                                    final Method method,
                                                                                                    final Object[] args) {
                                                                                   return method.getDefaultValue();
                                                                               }
                                                                           }));
    }

    private String label;
    private int max;
    private String message;

    private int min;

    public LenghtImpl addLabel(final String label) {
        this.label = label;
        return this;
    }

    public LenghtImpl addMax(final int max) {
        this.max = max;
        return this;
    }

    public LenghtImpl addMessage(final String message) {
        this.message = message;
        return this;
    }

    public LenghtImpl addMin(final int min) {
        this.min = min;
        return this;
    }

    @Override
    public String getMessage() {
        return ResourceBundle.getMessage(message, Arrays.asList(ResourceBundle.getMessage(label), String.valueOf(min),
                                                                String.valueOf(max)).toArray());
    }

    @Override
    public LenghtImpl initialize(final Lenght parameters) {
        return addLabel(parameters.label()).addMessage(parameters.message().isEmpty() ? null : parameters.message())
            .addMin(parameters.min()).addMax(parameters.max());
    }

    @Override
    public boolean isValid(final Object value) {
        if (null == value) return true;
        final String result = String.valueOf(value);
        if (min > result.length() || max < result.length()) return false;
        return true;
    }
}