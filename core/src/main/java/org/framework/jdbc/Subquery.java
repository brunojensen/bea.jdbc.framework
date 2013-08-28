package org.framework.jdbc;

import java.util.LinkedList;
import java.util.List;

public class Subquery implements Criteria {

    public static Subquery eq(final String principalProperty,
                              final QueryBuilder<?> query) {
        return new Subquery(Expression.EQ, principalProperty, query);
    }

    public static Subquery ge(final String principalProperty,
                              final QueryBuilder<?> query) {
        return new Subquery(Expression.GE, principalProperty, query);
    }

    public static Subquery gt(final String principalProperty,
                              final QueryBuilder<?> query) {
        return new Subquery(Expression.GT, principalProperty, query);
    }

    public static Subquery in(final String principalProperty,
                              final QueryBuilder<?> query) {
        return new Subquery(Expression.IN, principalProperty, query);
    }

    public static Subquery le(final String principalProperty,
                              final QueryBuilder<?> query) {
        return new Subquery(Expression.LE, principalProperty, query);
    }

    public static Subquery lt(final String principalProperty,
                              final QueryBuilder<?> query) {
        return new Subquery(Expression.LT, principalProperty, query);
    }

    public static Subquery ne(final String principalProperty,
                              final QueryBuilder<?> query) {
        return new Subquery(Expression.NE, principalProperty, query);
    }

    private final Expression expression;

    private final String principalProperty;

    private final QueryBuilder<?> query;

    public Subquery(final Expression expression,
                    final String principalProperty,
                    final QueryBuilder<?> query) {
        this.expression = expression;
        this.principalProperty = principalProperty;
        this.query = query;
    }

    @Override
    public String build(final List<TargetClass<?>> targetClasses) {
        final StringBuilder query = new StringBuilder();
        final List<TargetClass<?>> targets = new LinkedList<TargetClass<?>>();
        targets.addAll(targetClasses);
        targets.addAll(this.query.getTranslatorClasses());
        this.query.addTranslatorClasses(targetClasses);
        final String columnName = Translate.translate(targets,
                                                      principalProperty);
        if (expression.equals(Expression.IN))
            return query.append(columnName).append(String.format(expression
                                                       .toString(), this.query
                                                       .build())).toString();
        return query.append(columnName).append(expression).append(" (")
            .append(this.query.build()).append(" )").toString();
    }

    @Override
    public List<Object> getParameters() {
        return query.getParameters();
    }
}
