package org.framework.jdbc;

import java.util.List;

public interface Criteria {

    static enum Expression {
        AND("(%s AND %s)"),
        BETWEEN("BETWEEN ? AND ?"),
        EQ(" = "),
        GE(" >= "),
        GT(" > "),
        IN(" IN (%s) "),
        IS_NOT_NULL(" IS NOT NULL "),
        IS_NULL(" IS NULL "),
        LE(" <= "),
        LIKE(" LIKE "),
        LT(" < "),
        NE(" != "),
        NOT("NOT (%s)"),
        OR(" (%s OR %s) ");

        private final String value;

        Expression(final String value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return value;
        }
    }

    String build(List<TargetClass<?>> targetClasses);

    List<Object> getParameters();
}
