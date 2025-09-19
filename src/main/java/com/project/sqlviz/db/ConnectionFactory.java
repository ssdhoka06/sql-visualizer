package com.project.sqlviz.db;

import com.project.sqlviz.models.ConnectionConfig;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionFactory {

    public static Connection getConnection(ConnectionConfig config) throws SQLException {
        try {
            // Load the JDBC driver class dynamically
            Class.forName(config.getDriverClass());
        } catch (ClassNotFoundException e) {
            throw new SQLException("Driver class not found: " + config.getDriverClass(), e);
        }

        // Return a live DB connection
        return DriverManager.getConnection(
                config.getJdbcUrl(),
                config.getUsername(),
                config.getPassword()
        );
    }
}
