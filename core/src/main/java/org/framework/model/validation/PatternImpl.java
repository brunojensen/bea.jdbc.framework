package org.framework.model.validation;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import org.framework.model.ResourceBundle;
import org.framework.model.validation.annotations.Pattern;

public class PatternImpl implements Validation<Pattern> {

    public static synchronized PatternImpl create() {
        return new PatternImpl().initialize((Pattern) Proxy.newProxyInstance(Pattern.class.getClassLoader(),
                                                                             new Class[] { Pattern.class },
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

    private java.util.regex.Pattern pattern;

    public PatternImpl addLabel(final String label) {
        this.label = label;
        return this;
    }

    public PatternImpl addMessage(final String message) {
        this.message = message;
        return this;
    }

    public PatternImpl addPattern(final String regex, final Pattern.Flags flag) {
        pattern = java.util.regex.Pattern.compile(regex, flag.value());
        return this;
    }

    @Override
    public String getMessage() {
        return ResourceBundle.getMessage(message, label);
    }

    @Override
    public PatternImpl initialize(final Pattern parameters) {
        return addLabel(parameters.label()).addMessage(parameters.message().isEmpty() ? null : parameters.message())
            .addPattern(parameters.value(), parameters.flags());
    }

    @Override
    public boolean isValid(final Object value) {
        if (null == value)
            return true;
        else if (value instanceof String)
            return pattern.matcher((String) value).matches() || ((String) value).isEmpty();
        return false;
    }
}