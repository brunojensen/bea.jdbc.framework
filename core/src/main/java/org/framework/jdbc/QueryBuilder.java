package org.framework.jdbc;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.framework.jdbc.annotations.Table;

public final class QueryBuilder<S extends Searchable> implements Query {

    public static enum SelectType {
        COUNT("count(%s)"),
        DEFAULT(" %s "),
        DISTINCT("distinct %s"),
        DISTINCT_COUNT("count( distinct %s)"),
        MAX("max(%s)"),
        MIN("min(%s)"),
        SUM("sum(%s)");

        private final String expression;

        SelectType(final String expression) {
            this.expression = expression;
        }

        public String expression() {
            return expression;
        }
    }

    private static final long serialVersionUID = 1L;

    public static synchronized <S extends Searchable> QueryBuilder<S> select() {
        return new QueryBuilder<S>(SelectType.DEFAULT);
    }

    public static synchronized <S extends Searchable> QueryBuilder<S> select(final SelectType selectType,
                                                                             final String propertyName) {
        return new QueryBuilder<S>(selectType, propertyName);
    }

    public static synchronized <S extends Searchable> QueryBuilder<S> select(final String... properties) {
        return new QueryBuilder<S>(SelectType.DEFAULT, properties);
    }

    public static synchronized <S extends Searchable> QueryBuilder<S> selectDistinct() {
        return new QueryBuilder<S>(SelectType.DISTINCT);
    }

    private final List<Criteria> criterias = new ArrayList<Criteria>(0);

    private final List<String> groupBy = new ArrayList<String>(0);

    private Limit limit = new Limit(null, null);

    private final List<String> orderBy = new ArrayList<String>(0);

    @SuppressWarnings("rawtypes")
    private TargetClass principal;

    private final List<String> selection = new ArrayList<String>(0);

    private final SelectType selectType;

    private final List<TargetClass<?>> targetClasses = new ArrayList<TargetClass<?>>(0);

    private final List<TargetClass<?>> translatorClasses = new ArrayList<TargetClass<?>>(0);

    private QueryBuilder(final SelectType selectType,
                         final String... properties) {
        this.selectType = selectType;
        for (final String propertie : properties)
            if (null != propertie) selection.add(propertie);
    }

    QueryBuilder<S> addTranslatorClasses(final List<TargetClass<?>> targets) {
        translatorClasses.addAll(targets);
        return this;
    }

