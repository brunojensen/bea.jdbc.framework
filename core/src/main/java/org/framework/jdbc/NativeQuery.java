package org.framework.jdbc;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public final class NativeQuery implements Query {
    private static final long serialVersionUID = 1L;

    public static synchronized NativeQuery createQuery(final StringBuilder query, final Class<?> type) {
        return new NativeQuery(query, type);
    }

    private final List<Object> parameters = new LinkedList<Object>();
    private final StringBuilder query;

    private final Class<?> type;

    private NativeQuery(final StringBuilder query, final Class<?> type) {
        this.query = query;
        this.type = type;
    }

    public NativeQuery addParameter(final Object param) {
        parameters.add(param);
        return this;
    }

    public NativeQuery addParameters(final Object... params) {
        parameters.addAll(Arrays.asList(params));
        return this;
    }

    @Override
    public String build() {
        return query.toString();
    }

    @Override
    public List<Object> getParameters() {
        return parameters;
    }

    @Override
    public Class<?> getType() {
        return type;
    }
}
