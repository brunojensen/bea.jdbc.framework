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
import java.util.ArrayList;
import java.util.List;

public final class QueryBuilder implements Serializable {

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

    public static synchronized QueryBuilder select() {
        return new QueryBuilder(SelectType.DEFAULT);
    }

    public static synchronized QueryBuilder select(final SelectType selectType, final String propertyName) {
        return new QueryBuilder(selectType, propertyName);
    }

    public static synchronized QueryBuilder select(final String... properties) {
        return new QueryBuilder(SelectType.DEFAULT, properties);
    }

    public static synchronized QueryBuilder selectDistinct() {
        return new QueryBuilder(SelectType.DISTINCT);
    }

    private final List<Criteria> criterias = new ArrayList<Criteria>(0);

    private final List<String> groupBy = new ArrayList<String>(0);

    private Limit limit;

    private final List<String> orderBy = new ArrayList<String>(0);

    private TargetClass principal;

    private final List<String> selection = new ArrayList<String>(0);

    private final List<TargetClass> targetClasses = new ArrayList<TargetClass>(0);

    private final List<TargetClass> translatorClasses = new ArrayList<TargetClass>(0);

    private QueryBuilder(final SelectType selectType, final String... properties) {
        for (final String propertie : properties)
            if (null != propertie) selection.add(propertie);
    }

    String build() {
        return "";
    }

    public QueryBuilder from(final Class<?> target, final String alias) {
        principal = TargetClass.set(target, alias);
        translatorClasses.add(principal);
        return this;
    }

    public Class<?> getPrincipal() {
        return principal.getTarget();
    }

    List<TargetClass> getTranslatorClasses() {
        return translatorClasses;
    }

    public List<Object> getValues() {
        final List<Object> values = new ArrayList<Object>(0);
        for (final Criteria criteria : criterias)
            for (final Object value : criteria.getValues())
                if (value instanceof Object[])
                    for (final Object v : (Object[]) value)
                        values.add(v);
                else values.add(value);
        return values;
    }

    public QueryBuilder groupBy(final String... properties) {
        for (final String propertie : properties)
            if (null != propertie) groupBy.add(propertie);
        return this;
    }

    public QueryBuilder join(final Class<?> target, final String alias) {
        translatorClasses.add(TargetClass.set(target, alias));
        return this;
    }

    public QueryBuilder limit(final Long firstResult, final Long maxResult) {
        limit = new Limit(firstResult, maxResult);
        return this;
    }

    public QueryBuilder orderBy(final String... properties) {
        for (final String property : properties)
            if (null != property) orderBy.add(property);
        return this;
    }

    @Override
    public String toString() {
        return build();
    }

    public QueryBuilder where(final Criteria... criterias) {
        this.criterias.clear();
        for (final Criteria criteria : criterias)
            if (null != criteria) this.criterias.add(criteria);
        return this;
    }

    public QueryBuilder where(final List<Criteria> criterias) {
        this.criterias.clear();
        this.criterias.addAll(criterias);
        return this;
    }
}
