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
import br.com.bea.framework.model.ResourceBundle;
import br.com.bea.framework.model.validation.annotations.Pattern;

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
        return ResourceBundle.getMessage(message, ResourceBundle.getMessage(label));
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