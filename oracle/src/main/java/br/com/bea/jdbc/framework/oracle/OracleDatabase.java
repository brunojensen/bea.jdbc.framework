package br.com.bea.jdbc.framework.oracle;

import java.sql.Connection;
import java.sql.SQLException;
import javax.sql.DataSource;
import oracle.jdbc.pool.OracleDataSource;
import br.com.bea.framework.config.DatabaseProperties;
import br.com.bea.framework.jdbc.database.AbstractDatabase;

public class OracleDatabase extends AbstractDatabase {

    @Override
    public Connection getConnection() throws SQLException {
        return null == connection
                                 ? (connection = getDataSource().getConnection(DatabaseProperties.get("db_username"),
                                                                               DatabaseProperties.get("db_password")))
                                 : connection;
    }

    @Override
    public DataSource getDataSource() throws SQLException {
        final OracleDataSource oracleDataSource = new OracleDataSource();
        oracleDataSource.setURL(DatabaseProperties.get("db_url_connection"));
        return null == dataSource ? (dataSource = oracleDataSource) : dataSource;
    }

}