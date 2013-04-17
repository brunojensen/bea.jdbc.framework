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
package org.framework.jdbc;

import java.util.List;

public class Subquery implements Criteria {

    public static Subquery eq(final String principalProperty, final QueryBuilder query) {
        return new Subquery(Expression.EQ, principalProperty, query);
    }

    public static Subquery ge(final String principalProperty, final QueryBuilder query) {
        return new Subquery(Expression.GE, principalProperty, query);
    }

    public static Subquery gt(final String principalProperty, final QueryBuilder query) {
        return new Subquery(Expression.GT, principalProperty, query);
    }

    public static Subquery in(final String principalProperty, final QueryBuilder query) {
        return new Subquery(Expression.IN, principalProperty, query);
    }

    public static Subquery le(final String principalProperty, final QueryBuilder query) {
        return new Subquery(Expression.LE, principalProperty, query);
    }

    public static Subquery lt(final String principalProperty, final QueryBuilder query) {
        return new Subquery(Expression.LT, principalProperty, query);
    }

    public static Subquery ne(final String principalProperty, final QueryBuilder query) {
        return new Subquery(Expression.NE, principalProperty, query);
    }

    private final Expression expression;

    private final String principalProperty;

    private final QueryBuilder query;

    public Subquery(final Expression expression, final String principalProperty, final QueryBuilder query) {
        this.expression = expression;
        this.principalProperty = principalProperty;
        this.query = query;
    }

    @Override
    public String build() {
        final StringBuilder query = new StringBuilder();
        if (expression.equals(Expression.IN))
            return query.append(principalProperty).append(String.format(expression.toString(), this.query.build()))
                .toString();
        return query.append(principalProperty).append(expression).append(" (").append(this.query.build()).append(" )")
            .toString();
    }

    @Override
    public List<Object> getValues() {
        return query.getValues();
    }
}
