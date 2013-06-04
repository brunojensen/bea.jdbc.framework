package org.framework.jdbc.database;

import java.sql.Connection;
import java.sql.SQLException;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import org.framework.config.DatabaseProperties;

public final class ContextDatabase extends AbstractDatabase {

    private static final long serialVersionUID = 1L;

    @Override
    public final Connection getConnection() throws SQLException {
        return null == connection || connection.isClosed()
                                                          ? (connection = getDataSource()
                                                              .getConnection(DatabaseProperties
                                                                                 .get("db_username"),
                                                                             DatabaseProperties
                                                                                 .get("db_password")))
                                                          : connection;
    }

    @Override
    public final DataSource getDataSource() throws SQLException {
        try {
            return null == dataSource
                                     ? (dataSource = (DataSource) new InitialContext()
                                         .lookup(DatabaseProperties
                                             .get("datasource_naming")))
                                     : dataSource;
        } catch (final NamingException e) {
            throw new SQLException(e);
        }
    }
}
