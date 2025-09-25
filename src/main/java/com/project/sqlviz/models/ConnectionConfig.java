package com.project.sqlviz.models;

/**
 * POJO (Plain Old Java Object) to store database connection configuration
 * This follows the Encapsulation principle - private fields with public getters/setters
 */
public class ConnectionConfig {
    private int connId;           // Unique identifier for this connection
    private String name;          // Display name for the connection
    private String jdbcUrl;       // Database URL (e.g., jdbc:mysql://localhost:3306/mydb)
    private String username;      // Database username
    private String password;      // Database password
    private String driverClass;   // JDBC driver class name

    // Constructor - initializes all fields when creating a new ConnectionConfig object
    public ConnectionConfig(int connId, String name, String jdbcUrl, String username, String password, String driverClass) {
        this.connId = connId;
        this.name = name;
        this.jdbcUrl = jdbcUrl;
        this.username = username;
        this.password = password;
        this.driverClass = driverClass;
    }

    // Default constructor - allows creating empty objects that can be populated later
    public ConnectionConfig() {}

    // Getters - provide read access to private fields (Encapsulation)
    public int getConnId() { return connId; }
    public String getName() { return name; }
    public String getJdbcUrl() { return jdbcUrl; }
    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public String getDriverClass() { return driverClass; }

    // Setters - provide controlled write access to private fields (Encapsulation)
    public void setConnId(int connId) { this.connId = connId; }
    public void setName(String name) { this.name = name; }
    public void setJdbcUrl(String jdbcUrl) { this.jdbcUrl = jdbcUrl; }
    public void setUsername(String username) { this.username = username; }
    public void setPassword(String password) { this.password = password; }
    public void setDriverClass(String driverClass) { this.driverClass = driverClass; }

    @Override
    public String toString() {
        return "ConnectionConfig{" +
                "connId=" + connId +
                ", name='" + name + '\'' +
                ", jdbcUrl='" + jdbcUrl + '\'' +
                ", username='" + username + '\'' +
                '}'; // Note: password is excluded from toString for security
    }
}