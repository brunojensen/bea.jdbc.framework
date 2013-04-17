package br.com.bea.jdbc.framework.mysql;

import java.sql.Connection;
import java.sql.SQLException;
import javax.sql.DataSource;
import br.com.bea.framework.config.DatabaseProperties;
import br.com.bea.framework.jdbc.database.AbstractDatabase;
import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;

public class MysqlDatabase extends AbstractDatabase {

    @Override
    public Connection getConnection() throws SQLException {
        return null == connection
                                 ? (connection = getDataSource().getConnection(DatabaseProperties.get("db_username"),
                                                                               DatabaseProperties.get("db_password")))
                                 : connection;
    }

    @Override
    public DataSource getDataSource() throws SQLException {
        final MysqlDataSource mysqlDataSource = new MysqlDataSource();
        mysqlDataSource.setUrl(DatabaseProperties.get("db_url_connection"));
        return null == dataSource ? (dataSource = mysqlDataSource) : dataSource;
    }

}
