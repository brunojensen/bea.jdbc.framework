/*
The MIT License (MIT)
Copyright (c) 2013 B&A Tecnologia

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated 
documentation files (the "Software"), to deal in the Software without restriction, including without limitation
the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and
to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions 
of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED 
TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL 
THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF 
CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS 
IN THE SOFTWARE.
 */
package br.com.bea.framework.model.validation;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.regex.Pattern;
import br.com.bea.framework.model.ResourceBundle;
import br.com.bea.framework.model.validation.annotations.CNPJ;

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
        return ResourceBundle.getMessage(message, ResourceBundle.getMessage(label));
    }

    @Override
    public CNPJImpl initialize(final CNPJ parameters) {
        return addLabel(parameters.label()).addMessage(parameters.message().isEmpty() ? null : parameters.message());
    }

    @Override
    public boolean isValid(final Object value) {
        if (null == value)
            return true;
        else if (value instanceof String) {
            String cnpj = (String) value;
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