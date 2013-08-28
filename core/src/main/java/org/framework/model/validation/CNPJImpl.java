package org.framework.model.validation;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.regex.Pattern;
import org.framework.model.ResourceBundle;
import org.framework.model.validation.annotations.CNPJ;

public class CNPJImpl implements Validation<CNPJ> {

    public static synchronized CNPJImpl create() {
        return new CNPJImpl().initialize((CNPJ) Proxy.newProxyInstance(CNPJ.class.getClassLoader(),
                                                                       new Class[] { CNPJ.class },
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

    public CNPJImpl addLabel(final String label) {
        this.label = label;
        return this;
    }

    public CNPJImpl addMessage(final String message) {
        this.message = message;
        return this;
    }

    @Override
    public String getMessage() {
        return ResourceBundle.getMessage(message, label);
    }

    @Override
    public CNPJImpl initialize(final CNPJ parameters) {
        return addLabel(parameters.label()).addMessage(parameters.message().isEmpty() ? null : parameters.message());
    }

    @Override
    public boolean isValid(final Object value) {
        if (null == value)
            return true;
        else {
            String cnpj = String.valueOf(value);
            if (cnpj.isEmpty()) return true;
            if (Pattern.compile("^[0-9]{14}$").matcher(cnpj = cnpj.replaceAll("(\\.|\\-|\\/)", "")).matches()) {
                final char[] numbers = cnpj.toCharArray();
                int first = 0, last = 0, result = 0;
                for (int i = 0, j = 5, l = 13; i < 12; i++, j--, l--)
                    if (1 < j)
                        result = Character.getNumericValue(numbers[i]) * j + result;
                    else result = Character.getNumericValue(numbers[i]) * l + result;
                numbers[12] = String.valueOf(!(result % 11 < 2) ? (first = 11 - result % 11) : first).toCharArray()[0];
                result = 0;
                for (int i = 0, j = 6, l = 14; i < 13; i++, j--, l--)
                    if (1 < j)
                        result = Character.getNumericValue(numbers[i]) * j + result;
                    else result = Character.getNumericValue(numbers[i]) * l + result;
                numbers[13] = String.valueOf(!(result % 11 < 2) ? (last = 11 - result % 11) : last).toCharArray()[0];
                return Arrays.equals(cnpj.toCharArray(), numbers);
            }
        }
        return false;
    }
}