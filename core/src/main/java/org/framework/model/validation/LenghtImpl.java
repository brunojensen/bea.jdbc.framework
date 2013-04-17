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