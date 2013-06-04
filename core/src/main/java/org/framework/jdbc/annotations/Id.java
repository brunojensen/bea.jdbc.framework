package org.framework.jdbc.annotations;

import static java.lang.annotation.RetentionPolicy.RUNTIME;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Documented
@Target(ElementType.FIELD)
@Retention(RUNTIME)
public @interface Id {

    public static enum Type {
        COMPOSITE,
        DEFAULT;
    }

    Type value() default Type.DEFAULT;
}
