package org.framework.jdbc;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import org.framework.jdbc.annotations.Id;
import org.framework.jdbc.converters.ConverterFactory;

final class FieldMapper {
    static final synchronized FieldMapper create(final EntityMapper parent, final Field field, final boolean primaryKey) {
        return new FieldMapper(parent, field, primaryKey);
    }

    private final Converter converter;
    private final Field field;
    private final EntityMapper parent;

    private final boolean primaryKey;

    private FieldMapper(final EntityMapper parent, final Field field, final boolean primaryKey) {
        this.field = field;
        converter = ConverterFactory.get(field.getType());
        this.primaryKey = primaryKey;
        this.parent = parent;
    }

    void convert(final int index, final Object value, final PreparedStatement statement) {
        converter.convert(index, value, statement);
    }

    <S extends Serializable> void convert(final S searchable, final ResultSet resultSet) {
        field.setAccessible(true);
        converter.convert(searchable, resultSet, field);
    }

    Field getField() {
        field.setAccessible(true);
        return field;
    }

    <E extends Entity<?>> Object getValue(final E e) throws IllegalArgumentException, IllegalAccessException {
        field.setAccessible(true);
        if (primaryKey && parent.getType().equals(Id.Type.COMPOSITE)) return field.get(e.getId());
        return field.get(e);
    }

    public boolean isPrimaryKey() {
        return primaryKey;
    }
}
