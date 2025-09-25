package com.project.sqlviz.services;

import java.util.regex.Pattern;

/**
 * Interpreter Pattern implementation for SQL query analysis
 * Interpreter Pattern: Defines how to interpret and evaluate language expressions
 * This class analyzes SQL queries to determine their type and validate basic syntax
 */
public class SqlInterpreter {
    
    // Regular expressions to match different SQL statement types
    private static final Pattern SELECT_PATTERN = Pattern.compile("^\\s*SELECT\\s+", Pattern.CASE_INSENSITIVE);
    private static final Pattern INSERT_PATTERN = Pattern.compile("^\\s*INSERT\\s+", Pattern.CASE_INSENSITIVE);
    private static final Pattern UPDATE_PATTERN = Pattern.compile("^\\s*UPDATE\\s+", Pattern.CASE_INSENSITIVE);
    private static final Pattern DELETE_PATTERN = Pattern.compile("^\\s*DELETE\\s+", Pattern.CASE_INSENSITIVE);
    private static final Pattern CREATE_PATTERN = Pattern.compile("^\\s*CREATE\\s+", Pattern.CASE_INSENSITIVE);
    private static final Pattern DROP_PATTERN = Pattern.compile("^\\s*DROP\\s+", Pattern.CASE_INSENSITIVE);

    /**
     * Enumeration for different types of SQL statements
     */
    public enum QueryType {
        SELECT,     // Data retrieval
        INSERT,     // Data insertion
        UPDATE,     // Data modification
        DELETE,     // Data deletion
        CREATE,     // Schema creation
        DROP,       // Schema deletion
        UNKNOWN     // Unknown or unsupported query type
    }

    /**
     * Analyzes a SQL query and determines its type
     */
    public QueryType interpretQueryType(String sql) {
        if (sql == null || sql.trim().isEmpty()) {
            return QueryType.UNKNOWN;
        }

        String trimmedSql = sql.trim();
        
        if (SELECT_PATTERN.matcher(trimmedSql).find()) {
            return QueryType.SELECT;
        } else if (INSERT_PATTERN.matcher(trimmedSql).find()) {
            return QueryType.INSERT;
        } else if (UPDATE_PATTERN.matcher(trimmedSql).find()) {
            return QueryType.UPDATE;
        } else if (DELETE_PATTERN.matcher(trimmedSql).find()) {
            return QueryType.DELETE;
        } else if (CREATE_PATTERN.matcher(trimmedSql).find()) {
            return QueryType.CREATE;
        } else if (DROP_PATTERN.matcher(trimmedSql).find()) {
            return QueryType.DROP;
        } else {
            return QueryType.UNKNOWN;
        }
    }

    /**
     * Performs basic validation on SQL query
     */
    public boolean isValidQuery(String sql) {
        if (sql == null || sql.trim().isEmpty()) {
            return false;
        }

        // Basic validation: query should not be just whitespace and should have some content
        String trimmedSql = sql.trim();
        
        // Check for minimum length
        if (trimmedSql.length() < 3) {
            return false;
        }

        // Check for basic SQL statement patterns
        QueryType type = interpretQueryType(trimmedSql);
        return type != QueryType.UNKNOWN;
    }

    /**
     * Checks if the query is a data retrieval query (safe to execute for viewing)
     */
    public boolean isReadOnlyQuery(String sql) {
        QueryType type = interpretQueryType(sql);
        return type == QueryType.SELECT;
    }

    /**
     * Sanitizes SQL query by removing dangerous patterns
     * Basic protection against SQL injection
     */
    public String sanitizeQuery(String sql) {
        if (sql == null) {
            return "";
        }

        // Remove comments and potentially dangerous patterns
        String sanitized = sql.replaceAll("--.*", "")  // Remove single line comments
                             .replaceAll("/\\*.*?\\*/", "")  // Remove multi-line comments
                             .trim();

        return sanitized;
    }
}