package org.framework.model.validation.annotations;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import org.framework.model.validation.CNPJImpl;

@Retention(RUNTIME)
@Target({ METHOD, FIELD })
@Validation(CNPJImpl.class)
public @interface CNPJ {

    String label();

    String message() default "conf_validator_cnpj_default_message";
}