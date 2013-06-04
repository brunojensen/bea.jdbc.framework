package org.framework.model.validation;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Calendar;
import java.util.Date;
import org.framework.model.ResourceBundle;
import org.framework.model.validation.annotations.FutureDate;

public class FutureDateImpl implements Validation<FutureDate> {

    public static synchronized FutureDateImpl create() {
        return new FutureDateImpl().initialize((FutureDate) Proxy.newProxyInstance(FutureDate.class.getClassLoader(),
                                                                                   new Class[] { FutureDate.class },
                                                                                   new InvocationHandler() {
                                                                                       @Override
                                                                                       public Object invoke(final Object proxy,
                                                                                                            final Method method,
                                                                                                            final Object[] args) {
                                                                                           return method
                                                                                               .getDefaultValue();
                                                                                       }
                                                                                   }));
    }

    private String label;

    private String message;

    public FutureDateImpl addLabel(final String label) {
        this.label = label;
        return this;
    }

    public FutureDateImpl addMessage(final String message) {
        this.message = message;
        return this;
    }

    @Override
    public String getMessage() {
        return ResourceBundle.getMessage(message, label);
    }

    @Override
    public FutureDateImpl initialize(final FutureDate parameters) {
        return addLabel(parameters.label()).addMessage(parameters.message().isEmpty() ? null : parameters.message());
    }

    @Override
    public boolean isValid(final Object value) {
        if (null == value) return true;
        if (value instanceof Date)
            return ((Date) value).after(new Date());
        else if (value instanceof Calendar) return ((Calendar) value).after(Calendar.getInstance());
        return false;
    }
}