package test.java.com.project.sqlviz;

import com.project.sqlviz.db.ConnectionFactory;
import com.project.sqlviz.models.Connection;
import com.project.sqlviz.models.ConnectionConfig;
import com.project.sqlviz.models.QueryResult;
import com.project.sqlviz.services.QueryExecutor;
import com.project.sqlviz.services.SqlInterpreter;

/**
 * Test class to verify the implementation works correctly
 * This class demonstrates how to test the core functionality
 */
public class TestConnection {
    public static void main(String[] args) {
        System.out.println("=== SQL Visualizer Test Suite ===");
        
        // Test 1: SQL Interpreter
        testSqlInterpreter();
        
        // Test 2: Connection Factory
        testConnectionFactory();
        
        // Test 3: Query Execution (requires database)
        // testQueryExecution(); // Uncomment when you have a test database
        
        System.out.println("=== All Tests Completed ===");
    }
    
    private static void testSqlInterpreter() {
        System.out.println("\n--- Testing SQL Interpreter ---");
        
        SqlInterpreter interpreter = new SqlInterpreter();
        
        // Test query type detection
        String[] testQueries = {
            "SELECT * FROM users",
            "INSERT INTO users (name) VALUES ('John')",
            "UPDATE users SET name = 'Jane' WHERE id = 1",
            "DELETE FROM users WHERE id = 1",
            "CREATE TABLE test (id INT)",
            "DROP TABLE test",
            "INVALID QUERY",
            ""
        };
        
        for (String query : testQueries) {
            SqlInterpreter.QueryType type = interpreter.interpretQueryType(query);
            boolean isValid = interpreter.isValidQuery(query);
            boolean isReadOnly = interpreter.isReadOnlyQuery(query);
            
            System.out.printf("Query: '%s'\n", query.length() > 30 ? query.substring(0, 30) + "..." : query);
            System.out.printf("  Type: %s, Valid: %s, Read-only: %s\n", type, isValid, isReadOnly);
        }
    }
    
    private static void testConnectionFactory() {
        System.out.println("\n--- Testing Connection Factory ---");
        
        // Test MySQL connection configuration
        ConnectionConfig mysqlConfig = new ConnectionConfig(
            1,
            "Test MySQL Connection",
            "jdbc:mysql://localhost:3306/test",
            "testuser",
            "testpass",
            "com.mysql.cj.jdbc.Driver"
        );
        
        System.out.println("MySQL Config created: " + mysqlConfig);
        
        // Test PostgreSQL connection configuration
        ConnectionConfig postgresConfig = new ConnectionConfig(
            2,
            "Test PostgreSQL Connection",
            "jdbc:postgresql://localhost:5432/test",
            "testuser",
            "testpass",
            "org.postgresql.Driver"
        );
        
        System.out.println("PostgreSQL Config created: " + postgresConfig);
        
        // Note: Actual connection testing requires a real database
        System.out.println("Connection factory methods are available for testing with real databases");
    }
    
    // Uncomment this method when you have a test database set up
    /*
    private static void testQueryExecution() {
        System.out.println("\n--- Testing Query Execution ---");
        
        try {
            // Create connection config (update with your database details)
            ConnectionConfig config = new ConnectionConfig(
                1,
                "Test Connection",
                "jdbc:mysql://localhost:3306/VIT",
                "root",
                "1@mSHAKTI",
                "com.mysql.cj.jdbc.Driver"
            );
            
            // Create connection
            Connection conn = ConnectionFactory.createConnection(config);
            System.out.println("Connection created successfully");
            
            // Create query executor
            QueryExecutor executor = new QueryExecutor(conn);
            
            // Test simple query
            String testQuery = "SELECT 1 as test_column, 'Hello World' as message";
            QueryResult result = executor.executeQuery(testQuery);
            
            System.out.println("Query executed: " + result.getSqlQuery());
            System.out.println("Success: " + result.isSuccessful());
            System.out.println("Execution time: " + result.getExecutionTimeMs() + "ms");
            
            if (result.isSuccessful()) {
                System.out.println("Columns: " + result.getColumnNames());
                System.out.println("Row count: " + result.getRowCount());
                System.out.println("First row: " + (result.getRows().isEmpty() ? "No data" : result.getRows().get(0)));
            } else {
                System.out.println("Error: " + result.getErrorMessage());
            }
            
            // Close connection
            conn.close();
            System.out.println("Connection closed");
            
        } catch (Exception e) {
            System.out.println("Test failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
    */
}