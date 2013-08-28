package org.framework.model.validation;

import java.lang.annotation.Annotation;

public interface Validation<A extends Annotation> {

    public String getMessage();

    public Validation<A> initialize(A parameters);

    public boolean isValid(Object value);
}