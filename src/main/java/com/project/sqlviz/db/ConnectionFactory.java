package com.project.sqlviz.db;

import com.project.sqlviz.models.ConnectionConfig;
import com.project.sqlviz.models.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Factory Pattern implementation for creating database connections
 * Factory Pattern: Creates objects without specifying their exact class
 * Benefits: Centralized connection logic, easy to extend for new database types
 */
public class ConnectionFactory {
    
    /**
     * Creates a database connection based on the provided configuration
     * This method handles different database types through their JDBC drivers
     */
    public static Connection createConnection(ConnectionConfig config) throws SQLException {
        try {
            // Load the JDBC driver class
            // This is required for older JDBC versions, newer versions auto-load
            Class.forName(config.getDriverClass());
        } catch (ClassNotFoundException e) {
            throw new SQLException("Database driver not found: " + config.getDriverClass() + 
                                 ". Make sure the driver JAR is in your classpath.", e);
        }
        
        try {
            // Create the actual JDBC connection
            java.sql.Connection jdbcConn = DriverManager.getConnection(
                config.getJdbcUrl(),
                config.getUsername(),
                config.getPassword()
            );
            
            // Wrap it in our custom Connection class and return
            return new Connection(config, jdbcConn);
            
        } catch (SQLException e) {
            throw new SQLException("Failed to connect to database: " + e.getMessage(), e);
        }
    }
    
    /**
     * Factory method for MySQL connections
     * Demonstrates how Factory Pattern can provide specialized creation methods
     */
    public static Connection createMySQLConnection(String host, int port, String database, 
                                                 String username, String password) throws SQLException {
        ConnectionConfig config = new ConnectionConfig(
            0, // connId - can be set later
            "MySQL Connection", // name
            "jdbc:mysql://" + host + ":" + port + "/" + database,
            username,
            password,
            "com.mysql.cj.jdbc.Driver"
        );
        return createConnection(config);
    }
    
    /**
     * Factory method for PostgreSQL connections
     */
    public static Connection createPostgreSQLConnection(String host, int port, String database,
                                                      String username, String password) throws SQLException {
        ConnectionConfig config = new ConnectionConfig(
            0,
            "PostgreSQL Connection",
            "jdbc:postgresql://" + host + ":" + port + "/" + database,
            username,
            password,
            "org.postgresql.Driver"
        );
        return createConnection(config);
    }
}