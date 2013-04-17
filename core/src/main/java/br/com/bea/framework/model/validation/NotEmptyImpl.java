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

import java.lang.reflect.Array;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Collection;
import java.util.Map;
import br.com.bea.framework.model.ResourceBundle;
import br.com.bea.framework.model.validation.annotations.NotEmpty;

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
        return ResourceBundle.getMessage(message, ResourceBundle.getMessage(label));
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