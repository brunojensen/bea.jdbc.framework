package org.framework.jdbc;

class TargetClass<S extends Searchable> {

    static synchronized <S extends Searchable> TargetClass<S> set(final Class<S> target, final String alias) {
        return new TargetClass<S>(target, alias);
    }

    private String alias;

    private Class<S> target;

    private TargetClass(final Class<S> target, final String alias) {
        this.target = target;
        this.alias = alias;
    }

    public String getAlias() {
        return alias;
    }

    public Class<S> getTarget() {
        return target;
    }

    public void setAlias(final String alias) {
        this.alias = alias;
    }

    public void setTarget(final Class<S> target) {
        this.target = target;
    }
}
