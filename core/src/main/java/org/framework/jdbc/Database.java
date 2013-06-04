package org.framework.jdbc;

import java.io.Serializable;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public interface Database extends Serializable {

    void close() throws SQLException;

    void commit() throws SQLException;

    CallableStatement prepareCall(String sql) throws SQLException;

    PreparedStatement prepareStatement(String sql) throws SQLException;

    void rollback() throws SQLException;

    void setAutoCommit(boolean autoCommit) throws SQLException;

}
