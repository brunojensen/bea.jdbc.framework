package org.framework.jdbc.converters;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import org.framework.jdbc.annotations.Column;

class CharConverter extends NullableConverter {

    private static final CharConverter INSTANCE = new CharConverter();

    static CharConverter getInstance() {
        return CharConverter.INSTANCE;
    }

    private CharConverter() {
    }

    @Override
    public void convert(final int index, final Object value, final PreparedStatement statement) {
        try {
            if (nullable(index, value, statement)) return;
            statement.setString(index, String.valueOf(value));
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public <S extends Serializable> void convert(final S serializable, final ResultSet resultSet, final Field field) {
        try {
            final String value = resultSet.getString(field.getAnnotation(Column.class).name());
            field.set(serializable, null != value && !value.isEmpty() ? value.charAt(0) : ' ');
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

}
