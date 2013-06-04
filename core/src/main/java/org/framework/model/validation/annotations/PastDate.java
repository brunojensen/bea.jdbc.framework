package org.framework.model.validation.annotations;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import org.framework.model.validation.PastDateImpl;

@Retention(RUNTIME)
@Target({ METHOD, FIELD })
@Validation(PastDateImpl.class)
public @interface PastDate {

    String label();

    String message() default "conf_validator_pastdate_default_message";
}