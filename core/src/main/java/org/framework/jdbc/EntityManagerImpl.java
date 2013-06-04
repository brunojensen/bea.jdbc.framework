package org.framework.jdbc;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import javax.ejb.Singleton;
import javax.ejb.Stateless;
import org.framework.config.DatabaseProperties;
import org.framework.jdbc.annotations.Id;
import org.framework.jdbc.annotations.Sequence;
import org.framework.jdbc.converters.ConverterFactory;
import org.framework.model.validation.annotations.Validation;

@Singleton
@Stateless
public class EntityManagerImpl implements EntityManager {

    private static Database database;
    private static final long serialVersionUID = 1L;

    private static Database getDatabase() throws Exception {
        return null == EntityManagerImpl.database
                                                 ? EntityManagerImpl.database = (Database) Class
                                                     .forName(DatabaseProperties
                                                                  .get("database_class"))
                                                     .newInstance()
                                                 : EntityManagerImpl.database;
    }

    @Override
    public void begin() {
        try {
            EntityManagerImpl.getDatabase().setAutoCommit(false);
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void close() {
        try {
            EntityManagerImpl.getDatabase().close();
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void commit() {
        try {
            EntityManagerImpl.getDatabase().commit();
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
        PreparedStatement statement = null;
        try {
            for (final E e : entities) {
                if (null == statement)
                    statement = (PreparedStatement) deleteStatement(e
                        .getClass());
                final EntityMapper mapper = EntityMapper.get(e.getClass());
                int index = 0;
                for (final Entry<String, FieldMapper> entry : mapper.getId()
                    .entrySet()) {
                    final FieldMapper fieldMapper = entry.getValue();
                    fieldMapper.getField().setAccessible(true);
                    fieldMapper.convert(++index, fieldMapper.getField().get(e),
                                        statement);
                }
                statement.addBatch();
            }
            statement.executeBatch();

        } catch (final Exception ex) {
            try {
                EntityManagerImpl.getDatabase().rollback();
            } catch (final Exception e) {
                e.printStackTrace();
            }
            throw new RuntimeException(ex);
        } finally {
            try {
                if (null != statement) statement.close();
            } catch (final Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public <E extends Entity<?>> void deleteAll(final Class<E> target) {
        try {
            EntityManagerImpl.getDatabase()
                .prepareStatement(String.format("DELETE FROM %s", EntityMapper
                                      .get(target).getTable())).execute();
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    private <E extends Entity<?>> Statement deleteStatement(final Class<E> target) {
        try {
            final String DELETE_STATEMENT = "DELETE FROM %s WHERE %s";
            final EntityMapper mapper = EntityMapper.get(target);
            final StringBuilder builder = new StringBuilder();
            for (final Iterator<Entry<String, FieldMapper>> iterator = mapper
                .getId().entrySet().iterator(); iterator.hasNext();) {
                final Entry<String, FieldMapper> entry = iterator.next();
                builder.append(entry.getKey()).append(" = ? ");
                if (iterator.hasNext()) builder.append(" AND ");
            }
            return EntityManagerImpl.getDatabase()
                .prepareStatement(String.format(DELETE_STATEMENT, mapper
                                      .getTable(), builder.toString()));
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
    public <E extends Entity<?>> E find(final Class<E> target,
                                        final Serializable id) {
        try {
            final List<Criteria> criterias = new LinkedList<Criteria>();
            final EntityMapper mapper = EntityMapper.get(target);
            for (final Entry<String, FieldMapper> entry : mapper.getId()
                .entrySet())
                if (mapper.getType().equals(Id.Type.COMPOSITE))
                    criterias.add(Restriction.eq(String.format("E1.%s", entry
                        .getKey()), entry.getValue().getField().get(id)));
                else criterias.add(Restriction.eq(String.format("E1.%s", entry
                    .getKey()), id));
            final List<Searchable> list = search(QueryBuilder.<E> select()
                .from(target, "E1").where(criterias));
            return list.isEmpty() ? null : (E) list.get(0);
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    private <E extends Entity> void generateSequence(final E entity,
                                                     final Class<? extends Entity> target) {
        try {
            if (target.isAnnotationPresent(Sequence.class)) {
                final PreparedStatement sequenceStatement = EntityManagerImpl
                    .getDatabase()
                    .prepareStatement(String
                                          .format("SELECT %s.nextval FROM DUAL",
                                                  target
                                                      .getAnnotation(Sequence.class)
                                                      .value()));
                final ResultSet sequence = sequenceStatement.executeQuery();
                if (sequence.next()) entity.setId(sequence.getLong(1));
                sequence.close();
                sequenceStatement.close();
            }
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Object get(final Query query) {
        PreparedStatement statement = null;
        try {
            statement = EntityManagerImpl.getDatabase()
                .prepareStatement(query.build());
            int index = 0;
            for (final Object value : query.getParameters())
                ConverterFactory.get(value.getClass()).convert(++index, value,
                                                               statement);
            statement.execute();
            final ResultSet resultSet = statement.getResultSet();
            if (resultSet == null)
                throw new NullPointerException(ResultSet.class.getName());
            final Object resultado = resultSet.next()
                                                     ? resultSet.getObject(1)
                                                     : null;
            statement.close();
            return resultado;
        } catch (final Exception e) {
            throw new RuntimeException(e);
        } finally {
            try {
                if (null != statement) statement.close();
            } catch (final SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public <E extends Entity<?>> void persist(final List<E> entities) {
        PreparedStatement statement = null;
        try {
            for (final E e : entities) {
                if (null == statement)
                    statement = (PreparedStatement) persistStatement(e
                        .getClass());
                generateSequence(e, e.getClass());
                int index = 0;
                for (final Entry<String, FieldMapper> entry : EntityMapper
                    .get(e.getClass()).getAllColumnsFields().entrySet()) {
                    final FieldMapper fieldMapper = entry.getValue();
                    fieldMapper.convert(++index, fieldMapper.getValue(e),
                                        statement);
                }
                statement.addBatch();
            }
            statement.executeBatch();
        } catch (final Exception ex) {
            try {
                EntityManagerImpl.getDatabase().rollback();
            } catch (final Exception e) {
                e.printStackTrace();
            }
            throw new RuntimeException(ex);
        } finally {
            try {
                if (null != statement) statement.close();
            } catch (final Exception e) {
                e.printStackTrace();
            }
        }
    }

    private <E extends Entity<?>> Statement persistStatement(final Class<E> target) {
        try {
            final EntityMapper mapper = EntityMapper.get(target);
            final StringBuilder columnsbuilder = new StringBuilder();
            final StringBuilder positionsbuilder = new StringBuilder();
            for (final Iterator<Entry<String, FieldMapper>> iterator = mapper
                .getAllColumnsFields().entrySet().iterator(); iterator
                .hasNext();) {
                final Entry<String, FieldMapper> entry = iterator.next();
                columnsbuilder.append(entry.getKey());
                positionsbuilder.append(" ? ");
                if (iterator.hasNext()) {
                    columnsbuilder.append(" , ");
                    positionsbuilder.append(" , ");
                }
            }
            return EntityManagerImpl.getDatabase()
                .prepareStatement(String
                                      .format("INSERT INTO %s(%s) VALUES (%s)",
                                              mapper.getTable(), columnsbuilder
                                                  .toString(), positionsbuilder
                                                  .toString()));
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("unchecked")
    private <S extends Searchable> List<S> result(final Class<S> principal,
                                                  final ResultSet resultSet)
        throws Exception {
        final List<S> result = new LinkedList<S>();
        while (resultSet.next()) {
            final S searchable = principal.newInstance();
            final EntityMapper mapper = EntityMapper.get(principal);
            for (final Entry<String, FieldMapper> entry : mapper
                .getColumnsFields().entrySet()) {
                final FieldMapper fieldMapper = entry.getValue();
                fieldMapper.getField().setAccessible(true);
                fieldMapper.convert(searchable, resultSet);
            }
            if (!mapper.getId().isEmpty())
                for (final Entry<String, FieldMapper> entry : mapper.getId()
                    .entrySet())
                    if (mapper.getType().equals(Id.Type.COMPOSITE)) {
                        final Entity<Serializable> entity = (Entity<Serializable>) searchable;
                        if (null == entity.getId())
                            entity
                                .setId(((Class<Serializable>) ((ParameterizedType) principal
                                    .getGenericSuperclass())
                                    .getActualTypeArguments()[0]).newInstance());
                        final FieldMapper fieldMapper = entry.getValue();
                        fieldMapper.convert(entity.getId(), resultSet);
                    } else {
                        final FieldMapper fieldMapper = entry.getValue();
                        fieldMapper.convert(searchable, resultSet);
                        break;
                    }
            result.add(searchable);
        }
        return result;

    }

    @Override
    public void rollback() {
        try {
            EntityManagerImpl.getDatabase().rollback();
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public <E extends Entity<?>> E save(final E entity) {
        if (null == entity.getId()
            || null == find(entity.getClass(), (Serializable) entity.getId()))
            persist(Arrays.asList(entity));
        else update(Arrays.asList(entity));
        return entity;
    }

    @Override
    public <E extends Entity<?>> void save(final List<E> entities) {
        final List<E> persists = new LinkedList<E>();
        final List<E> updates = new LinkedList<E>();
        for (final E e : entities)
            if (null == e.getId()
                || null == find(e.getClass(), (Serializable) e.getId()))
                persists.add(e);
            else updates.add(e);
        persist(persists);
        update(updates);
    }

    @Override
    public <S extends Searchable> List<S> search(final Query query) {
        PreparedStatement statement = null;
        try {
            statement = EntityManagerImpl.getDatabase()
                .prepareStatement(query.build());
            int index = 0;
            for (final Object value : query.getParameters())
                ConverterFactory.get(value.getClass()).convert(++index, value,
                                                               statement);
            statement.execute();
            final ResultSet resultSet = statement.getResultSet();
            if (resultSet == null)
                throw new NullPointerException(ResultSet.class.getName());
            @SuppressWarnings("unchecked")
            final List<S> result = result((Class<S>) query.getType(), resultSet);
            if (null != statement) statement.close();
            return result;
        } catch (final Exception e) {
            throw new RuntimeException(e);
        } finally {
            try {
                if (null != statement) statement.close();
            } catch (final Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    @Override
    public List<Object[]> search(final String nativeQuery,
                                 final List<?> parameters) {
        final List<Object[]> resultado = new LinkedList<Object[]>();
        PreparedStatement statement = null;
        try {
            statement = EntityManagerImpl.getDatabase()
                .prepareStatement(nativeQuery);
            int index = 0;
            if (null != parameters) for (final Object param : parameters)
                statement.setObject(++index, param);
            statement.execute();
            final ResultSet resultSet = statement.getResultSet();
            if (resultSet == null)
                throw new NullPointerException(ResultSet.class.getName());
            while (resultSet.next()) {
                final Object[] object = new Object[resultSet.getMetaData()
                    .getColumnCount()];
                for (int i = 0; i < resultSet.getMetaData().getColumnCount(); i++)
                    object[i] = resultSet.getObject(i + 1);
                resultado.add(object);
            }
        } catch (final Exception e) {
            throw new RuntimeException(e);
        } finally {
            try {
                if (null != statement) statement.close();
            } catch (final SQLException e) {
                e.printStackTrace();
            }
        }
        return resultado;
    }

    public <E extends Entity<?>> void update(final List<E> entities) {
        PreparedStatement statement = null;
        try {

            for (final E e : entities) {
                if (null == statement)
                    statement = (PreparedStatement) updateStatement(e
                        .getClass());
                final EntityMapper mapper = EntityMapper.get(e.getClass());
                int index = 0;
                for (final Entry<String, FieldMapper> entry : mapper
                    .getColumnsFields().entrySet()) {
                    final FieldMapper fieldMapper = entry.getValue();
                    fieldMapper.convert(++index, fieldMapper.getValue(e),
                                        statement);
                }
                for (final Entry<String, FieldMapper> entry : mapper.getId()
                    .entrySet()) {
                    final FieldMapper fieldMapper = entry.getValue();
                    fieldMapper.convert(++index, fieldMapper.getValue(e),
                                        statement);
                }
                statement.addBatch();
            }
            statement.executeBatch();
        } catch (final Exception ex) {
            try {
                EntityManagerImpl.getDatabase().rollback();
            } catch (final Exception e) {
                e.printStackTrace();
            }
            throw new RuntimeException(ex);
        } finally {
            try {
                if (null != statement) statement.close();
            } catch (final Exception e) {
                e.printStackTrace();
            }
        }
    }

    private <E extends Entity<?>> Statement updateStatement(final Class<E> target) {
        try {

            final EntityMapper mapper = EntityMapper.get(target);
            final StringBuilder builder = new StringBuilder();
            for (final Iterator<Entry<String, FieldMapper>> iterator = mapper
                .getColumnsFields().entrySet().iterator(); iterator.hasNext();) {
                final Entry<String, FieldMapper> entry = iterator.next();
                builder.append(entry.getKey()).append(" = ? ");
                if (iterator.hasNext()) builder.append(" , ");
            }
            builder.append(" WHERE ");
            for (final Iterator<Entry<String, FieldMapper>> iterator = mapper
                .getId().entrySet().iterator(); iterator.hasNext();) {
                final Entry<String, FieldMapper> entry = iterator.next();
                builder.append(entry.getKey()).append(" = ? ");
                if (iterator.hasNext()) builder.append(" AND ");
            }
            return EntityManagerImpl.getDatabase()
                .prepareStatement(String.format("UPDATE %s SET %s", mapper
                                      .getTable(), builder.toString()));
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public <E extends Entity<?>> List<String> validate(final E entity) {
        try {
            final List<String> results = new ArrayList<String>(0);
            for (final FieldMapper field : EntityMapper.get(entity.getClass())
                .getAllColumnsFields().values())
                for (final Annotation annotation : field.getField()
                    .getAnnotations())
                    if (annotation.annotationType()
                        .isAnnotationPresent(Validation.class)) {
                        field.getField().setAccessible(true);
                        @SuppressWarnings("unchecked")
                        final org.framework.model.validation.Validation<?> validador = annotation
                            .annotationType().getAnnotation(Validation.class)
                            .value().newInstance().initialize(annotation);
                        if (!validador.isValid(field.getField().get(entity)))
                            results.add(validador.getMessage());
                    }
            return results;
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }
}
