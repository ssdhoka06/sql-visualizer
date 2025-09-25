package com.project.sqlviz.models;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Model class to hold query execution results
 * This encapsulates all information about a query execution
 */
public class QueryResult {
    private String sqlQuery;                    // The SQL query that was executed
    private List<String> columnNames;           // Names of columns in result set
    private List<List<Object>> rows;            // Actual data rows
    private int rowCount;                       // Number of rows returned
    private long executionTimeMs;               // How long the query took to execute
    private LocalDateTime executedAt;           // When the query was executed
    private boolean isSuccessful;               // Whether query executed without errors
    private String errorMessage;                // Error message if query failed

    // Constructor for successful query
    public QueryResult(String sqlQuery, List<String> columnNames, List<List<Object>> rows, long executionTimeMs) {
        this.sqlQuery = sqlQuery;
        this.columnNames = columnNames;
        this.rows = rows;
        this.rowCount = rows.size();
        this.executionTimeMs = executionTimeMs;
        this.executedAt = LocalDateTime.now();
        this.isSuccessful = true;
    }

    // Constructor for failed query
    public QueryResult(String sqlQuery, String errorMessage, long executionTimeMs) {
        this.sqlQuery = sqlQuery;
        this.errorMessage = errorMessage;
        this.executionTimeMs = executionTimeMs;
        this.executedAt = LocalDateTime.now();
        this.isSuccessful = false;
    }

    // Getters
    public String getSqlQuery() { return sqlQuery; }
    public List<String> getColumnNames() { return columnNames; }
    public List<List<Object>> getRows() { return rows; }
    public int getRowCount() { return rowCount; }
    public long getExecutionTimeMs() { return executionTimeMs; }
    public LocalDateTime getExecutedAt() { return executedAt; }
    public boolean isSuccessful() { return isSuccessful; }
    public String getErrorMessage() { return errorMessage; }
}