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
package org.framework.model.validation.annotations;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import org.framework.model.validation.PatternImpl;

@Retention(RUNTIME)
@Target({ METHOD, FIELD })
@Validation(PatternImpl.class)
public @interface Pattern {

    public static enum Flags {

        CANON_EQ(java.util.regex.Pattern.CANON_EQ),

        CASE_INSENSITIVE(java.util.regex.Pattern.CASE_INSENSITIVE),

        COMMENTS(java.util.regex.Pattern.COMMENTS),

        DEFAULT(0),

        DOTALL(java.util.regex.Pattern.DOTALL),

        LITERAL(java.util.regex.Pattern.LITERAL),

        MULTILINE(java.util.regex.Pattern.MULTILINE),

        UNICODE_CASE(java.util.regex.Pattern.UNICODE_CASE),

        UNIX_LINES(java.util.regex.Pattern.UNIX_LINES);

        private int flag;

        private Flags(final int flag) {
            this.flag = flag;
        }

        public int value() {
            return flag;
        }
    }

    Flags flags() default Flags.DEFAULT;

    String label();

    String message() default "conf_validator_pattern_default_message";

    String value() default "";
}