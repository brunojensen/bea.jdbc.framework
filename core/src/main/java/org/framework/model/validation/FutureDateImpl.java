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
        return ResourceBundle.getMessage(message, ResourceBundle.getMessage(label));
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