package br.com.bea.framework.jdbc.converters;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import br.com.bea.framework.jdbc.annotations.Column;

class StringConverter extends NullableConverter {

    private static final StringConverter INSTANCE = new StringConverter();

    static StringConverter getInstance() {
        return StringConverter.INSTANCE;
    }

    private StringConverter() {
    }

    @Override
    public void convert(final int index, final Object value, final PreparedStatement statement) {
        try {
            if (nullable(index, value, statement)) return;
            statement.setString(index, value.toString());
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public <S extends Serializable> void convert(final S serializable, final ResultSet resultSet, final Field field) {
        try {
            field.set(serializable, resultSet.getString(field.getAnnotation(Column.class).name()));
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

}
