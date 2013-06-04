package org.framework.oracle;

import java.sql.Connection;
import java.sql.SQLException;
import javax.sql.DataSource;
import oracle.jdbc.pool.OracleDataSource;
import org.framework.config.DatabaseProperties;
import org.framework.jdbc.database.AbstractDatabase;

public class OracleDatabase extends AbstractDatabase {

    private static final long serialVersionUID = 1L;

    @Override
    public Connection getConnection() throws SQLException {
        return null == connection ? (connection = getDataSource()
            .getConnection(DatabaseProperties.get("db_username"),
                           DatabaseProperties.get("db_password"))) : connection;
    }

    @Override
    public DataSource getDataSource() throws SQLException {
        final OracleDataSource oracleDataSource = new OracleDataSource();
        oracleDataSource.setURL(DatabaseProperties.get("db_url_connection"));
        return null == dataSource
                                 ? (dataSource = oracleDataSource) : dataSource;
    }

}