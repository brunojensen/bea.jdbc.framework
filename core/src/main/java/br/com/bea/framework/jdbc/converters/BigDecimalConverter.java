package br.com.bea.framework.jdbc.converters;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import br.com.bea.framework.jdbc.annotations.Column;

class BigDecimalConverter extends NullableConverter {

    private static final BigDecimalConverter INSTANCE = new BigDecimalConverter();

    static BigDecimalConverter getInstance() {
        return BigDecimalConverter.INSTANCE;
    }

    private BigDecimalConverter() {
    }

    @Override
    public void convert(final int index, final Object value, final PreparedStatement statement) {
        try {
            if (nullable(index, value, statement)) return;
            statement.setBigDecimal(index, BigDecimal.valueOf(((Number) value).doubleValue()));
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public <S extends Serializable> void convert(final S serializable, final ResultSet resultSet, final Field field) {
        try {
            field.set(serializable, resultSet.getBigDecimal(field.getAnnotation(Column.class).name()));
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

}
