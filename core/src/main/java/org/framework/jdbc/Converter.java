package org.framework.jdbc;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public interface Converter {

    void convert(final int index, final Object value, final PreparedStatement statement);

    <S extends Serializable> void convert(final S serializable, ResultSet resultSet, Field field);
}
