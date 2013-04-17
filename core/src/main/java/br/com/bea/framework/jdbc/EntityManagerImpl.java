/*
The MIT License (MIT)
Copyright (c) 2013 B&A Tecnologia

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated 
documentation files (the "Software"), to deal in the Software without restriction, including without limitation
the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and
to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions 
of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED 
TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL 
THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF 
CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS 
IN THE SOFTWARE.
 */
package br.com.bea.framework.jdbc;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import javax.ejb.Singleton;
import javax.ejb.Stateless;
import br.com.bea.framework.config.DatabaseProperties;
import br.com.bea.framework.jdbc.annotations.Id;
import br.com.bea.framework.jdbc.converters.ConverterFactory;
import br.com.bea.framework.model.validation.annotations.Validation;

@Singleton
@Stateless
public final class EntityManagerImpl implements EntityManager {

    private static Database database;
    private static final long serialVersionUID = 1L;

    private static Database getDatabase() throws Exception {
        return null == EntityManagerImpl.database ? EntityManagerImpl.database = (Database) Class
            .forName(DatabaseProperties.get("database_class")).newInstance() : EntityManagerImpl.database;
    }

    @Override
    public void close() {
        try {
            EntityManagerImpl.getDatabase().close();
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public <E extends Entity<?>> void delete(final E entity) {
        delete(Arrays.asList(entity));
    }

    @Override
    public <E extends Entity<?>> void delete(final List<E> entities) {
        @SuppressWarnings("unchecked")
        final PreparedStatement statement = (PreparedStatement) deleteStatement((Class<E>) entities.getClass()
            .getTypeParameters()[0].getClass());
        if (null != statement) {
            try {
                for (final E e : entities) {
                    final EntityMapper mapper = EntityMapper.get(e.getClass());
                    int index = 0;
                    for (final Entry<String, FieldMapper> entry : mapper.getId().entrySet()) {
                        final FieldMapper fieldMapper = entry.getValue();
                        fieldMapper.getField().setAccessible(true);
                        fieldMapper.convert(++index, fieldMapper.getField().get(e), statement);
                    }
                    statement.addBatch();
                }
                statement.executeBatch();
                EntityManagerImpl.getDatabase().commit();
            } catch (final Exception ex) {
                try {
                    EntityManagerImpl.getDatabase().rollback();
                } catch (final Exception e) {
                    e.printStackTrace();
                }
                throw new RuntimeException(ex);
            } finally {
                try {
                    statement.close();
                } catch (final Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public <E extends Entity<?>> void deleteAll(final Class<E> target) {
        try {
            EntityManagerImpl.getDatabase().prepareStatement(String.format("DELETE FROM %s", EntityMapper.get(target)
                                                                 .getTable())).execute();
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    private <E extends Entity<?>> Statement deleteStatement(final Class<E> target) {
        try {
            EntityManagerImpl.getDatabase().setAutoCommit(false);
            final String DELETE_STATEMENT = "DELETE FROM %s WHERE %s";
            final EntityMapper mapper = EntityMapper.get(target);
            final StringBuilder builder = new StringBuilder();
            for (final Iterator<Entry<String, FieldMapper>> iterator = mapper.getId().entrySet().iterator(); iterator
                .hasNext();) {
                final Entry<String, FieldMapper> entry = iterator.next();
                builder.append(entry.getKey()).append(" = ? ");
                if (iterator.hasNext()) builder.append(" AND ");
            }
            return EntityManagerImpl.getDatabase().prepareStatement(String.format(DELETE_STATEMENT, mapper.getTable(),
                                                                                  builder.toString()));
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void executeCall(final String call) {
        try {
            EntityManagerImpl.getDatabase().prepareCall(call).execute();
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public <E extends Entity<?>> E find(final Class<E> target, final Serializable id) {
        try {
            final List<Criteria> criterias = new LinkedList<Criteria>();
            final EntityMapper mapper = EntityMapper.get(target);
            for (final Entry<String, FieldMapper> entry : mapper.getId().entrySet()) {
                if (mapper.getType().equals(Id.Type.COMPOSITE))
                    criterias.add(Restriction.eq(String.format("E1.%s", entry.getKey()), entry.getValue().getField()
                        .get(id)));
                else criterias.add(Restriction.eq(String.format("E1.%s", entry.getKey()), id));
            }
            return (E) search(QueryBuilder.select().from(target, "E1").where(criterias)).get(0);
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Long get(final QueryBuilder query) {
        return null;
    }

    public <E extends Entity<?>> void persist(final List<E> entities) {
        PreparedStatement statement = null;
        try {
            for (final E e : entities) {
                if (null == statement) statement = (PreparedStatement) persistStatement(e.getClass());
                int index = 0;
                for (final Entry<String, FieldMapper> entry : EntityMapper.get(e.getClass()).getAllColumnsFields()
                    .entrySet()) {
                    final FieldMapper fieldMapper = entry.getValue();
                    fieldMapper.convert(++index, fieldMapper.getValue(e), statement);
                }
                statement.addBatch();
            }
            statement.executeBatch();
            EntityManagerImpl.getDatabase().commit();
        } catch (final Exception ex) {
            try {
                EntityManagerImpl.getDatabase().rollback();
            } catch (final Exception e) {
                e.printStackTrace();
            }
            throw new RuntimeException(ex);
        } finally {
            try {
                statement.close();
            } catch (final Exception e) {
                e.printStackTrace();
            }
        }
    }

    private <E extends Entity<?>> Statement persistStatement(final Class<E> target) {
        try {
            EntityManagerImpl.getDatabase().setAutoCommit(false);
            final EntityMapper mapper = EntityMapper.get(target);
            final StringBuilder columnsbuilder = new StringBuilder();
            final StringBuilder positionsbuilder = new StringBuilder();
            for (final Iterator<Entry<String, FieldMapper>> iterator = mapper.getAllColumnsFields().entrySet()
                .iterator(); iterator.hasNext();) {
                final Entry<String, FieldMapper> entry = iterator.next();
                columnsbuilder.append(entry.getKey());
                positionsbuilder.append(" ? ");
                if (iterator.hasNext()) {
                    columnsbuilder.append(" , ");
                    positionsbuilder.append(" , ");
                }
            }
            return EntityManagerImpl.getDatabase().prepareStatement(String.format("INSERT INTO %s(%s) VALUES (%s)",
                                                                                  mapper.getTable(), columnsbuilder
                                                                                      .toString(), positionsbuilder
                                                                                      .toString()));
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("unchecked")
    private <S extends Searchable> List<S> result(final Class<S> principal, final ResultSet resultSet) throws Exception {
        final List<S> result = new LinkedList<S>();
        while (resultSet.next()) {
            final S searchable = principal.newInstance();
            final EntityMapper mapper = EntityMapper.get(principal);
            for (final Entry<String, FieldMapper> entry : mapper.getColumnsFields().entrySet()) {
                final FieldMapper fieldMapper = entry.getValue();
                fieldMapper.getField().setAccessible(true);
                fieldMapper.convert(searchable, resultSet);
            }
            if (!mapper.getId().isEmpty()) {
                for (final Entry<String, FieldMapper> entry : mapper.getId().entrySet()) {
                    if (mapper.getType().equals(Id.Type.COMPOSITE)) {
                        final Entity<Serializable> entity = (Entity<Serializable>) searchable;
                        if (null == entity.getId())
                            entity.setId(((Class<Serializable>) ((ParameterizedType) principal.getGenericSuperclass())
                                .getActualTypeArguments()[0]).newInstance());
                        final FieldMapper fieldMapper = entry.getValue();
                        fieldMapper.convert(entity.getId(), resultSet);
                    } else {
                        final FieldMapper fieldMapper = entry.getValue();
                        fieldMapper.convert(searchable, resultSet);
                        break;
                    }
                }
            }
            result.add(searchable);
        }
        return result;

    }

    @SuppressWarnings("unchecked")
    @Override
    public <E extends Entity<?>> E save(final E entity) {
        if (null == find(entity.getClass(), (Serializable) entity.getId()))
            persist(Arrays.asList(entity));
        else update(Arrays.asList(entity));
        return entity;
    }

    @Override
    public <E extends Entity<?>> void save(final List<E> entities) {
        final List<E> persists = new LinkedList<E>();
        final List<E> updates = new LinkedList<E>();
        for (final E e : entities) {
            if (null == find(e.getClass(), (Serializable) e.getId()))
                persists.add(e);
            else updates.add(e);
        }
        persist(persists);
        update(updates);
    }

    @Override
    public <S extends Searchable> List<S> search(final QueryBuilder query) {
        PreparedStatement statement = null;
        try {
            statement = EntityManagerImpl.getDatabase().prepareStatement(query.build());
            int index = 0;
            for (final Object value : query.getValues())
                ConverterFactory.get(value.getClass()).convert(++index, value, statement);
            statement.execute();
            final ResultSet resultSet = statement.getResultSet();
            if (resultSet == null) throw new NullPointerException(ResultSet.class.getName());
            @SuppressWarnings("unchecked")
            final List<S> result = result((Class<S>) query.getPrincipal(), resultSet);
            statement.close();
            return result;
        } catch (final Exception e) {
            throw new RuntimeException(e);
        } finally {
            try {
                statement.close();
            } catch (final Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    @Override
    public List<Object[]> search(final String nativeQuery, final List<?> parameters) {
        return null;
    }

    @Override
    public <S extends Searchable> List<S> search(final String nativeQuery,
                                                 final List<?> parameters,
                                                 final Class<?> targetClass) {
        PreparedStatement statement = null;
        try {
            statement = EntityManagerImpl.getDatabase().prepareStatement(nativeQuery);
            int index = 0;
            if (null != parameters) for (final Object value : parameters)
                ConverterFactory.get(value.getClass()).convert(++index, value, statement);
            statement.execute();
            final ResultSet resultSet = statement.getResultSet();
            if (resultSet == null) throw new NullPointerException(ResultSet.class.getName());
            @SuppressWarnings("unchecked")
            final List<S> result = result((Class<S>) targetClass, resultSet);
            statement.close();
            return result;
        } catch (final Exception e) {
            throw new RuntimeException(e);
        } finally {
            try {
                statement.close();
            } catch (final Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    public <E extends Entity<?>> void update(final List<E> entities) {
        PreparedStatement statement = null;
        try {
            EntityManagerImpl.getDatabase().setAutoCommit(false);
            for (final E e : entities) {
                if (null == statement) statement = (PreparedStatement) updateStatement(e.getClass());
                final EntityMapper mapper = EntityMapper.get(e.getClass());
                int index = 0;
                for (final Entry<String, FieldMapper> entry : mapper.getColumnsFields().entrySet()) {
                    final FieldMapper fieldMapper = entry.getValue();
                    fieldMapper.convert(++index, fieldMapper.getValue(e), statement);
                }
                for (final Entry<String, FieldMapper> entry : mapper.getId().entrySet()) {
                    final FieldMapper fieldMapper = entry.getValue();
                    fieldMapper.convert(++index, fieldMapper.getValue(e), statement);
                }
                statement.addBatch();
            }
            statement.executeBatch();
            EntityManagerImpl.getDatabase().commit();
        } catch (final Exception ex) {
            try {
                EntityManagerImpl.getDatabase().rollback();
            } catch (final Exception e) {
                e.printStackTrace();
            }
            throw new RuntimeException(ex);
        } finally {
            try {
                statement.close();
            } catch (final Exception e) {
                e.printStackTrace();
            }
        }
    }

    private <E extends Entity<?>> Statement updateStatement(final Class<E> target) {
        try {
            EntityManagerImpl.getDatabase().setAutoCommit(false);
            final EntityMapper mapper = EntityMapper.get(target);
            final StringBuilder builder = new StringBuilder();
            for (final Iterator<Entry<String, FieldMapper>> iterator = mapper.getColumnsFields().entrySet().iterator(); iterator
                .hasNext();) {
                final Entry<String, FieldMapper> entry = iterator.next();
                builder.append(entry.getKey()).append(" = ? ");
                if (iterator.hasNext()) builder.append(" , ");
            }
            builder.append(" WHERE ");
            for (final Iterator<Entry<String, FieldMapper>> iterator = mapper.getId().entrySet().iterator(); iterator
                .hasNext();) {
                final Entry<String, FieldMapper> entry = iterator.next();
                builder.append(entry.getKey()).append(" = ? ");
                if (iterator.hasNext()) builder.append(" AND ");
            }
            return EntityManagerImpl.getDatabase().prepareStatement(String
                                                                        .format("UPDATE %s SET %s", mapper.getTable(),
                                                                                builder.toString()));
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public <E extends Entity<?>> List<String> validate(final E entity) {
        try {
            final List<String> results = new ArrayList<String>(0);
            for (final FieldMapper field : EntityMapper.get(entity.getClass()).getAllColumnsFields().values())
                for (final Annotation annotation : field.getField().getAnnotations())
                    if (annotation.annotationType().isAnnotationPresent(Validation.class)) {
                        field.getField().setAccessible(true);
                        @SuppressWarnings("unchecked")
                        final br.com.bea.framework.model.validation.Validation<?> validador = annotation
                            .annotationType().getAnnotation(Validation.class).value().newInstance()
                            .initialize(annotation);
                        if (!validador.isValid(field.getField().get(entity))) results.add(validador.getMessage());
                    }
            return results;
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

}
