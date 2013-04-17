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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Restriction implements Criteria {

    public static enum MatchMode {
        ANYWHERE,
        END,
        START;
    }

    public static Restriction and(final Restriction lhs, final Restriction rhs) {
        return new Restriction(lhs, rhs, Expression.AND);
    }

    public static Restriction eq(final String principalProperty, final Object value) {
        return new Restriction(principalProperty, value, Expression.EQ);
    }

    public static Restriction eqProperty(final String principalProperty, final String targetProperty) {
        return new Restriction(principalProperty, targetProperty, Expression.EQ);
    }

    public static Restriction ge(final String principalProperty, final Object value) {
        return new Restriction(principalProperty, value, Expression.GE);
    }

    public static Restriction geProperty(final String principalProperty, final String targetProperty) {
        return new Restriction(principalProperty, targetProperty, Expression.GE);
    }

    public static Restriction gt(final String principalProperty, final Object value) {
        return new Restriction(principalProperty, value, Expression.GT);
    }

    public static Restriction gtProperty(final String principalProperty, final String targetProperty) {
        return new Restriction(principalProperty, targetProperty, Expression.GT);
    }

    public static Restriction in(final String principalProperty, final Object... values) {
        return new Restriction(principalProperty, values, Expression.IN);
    }

    public static Restriction isNotNull(final String principalProperty) {
        return new Restriction(principalProperty, Expression.IS_NOT_NULL);
    }

    public static Restriction isNull(final String principalProperty) {
        return new Restriction(principalProperty, Expression.IS_NULL);
    }

    public static Restriction le(final String principalProperty, final Object value) {
        return new Restriction(principalProperty, value, Expression.LE);
    }

    public static Restriction leProperty(final String principalProperty, final String targetProperty) {
        return new Restriction(principalProperty, targetProperty, Expression.LE);
    }

    public static Restriction like(final String principalProperty, final Object value, final MatchMode matchMode) {
        return new Restriction(principalProperty, value, Expression.LIKE, matchMode);
    }

    public static Restriction lt(final String principalProperty, final Object value) {
        return new Restriction(principalProperty, value, Expression.LT);
    }

    public static Restriction ltProperty(final String principalProperty, final String targetProperty) {
        return new Restriction(principalProperty, targetProperty, Expression.LT);
    }

    public static Restriction ne(final String principalProperty, final Object value) {
        return new Restriction(principalProperty, value, Expression.NE);
    }

    public static Restriction neProperty(final String principalProperty, final String targetProperty) {
        return new Restriction(principalProperty, targetProperty, Expression.NE);
    }

    public static Restriction not(final Restriction expression) {
        return new Restriction(expression, Expression.NOT);
    }

    public static Restriction or(final Restriction lhs, final Restriction rhs) {
        return new Restriction(lhs, rhs, Expression.OR);
    }

    private final Expression expression;

    private Restriction lcriterion;

    private MatchMode matchMode;

    private String principalProperty;

    private Restriction rcriterion;

    private String targetProperty;

    private Object value;

    public Restriction(final Restriction criterion, final Expression expression) {
        lcriterion = criterion;
        this.expression = expression;
    }

    public Restriction(final Restriction lcriterion, final Restriction rcriterion, final Expression expression) {
        this.lcriterion = lcriterion;
        this.rcriterion = rcriterion;
        this.expression = expression;
    }

    public Restriction(final String principalProperty, final Expression expression) {
        this.principalProperty = principalProperty;
        this.expression = expression;
    }

    public Restriction(final String principalProperty, final Object value, final Expression expression) {
        this.principalProperty = principalProperty;
        this.value = value;
        this.expression = expression;
    }

    public Restriction(final String principalProperty,
                       final Object value,
                       final Expression expression,
                       final MatchMode matchMode) {
        this.principalProperty = principalProperty;
        this.value = value;
        this.expression = expression;
        this.matchMode = matchMode;
    }

    public Restriction(final String principalProperty, final String targetProperty, final Expression expression) {
        this.principalProperty = principalProperty;
        this.targetProperty = targetProperty;
        this.expression = expression;
    }

    @Override
    public String build() {
        final StringBuilder builder = new StringBuilder();
        switch (expression) {
            case AND:
                builder.append(String.format(expression.toString(), lcriterion.build(), rcriterion.build()));
                break;
            case BETWEEN:
                break;
            case IN:
                final StringBuilder inBuilder = new StringBuilder();
                if (value instanceof Object[]) for (int i = 0; i < ((Object[]) value).length; i++) {
                    if (i != 0) inBuilder.append(",");
                    inBuilder.append("?");
                }
                builder.append(principalProperty).append(String.format(Expression.IN.toString(), inBuilder.toString()));
                break;
            case IS_NOT_NULL:
                builder.append(principalProperty).append(expression);
                break;
            case IS_NULL:
                builder.append(principalProperty).append(expression);
                break;
            case NOT:
                builder.append(String.format(expression.toString(), lcriterion.build()));
                break;
            case OR:
                builder.append(String.format(expression.toString(), lcriterion.build(), rcriterion.build()));
                break;
            case LIKE:
                builder.append(principalProperty).append(expression).append(" ? ");
                switch (matchMode) {
                    case ANYWHERE:
                        value = new StringBuilder().append("%").append(value).append("%").toString();
                        break;
                    case END:
                        value = new StringBuilder().append(value).append("%").toString();
                        break;
                    case START:
                        value = new StringBuilder().append("%").append(value).toString();
                        break;
                }
                break;
            default:
                builder.append(principalProperty).append(expression).append(null == targetProperty
                                                                                                  ? " ? "
                                                                                                  : targetProperty);
        }
        return builder.toString();
    }

    @Override
    public List<Object> getValues() {
        final List<Object> values = new ArrayList<Object>(0);
        if (null != lcriterion) values.addAll(lcriterion.getValues());
        if (null != rcriterion) values.addAll(rcriterion.getValues());
        if (null != value) values.addAll(Arrays.asList(value));
        return values;
    }
}
