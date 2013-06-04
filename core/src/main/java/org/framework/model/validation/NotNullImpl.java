package org.framework.model.validation;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import org.framework.model.ResourceBundle;
import org.framework.model.validation.annotations.NotNull;

public class NotNullImpl implements Validation<NotNull> {

    public static synchronized NotNullImpl create() {
        return new NotNullImpl().initialize((NotNull) Proxy.newProxyInstance(NotNull.class.getClassLoader(),
                                                                             new Class[] { NotNull.class },
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

    public NotNullImpl addLabel(final String label) {
        this.label = label;
        return this;
    }

    public NotNullImpl addMessage(final String message) {
        this.message = message;
        return this;
    }

    @Override
    public String getMessage() {
        return ResourceBundle.getMessage(message, label);
    }

    @Override
    public NotNullImpl initialize(final NotNull parameters) {
        return addLabel(parameters.label()).addMessage(parameters.message().isEmpty() ? null : parameters.message());
    }

    @Override
    public boolean isValid(final Object value) {
        return null != value;
    }
}