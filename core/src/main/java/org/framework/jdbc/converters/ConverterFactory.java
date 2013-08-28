package org.framework.jdbc.converters;

import java.math.BigDecimal;
import java.util.Date;
import org.framework.jdbc.Converter;

public final class ConverterFactory {

    public static final synchronized Converter get(final Class<?> target) {
        if (target.equals(String.class)) return StringConverter.getInstance();
        if (target.equals(char.class) || target.equals(Character.class)) return CharConverter.getInstance();
        if (target.equals(byte[].class)) return ByteArrayConverter.getInstance();
        if (target.equals(Date.class)) return DateConverter.getInstance();
        if (target.equals(Integer.class) || target.equals(int.class)) return IntegerConverter.getInstance();
        if (target.equals(Long.class) || target.equals(long.class)) return LongConverter.getInstance();
        if (target.equals(Double.class) || target.equals(double.class)) return DoubleConverter.getInstance();
        if (target.equals(BigDecimal.class)) return BigDecimalConverter.getInstance();
        return null;
    }
}
