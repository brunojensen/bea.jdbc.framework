package org.framework.model.validation.annotations;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import org.framework.model.validation.LenghtImpl;

@Retention(RUNTIME)
@Target({ METHOD, FIELD })
@Validation(LenghtImpl.class)
public @interface Lenght {

    String label();

    int max() default Integer.MAX_VALUE;

    String message() default "conf_validator_length_default_message";

    int min() default 0;
}