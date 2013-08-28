package org.framework.model.validation;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.regex.Pattern;
import org.framework.model.ResourceBundle;
import org.framework.model.validation.annotations.CPF;

public class CPFImpl implements Validation<CPF> {

    public static synchronized CPFImpl create() {
        return new CPFImpl().initialize((CPF) Proxy.newProxyInstance(CPF.class.getClassLoader(),
                                                                     new Class[] { CPF.class },
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

    public CPFImpl addLabel(final String label) {
        this.label = label;
        return this;
    }

    public CPFImpl addMessage(final String message) {
        this.message = message;
        return this;
    }

    @Override
    public String getMessage() {
        return ResourceBundle.getMessage(message, label);
    }

    @Override
    public CPFImpl initialize(final CPF parameters) {
        return addLabel(parameters.label()).addMessage(parameters.message().isEmpty() ? null : parameters.message());
    }

    @Override
    public boolean isValid(final Object value) {
        if (null == value)
            return true;
        else {
            String cpf = String.valueOf(value);
            if (cpf.isEmpty()) return true;
            if (Pattern.compile("^[0-9]{11}$").matcher(cpf = cpf.replaceAll("(\\.|\\-)", "")).matches()
                && !"00000000000".equals(cpf) && !"11111111111".equals(cpf) && !"22222222222".equals(cpf)
                && !"33333333333".equals(cpf) && !"44444444444".equals(cpf) && !"55555555555".equals(cpf)
                && !"66666666666".equals(cpf) && !"77777777777".equals(cpf) && !"88888888888".equals(cpf)
                && !"99999999999".equals(cpf) && !"01234567890".equals(cpf)) {
                final char[] numbers = cpf.toCharArray();
                int first = 0, last = 0, result = 0;
                for (int i = 0, j = 10; i < 9; i++, j--)
                    result = Character.getNumericValue(numbers[i]) * j + result;
                numbers[9] = String.valueOf(!(result % 11 < 2) ? (first = 11 - result % 11) : first).toCharArray()[0];
                result = 0;
                for (int i = 0, j = 11; i < 10; i++, j--)
                    result = Character.getNumericValue(numbers[i]) * j + result;
                numbers[10] = String.valueOf(!(result % 11 < 2) ? (last = 11 - result % 11) : last).toCharArray()[0];
                return Arrays.equals(cpf.toCharArray(), numbers);
            }
        }
        return false;
    }
}