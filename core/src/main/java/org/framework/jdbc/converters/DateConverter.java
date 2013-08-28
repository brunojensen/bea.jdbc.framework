package org.framework.jdbc.converters;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Date;
import org.framework.jdbc.annotations.Column;

class DateConverter extends NullableConverter {

    private static final DateConverter INSTANCE = new DateConverter();

    static DateConverter getInstance() {
        return DateConverter.INSTANCE;
    }

    private DateConverter() {
    }

    @Override
    public void convert(final int index, final Object value, final PreparedStatement statement) {
        try {
            if (nullable(index, value, statement)) return;
            statement.setDate(index, new java.sql.Date(((Date) value).getTime()));
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public <S extends Serializable> void convert(final S serializable, final ResultSet resultSet, final Field field) {
        try {
            final java.sql.Date date = resultSet.getDate(field.getAnnotation(Column.class).name());
            field.set(serializable, null != date ? new Date(date.getTime()) : null);
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

}
