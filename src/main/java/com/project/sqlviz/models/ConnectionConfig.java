package com.project.sqlviz.models;

public class ConnectionConfig {
    private String jdbcUrl;
    private String username;
    private String password;
    private String driverClass;

    // Constructor
    public ConnectionConfig(String jdbcUrl, String username, String password, String driverClass) {
        this.jdbcUrl = jdbcUrl;
        this.username = username;
        this.password = password;
        this.driverClass = driverClass;
    }

    // Getters
    public String getJdbcUrl() {
        return jdbcUrl;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getDriverClass() {
        return driverClass;
    }

    // Setters 
    public void setJdbcUrl(String jdbcUrl) {
        this.jdbcUrl = jdbcUrl;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setDriverClass(String driverClass) {
        this.driverClass = driverClass;
    }
}
