package com.project.sqlviz.models;

import java.sql.SQLException;
import java.time.LocalDateTime;

/**
 * Wrapper class for database connections
 * This class encapsulates a JDBC connection along with its configuration
 */
public class Connection {
    private ConnectionConfig config;           // Configuration used to create this connection
    private java.sql.Connection jdbcConnection; // The actual JDBC connection
    private LocalDateTime connectedAt;         // When this connection was established
    private boolean isActive;                  // Whether this connection is currently active

    // Constructor - creates a Connection object with configuration and JDBC connection
    public Connection(ConnectionConfig config, java.sql.Connection jdbcConnection) {
        this.config = config;
        this.jdbcConnection = jdbcConnection;
        this.connectedAt = LocalDateTime.now();
        this.isActive = true;
    }

    // Getters
    public ConnectionConfig getConfig() { return config; }
    public java.sql.Connection getJdbcConnection() { return jdbcConnection; }
    public LocalDateTime getConnectedAt() { return connectedAt; }
    public boolean isActive() { return isActive; }

    // Method to close the connection
    public void close() throws SQLException {
        if (jdbcConnection != null && !jdbcConnection.isClosed()) {
            jdbcConnection.close();
            this.isActive = false;
        }
    }

    // Method to check if connection is still valid
    public boolean isValid() throws SQLException {
        return jdbcConnection != null && !jdbcConnection.isClosed() && jdbcConnection.isValid(5);
    }
}