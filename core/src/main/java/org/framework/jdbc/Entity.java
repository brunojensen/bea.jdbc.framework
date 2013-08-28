package org.framework.jdbc;

public abstract class Entity<Id> implements Searchable {

    private static final long serialVersionUID = 1L;

    public abstract Id getId();

    public abstract void setId(Id id);

}
