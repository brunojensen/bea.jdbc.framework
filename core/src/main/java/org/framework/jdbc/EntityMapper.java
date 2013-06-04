package org.framework.jdbc;

import java.lang.reflect.Field;
import java.util.LinkedHashMap;
import java.util.Map;
import org.framework.jdbc.annotations.Column;
import org.framework.jdbc.annotations.Id;
import org.framework.jdbc.annotations.Sequence;
import org.framework.jdbc.annotations.Table;
import org.framework.jdbc.annotations.Transient;

final class EntityMapper {
    private static final Map<Integer, EntityMapper> CACHE = new LinkedHashMap<Integer, EntityMapper>();

    private static final synchronized <S extends Searchable> EntityMapper create(final Class<S> targetClass) {
        return new EntityMapper(targetClass);
    }

    static final synchronized <S extends Searchable> EntityMapper get(final Class<S> targetClass) {
        if (!EntityMapper.CACHE.containsKey(targetClass.hashCode())) EntityMapper.create(targetClass);
        return EntityMapper.CACHE.get(targetClass.hashCode());
    }

    private final Integer classHashCode;
    private final Map<String, FieldMapper> columnsFields = new LinkedHashMap<String, FieldMapper>(0);
    private final Map<String, FieldMapper> id = new LinkedHashMap<String, FieldMapper>(0);
    private String schema;
    private String sequence;
    private String table;

    private Id.Type type;

    private <S extends Searchable> EntityMapper(final Class<S> targetClass) {
        classHashCode = targetClass.hashCode();
        if (targetClass.isAnnotationPresent(Table.class)) {
            final Table annotation = targetClass.getAnnotation(Table.class);
            table = annotation.name();
            schema = annotation.schema();
        }
        if (targetClass.isAnnotationPresent(Sequence.class)) {
            sequence = targetClass.getAnnotation(Sequence.class).value();
        }
        for (final Field field : targetClass.getDeclaredFields()) {
            if (!field.isAnnotationPresent(Transient.class)) {
                if (field.isAnnotationPresent(Id.class)) {
                    type = field.getAnnotation(Id.class).value();
                    idMapper(field);
                } else if (field.isAnnotationPresent(Column.class))
                    columnsFields.put(field.getAnnotation(Column.class).name(), FieldMapper.create(this, field, false));
            }
        }
        EntityMapper.CACHE.put(getClassHashCode(), this);
    }

    public Map<String, FieldMapper> getAllColumnsFields() {
        final Map<String, FieldMapper> allColumns = new LinkedHashMap<String, FieldMapper>();
        allColumns.putAll(getId());
        allColumns.putAll(getColumnsFields());
        return allColumns;
    }

    public Integer getClassHashCode() {
        return classHashCode;
    }

    public Map<String, FieldMapper> getColumnsFields() {
        return new LinkedHashMap<String, FieldMapper>(columnsFields);
    }

    public Map<String, FieldMapper> getId() {
        return new LinkedHashMap<String, FieldMapper>(id);
    }

    public String getSchema() {
        return schema;
    }

    public String getSequence() {
        return sequence;
    }

    public String getTable() {
        return table;
    }

    public Id.Type getType() {
        return type;
    }

    private void idMapper(final Field fieldId) {
        if (fieldId.getType().getGenericInterfaces()[0].equals(EmbeddedId.class)) {
            for (final Field field : fieldId.getType().getDeclaredFields()) {
                if (!field.isAnnotationPresent(Transient.class)) {
                    if (field.isAnnotationPresent(Column.class))
                        id.put(field.getAnnotation(Column.class).name(), FieldMapper.create(this, field, true));
                }
            }
        } else {
            id.put(fieldId.getAnnotation(Column.class).name(), FieldMapper.create(this, fieldId, true));
        }
    }

}
