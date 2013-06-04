package org.framework.model.metadata;

import java.lang.reflect.Field;

public final class MetadataObject {
    private final Field field;
    private final String value;

    public MetadataObject(final Field field, final String value) {
        this.field = field;
        this.value = value;
    }

    public Field getField() {
        return field;
    }

    public String getValue() {
        return value;
    }

}
