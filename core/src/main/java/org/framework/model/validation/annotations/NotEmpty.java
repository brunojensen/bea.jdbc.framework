package org.framework.model.validation.annotations;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import org.framework.model.validation.NotEmptyImpl;

@Retention(RUNTIME)
@Target({ METHOD, FIELD })
@Validation(NotEmptyImpl.class)
public @interface NotEmpty {

    String label();

    String message() default "conf_validator_notempty_default_message";
}