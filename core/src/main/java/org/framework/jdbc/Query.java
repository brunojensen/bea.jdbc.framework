package org.framework.jdbc;

import java.io.Serializable;
import java.util.List;

public interface Query extends Serializable {

    public String build();

    public List<Object> getParameters();

    public Class<?> getType();
}
