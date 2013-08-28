package org.framework.model.validation;

import java.lang.reflect.Array;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Collection;
import java.util.Map;
import org.framework.model.ResourceBundle;
import org.framework.model.validation.annotations.NotEmpty;

public class NotEmptyImpl implements Validation<NotEmpty> {

    public static synchronized NotEmptyImpl create() {
        return new NotEmptyImpl().initialize((NotEmpty) Proxy.newProxyInstance(NotEmpty.class.getClassLoader(),
                                                                               new Class[] { NotEmpty.class },
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

    private String message;

    public NotEmptyImpl addLabel(final String label) {
        this.label = label;
        return this;
    }

    public NotEmptyImpl addMessage(final String message) {
        this.message = message;
        return this;
    }

    @Override
    public String getMessage() {
        return ResourceBundle.getMessage(message, label);
    }

    @Override
    public NotEmptyImpl initialize(final NotEmpty parameters) {
        return addLabel(parameters.label()).addMessage(parameters.message().isEmpty() ? null : parameters.message());
    }

    @Override
    public boolean isValid(final Object value) {
        if (null == value)
            return true;
        else if (value instanceof String)
            return 0 < ((String) value).trim().length();
        else if (value instanceof Collection)
            return 0 < ((Collection<?>) value).size();
        else if (value instanceof Map)
            return 0 < ((Map<?, ?>) value).size();
        else if (value.getClass().isArray()) return 0 < Array.getLength(value);
        return false;
    }
}