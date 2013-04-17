package br.com.bea.framework.jdbc.converters;

import java.math.BigDecimal;
import java.util.Date;
import br.com.bea.framework.jdbc.Converter;

public final class ConverterFactory {

    public static final synchronized Converter get(final Class<?> target) {
        if (target.equals(String.class)) return StringConverter.getInstance();
        if (target.equals(BigDecimal.class)) return BigDecimalConverter.getInstance();
        if (target.equals(Date.class)) return DateConverter.getInstance();
        if (target.equals(Long.class)) return LongConverter.getInstance();
        return null;
    }
}
