package com.project.sqlviz.services;

import com.project.sqlviz.models.Connection;
import com.project.sqlviz.models.QueryResult;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Service class responsible for executing SQL queries
 * This class handles the actual database interaction using JDBC
 */
public class QueryExecutor {
    private Connection connection;
    private SqlInterpreter interpreter;

    public QueryExecutor(Connection connection) {
        this.connection = connection;
        this.interpreter = new SqlInterpreter();
    }

    /**
     * Executes a SQL query and returns results
     * This method handles both SELECT queries (with results) and other queries (without results)
     */
    public QueryResult executeQuery(String sql) {
        long startTime = System.currentTimeMillis();

        try {
            // Validate the query first
            if (!interpreter.isValidQuery(sql)) {
                long duration = System.currentTimeMillis() - startTime;
                return new QueryResult(sql, "Invalid SQL query", duration);
            }

            // Sanitize the query
            String sanitizedSql = interpreter.sanitizeQuery(sql);
            
            // Check connection validity
            if (!connection.isValid()) {
                long duration = System.currentTimeMillis() - startTime;
                return new QueryResult(sql, "Database connection is not valid", duration);
            }

            // Determine query type to choose appropriate execution method
            SqlInterpreter.QueryType queryType = interpreter.interpretQueryType(sanitizedSql);

            if (queryType == SqlInterpreter.QueryType.SELECT) {
                return executeSelectQuery(sanitizedSql, startTime);
            } else {
                return executeUpdateQuery(sanitizedSql, startTime);
            }

        } catch (SQLException e) {
            long duration = System.currentTimeMillis() - startTime;
            return new QueryResult(sql, "Database error: " + e.getMessage(), duration);
        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            return new QueryResult(sql, "Unexpected error: " + e.getMessage(), duration);
        }
    }

    /**
     * Executes SELECT queries that return data
     */
    private QueryResult executeSelectQuery(String sql, long startTime) throws SQLException {
        try (PreparedStatement stmt = connection.getJdbcConnection().prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            // Get column information
            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();
            
            List<String> columnNames = new ArrayList<>();
            for (int i = 1; i <= columnCount; i++) {
                columnNames.add(metaData.getColumnName(i));
            }

            // Get row data
            List<List<Object>> rows = new ArrayList<>();
            while (rs.next()) {
                List<Object> row = new ArrayList<>();
                for (int i = 1; i <= columnCount; i++) {
                    row.add(rs.getObject(i));
                }
                rows.add(row);
            }

            long duration = System.currentTimeMillis() - startTime;
            return new QueryResult(sql, columnNames, rows, duration);
        }
    }

    /**
     * Executes INSERT, UPDATE, DELETE queries that don't return data
     */
    private QueryResult executeUpdateQuery(String sql, long startTime) throws SQLException {
        try (PreparedStatement stmt = connection.getJdbcConnection().prepareStatement(sql)) {
            int rowsAffected = stmt.executeUpdate();
            
            // Create a result indicating success with number of affected rows
            List<String> columnNames = List.of("Rows Affected");
            List<List<Object>> rows = List.of(List.of(rowsAffected));
            
            long duration = System.currentTimeMillis() - startTime;
            return new QueryResult(sql, columnNames, rows, duration);
        }
    }

    /**
     * Executes query with parameters (prepared statement)
     * This method prevents SQL injection by using parameterized queries
     */
    public QueryResult executeParameterizedQuery(String sql, List<Object> parameters) {
        long startTime = System.currentTimeMillis();

        try {
            if (!connection.isValid()) {
                long duration = System.currentTimeMillis() - startTime;
                return new QueryResult(sql, "Database connection is not valid", duration);
            }

            try (PreparedStatement stmt = connection.getJdbcConnection().prepareStatement(sql)) {
                // Set parameters
                for (int i = 0; i < parameters.size(); i++) {
                    stmt.setObject(i + 1, parameters.get(i));
                }

                // Execute based on query type
                SqlInterpreter.QueryType queryType = interpreter.interpretQueryType(sql);
                if (queryType == SqlInterpreter.QueryType.SELECT) {
                    try (ResultSet rs = stmt.executeQuery()) {
                        return processResultSet(sql, rs, startTime);
                    }
                } else {
                    int rowsAffected = stmt.executeUpdate();
                    List<String> columnNames = List.of("Rows Affected");
                    List<List<Object>> rows = List.of(List.of(rowsAffected));
                    long duration = System.currentTimeMillis() - startTime;
                    return new QueryResult(sql, columnNames, rows, duration);
                }
            }

        } catch (SQLException e) {
            long duration = System.currentTimeMillis() - startTime;
            return new QueryResult(sql, "Database error: " + e.getMessage(), duration);
        }
    }

    /**
     * Helper method to process ResultSet into QueryResult
     */
    private QueryResult processResultSet(String sql, ResultSet rs, long startTime) throws SQLException {
        ResultSetMetaData metaData = rs.getMetaData();
        int columnCount = metaData.getColumnCount();
        
        List<String> columnNames = new ArrayList<>();
        for (int i = 1; i <= columnCount; i++) {
            columnNames.add(metaData.getColumnName(i));
        }

        List<List<Object>> rows = new ArrayList<>();
        while (rs.next()) {
            List<Object> row = new ArrayList<>();
            for (int i = 1; i <= columnCount; i++) {
                row.add(rs.getObject(i));
            }
            rows.add(row);
        }

        long duration = System.currentTimeMillis() - startTime;
        return new QueryResult(sql, columnNames, rows, duration);
    }
}