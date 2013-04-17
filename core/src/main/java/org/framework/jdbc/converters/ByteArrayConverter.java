package org.framework.jdbc.converters;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import org.framework.jdbc.annotations.Column;

class ByteArrayConverter extends NullableConverter {

    private static final ByteArrayConverter INSTANCE = new ByteArrayConverter();

    static ByteArrayConverter getInstance() {
        return ByteArrayConverter.INSTANCE;
    }

    private ByteArrayConverter() {
    }

    @Override
    public void convert(final int index, final Object value, final PreparedStatement statement) {
        try {
            if (nullable(index, value, statement)) return;
            statement.setBytes(index, (byte[]) value);
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public <S extends Serializable> void convert(final S serializable, final ResultSet resultSet, final Field field) {
        try {
            field.set(serializable, resultSet.getBytes(field.getAnnotation(Column.class).name()));
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

}
