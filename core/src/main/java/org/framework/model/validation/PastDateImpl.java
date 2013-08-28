package org.framework.model.validation;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Calendar;
import java.util.Date;
import org.framework.model.ResourceBundle;
import org.framework.model.validation.annotations.PastDate;

public class PastDateImpl implements Validation<PastDate> {

    public static synchronized PastDateImpl create() {
        return new PastDateImpl().initialize((PastDate) Proxy.newProxyInstance(PastDate.class.getClassLoader(),
                                                                               new Class[] { PastDate.class },
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

    public PastDateImpl addLabel(final String label) {
        this.label = label;
        return this;
    }

    public PastDateImpl addMessage(final String message) {
        this.message = message;
        return this;
    }

    @Override
    public String getMessage() {
        return ResourceBundle.getMessage(message, label);
    }

    @Override
    public PastDateImpl initialize(final PastDate parameters) {
        return addLabel(parameters.message()).addMessage(parameters.message().isEmpty() ? null : parameters.message());
    }

    @Override
    public boolean isValid(final Object value) {
        if (null == value) return true;
        if (value instanceof Date)
            return ((Date) value).before(new Date());
        else if (value instanceof Calendar) return ((Calendar) value).before(Calendar.getInstance());
        return false;
    }
}