package org.framework.jdbc.database;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import javax.sql.DataSource;
import org.framework.jdbc.Database;

public abstract class AbstractDatabase implements Database {

    protected Connection connection;
    protected DataSource dataSource;

    @Override
    public final void close() throws SQLException {
        getConnection().close();
    }

    @Override
    public final void commit() throws SQLException {
        getConnection().commit();
    }

    protected abstract Connection getConnection() throws SQLException;

    protected abstract DataSource getDataSource() throws SQLException;

    @Override
    public final CallableStatement prepareCall(final String sql) throws SQLException {
        return getConnection().prepareCall(sql);
    }

    @Override
    public final PreparedStatement prepareStatement(final String sql) throws SQLException {
        return getConnection().prepareStatement(sql);
    }

    @Override
    public final void rollback() throws SQLException {
        getConnection().rollback();
    }

    @Override
    public final void setAutoCommit(final boolean autoCommit) throws SQLException {
        getConnection().setAutoCommit(autoCommit);
    }
}
