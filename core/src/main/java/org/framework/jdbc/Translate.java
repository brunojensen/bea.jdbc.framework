package org.framework.jdbc;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public final class Translate {
    @SuppressWarnings("unchecked")
    public static <S extends Searchable> String translate(final List<TargetClass<?>> targetClasses,
                                                          final String property) {
        if (null == property) return property;
        for (final TargetClass<?> targetClass : targetClasses)
            if (property.contains(targetClass.getAlias())) {
                final Map<String, FieldMapper> mapa = EntityMapper.get((Class<S>) targetClass.getTarget())
                    .getAllColumnsFields();
                final Iterator<Entry<String, FieldMapper>> iterator = mapa.entrySet().iterator();
                while (iterator.hasNext()) {
                    final Entry<String, FieldMapper> entry = iterator.next();
                    if (property.contains(String.format("%s.%s", targetClass.getAlias(), entry.getValue().getField()
                        .getName()))) return property.replace(entry.getValue().getField().getName(), entry.getKey());
                }
            }
        return property;
    }
}
