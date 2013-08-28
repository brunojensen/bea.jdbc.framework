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