package br.com.bea.framework.jdbc.converters;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import br.com.bea.framework.jdbc.Converter;

public abstract class NullableConverter implements Converter {

    private boolean converting(final int index, final Object value, final PreparedStatement statement) {
        try {
            statement.setNull(index, java.sql.Types.NULL);
            return true;
        } catch (final SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean nullable(final int index, final Object value, final PreparedStatement statement) {
        return null == value ? converting(index, value, statement) : false;
    }
}
