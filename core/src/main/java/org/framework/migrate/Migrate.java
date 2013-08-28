package org.framework.migrate;

import java.sql.Connection;
import java.sql.SQLException;

public interface Migrate {
    void migrate(final Connection connection) throws SQLException;
}