    @Override
    public String build() {
        return buildGroupBy(
                            buildOrderBy(buildLimit(buildWhere(buildFrom(buildSelection(new StringBuilder()))))))
            .toString();
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private StringBuilder buildFrom(final StringBuilder builder) {
        builder.append(" FROM ").append(((Class<S>) getType())
                                            .getAnnotation(Table.class).name())
            .append(" ").append(principal.getAlias());
        if (!targetClasses.isEmpty()) {
            final Iterator<TargetClass<?>> iterator = targetClasses.iterator();
            while (iterator.hasNext()) {
                final TargetClass targetClass = iterator.next();
                builder.append(", ");
                builder.append(((Class<S>) targetClass.getTarget())
                                   .getAnnotation(Table.class).name())
                    .append(" ").append(targetClass.getAlias());
            }
        }
        return builder;
    }

    private StringBuilder buildGroupBy(final StringBuilder builder) {
        if (!groupBy.isEmpty()) {
            final List<TargetClass<?>> classes = new ArrayList<TargetClass<?>>(targetClasses);
            classes.add(principal);
            builder.append(" GROUP BY ");
            final Iterator<String> iterator = groupBy.iterator();
            while (iterator.hasNext()) {
                builder.append(Translate.translate(classes, iterator.next()));
                if (iterator.hasNext()) builder.append(", ");
            }
        }
        return builder;
    }

    private StringBuilder buildLimit(final StringBuilder builder) {
        if (null == limit.getFirstResults() && null == limit.getMaxResults())
            return builder;
        final StringBuilder subbuilder = new StringBuilder();
        subbuilder.append("SELECT * FROM (")
            .append(" SELECT ROW_NUMBER() OVER (");
        if (!orderBy.isEmpty()) {
            final List<TargetClass<?>> classes = new ArrayList<TargetClass<?>>(targetClasses);
            classes.add(principal);
            subbuilder.append(" ORDER BY ");
            final Iterator<String> iterator = orderBy.iterator();
            while (iterator.hasNext()) {
                subbuilder
                    .append(Translate.translate(classes, iterator.next()));
                if (iterator.hasNext()) subbuilder.append(", ");
            }
        } else {
            subbuilder.append("ORDER BY ");
            final Iterator<String> iterator = EntityMapper.get(getPrincipal())
                .getId().keySet().iterator();

            while (iterator.hasNext()) {
                final String column = iterator.next();
                subbuilder.append(principal.getAlias()).append(".")
                    .append(column);
                if (iterator.hasNext()) subbuilder.append(", ");
            }
        }
        subbuilder.append(" ) LIMITE, ").append(builder.toString()
                                                    .replace("SELECT", ""))
            .append(") WHERE LIMITE BETWEEN ").append(limit.getFirstResults()
                                                          * limit
                                                              .getMaxResults()
                                                          + 1).append(" AND ")
            .append(limit.getFirstResults() * limit.getMaxResults()
                        + limit.getMaxResults()).append(" ORDER BY LIMITE");
        return subbuilder;
    }

    private StringBuilder buildOrderBy(final StringBuilder builder) {
        if (null != limit.getFirstResults() && null != limit.getMaxResults())
            return builder;
        if (!orderBy.isEmpty()) {
            final List<TargetClass<?>> classes = new ArrayList<TargetClass<?>>(targetClasses);
            classes.add(principal);
            builder.append(" ORDER BY ");
            final Iterator<String> iterator = orderBy.iterator();
            while (iterator.hasNext()) {
                builder.append(Translate.translate(classes, iterator.next()));
                if (iterator.hasNext()) builder.append(", ");
            }
        }
        return builder;
    }

    private StringBuilder buildSelection(final StringBuilder builder) {
        final StringBuilder selectBuilder = new StringBuilder();
        final List<TargetClass<?>> classes = new ArrayList<TargetClass<?>>(targetClasses);
        classes.add(principal);
        if (selection.isEmpty()) {
            final Map<String, FieldMapper> mapa = EntityMapper
                .get(getPrincipal()).getAllColumnsFields();
            final Iterator<Entry<String, FieldMapper>> iterator = mapa
                .entrySet().iterator();
            while (iterator.hasNext()) {
                final Entry<String, FieldMapper> entry = iterator.next();
                selectBuilder.append(principal.getAlias()).append(".")
                    .append(entry.getKey());
                if (iterator.hasNext()) selectBuilder.append(", ");
            }
        } else if (selectType.equals(SelectType.DEFAULT)
            || selectType.equals(SelectType.DISTINCT)) {
            final Iterator<String> iterator = selection.iterator();
            while (iterator.hasNext()) {
                final String value = iterator.next();
                selectBuilder.append(Translate.translate(classes, value));
                if (iterator.hasNext()) selectBuilder.append(", ");
            }
        } else {
            final Iterator<String> iterator = selection.iterator();
            while (iterator.hasNext()) {
                final String value = iterator.next();
                selectBuilder.append(Translate.translate(classes, value));
                if (iterator.hasNext()) selectBuilder.append(", ");
            }
        }
        return builder.append(" SELECT ").append(String
                                                     .format(selectType
                                                                 .expression(),
                                                             selectBuilder
                                                                 .toString()));
    }

    private StringBuilder buildWhere(final StringBuilder builder) {
        if (!criterias.isEmpty()) {
            builder.append(" WHERE ");
            final Iterator<Criteria> iterator = criterias.iterator();
            while (iterator.hasNext()) {
                final Criteria criterio = iterator.next();
                builder.append(criterio.build(translatorClasses));
                if (iterator.hasNext()) builder.append(" AND ");
            }
        }
        return builder;
    }

    public QueryBuilder<S> from(final Class<S> target, final String alias) {
        this.principal = TargetClass.<S> set(target, alias);
        translatorClasses.add(principal);
        return this;
    }

    @Override
    public List<Object> getParameters() {
        return getValues();
    }

    @SuppressWarnings("unchecked")
    Class<S> getPrincipal() {
        return principal.getTarget();
    }

    List<TargetClass<?>> getTranslatorClasses() {
        return translatorClasses;
    }

    @Override
    public Class<?> getType() {
        return getPrincipal();
    }

    public List<Object> getValues() {
        final List<Object> values = new ArrayList<Object>(0);
        for (final Criteria criterio : criterias)
            for (final Object value : criterio.getParameters())
                if (value instanceof Object[])
                    for (final Object v : (Object[]) value)
                        values.add(v);
                else values.add(value);
        return values;
    }

    public QueryBuilder<S> groupBy(final String... properties) {
        for (final String propertie : properties)
            if (null != propertie) groupBy.add(propertie);
        return this;
    }

    public <E extends Searchable> QueryBuilder<S> join(final Class<E> target,
                                                       final String alias) {
        final TargetClass<E> targetClass = TargetClass.<E> set(target, alias);
        targetClasses.add(targetClass);
        translatorClasses.add(targetClass);
        return this;
    }

    public QueryBuilder<S> limit(final Long firstResult, final Long maxResult) {
        limit = new Limit(firstResult, maxResult);
        return this;
    }

    public QueryBuilder<S> orderBy(final String... properties) {
        for (final String propertie : properties)
            if (null != propertie) orderBy.add(propertie);
        return this;
    }

    @Override
    public String toString() {
        return build();
    }

    public QueryBuilder<S> where(final Criteria... criterios) {
        this.criterias.clear();
        for (final Criteria criterio : criterios)
            if (null != criterio) this.criterias.add(criterio);
        return this;
    }

    public QueryBuilder<S> where(final List<Criteria> criterios) {
        this.criterias.clear();
        this.criterias.addAll(criterios);
        return this;
    }
}
