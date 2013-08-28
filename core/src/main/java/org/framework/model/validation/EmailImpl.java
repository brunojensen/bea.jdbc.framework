package org.framework.model.validation;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.regex.Pattern;
import org.framework.model.ResourceBundle;
import org.framework.model.validation.annotations.Email;

public class EmailImpl implements Validation<Email> {

    public static synchronized EmailImpl create() {
        return new EmailImpl().initialize((Email) Proxy.newProxyInstance(Email.class.getClassLoader(),
                                                                         new Class[] { Email.class },
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

    public EmailImpl addLabel(final String label) {
        this.label = label;
        return this;
    }

    public EmailImpl addMessage(final String message) {
        this.message = message;
        return this;
    }

    @Override
    public String getMessage() {
        return ResourceBundle.getMessage(message, label);
    }

    @Override
    public EmailImpl initialize(final Email parameters) {
        return addLabel(parameters.label()).addMessage(parameters.message().isEmpty() ? null : parameters.message());
    }

    @Override
    public boolean isValid(final Object value) {
        if (null == value)
            return true;
        else if (value instanceof String) {
            final String mail = "[^\\x00-\\x1F^\\(^\\)^\\<^\\>^\\@^\\,^\\;^\\:^\\\\^\\\"^\\.^\\[^\\]^\\s]";
            final String domain = "(" + mail + "+(\\." + mail + "+)*";
            final String ip = "\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\]";
            final String regex = "^" + mail + "+(\\." + mail + "+)*@" + domain + "|" + ip + ")$";
            return Pattern.compile(regex, Pattern.CASE_INSENSITIVE).matcher((String) value).matches()
                || ((String) value).isEmpty();
        }
        return false;
    }
}