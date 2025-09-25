package com.project.sqlviz.gui;

import com.project.sqlviz.models.Connection;
import com.project.sqlviz.models.ConnectionConfig;
import com.project.sqlviz.models.QueryResult;
import com.project.sqlviz.db.ConnectionFactory;
import com.project.sqlviz.services.QueryExecutor;
import com.project.sqlviz.services.QueryHistoryService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.List;

/**
 * Main GUI window for the SQL Visualizer application
 * This class demonstrates Swing GUI components and MVC pattern
 */
public class MainWindow extends JFrame {
    
    // GUI Components
    private JTextArea sqlEditor;           // Text area for SQL input
    private JTable resultTable;           // Table to display query results
    private DefaultTableModel tableModel; // Model for the result table
    private JButton executeButton;        // Button to execute queries
    private JButton connectButton;        // Button to connect to database
    private JLabel statusLabel;           // Status bar
    private JTextField connectionUrlField; // Connection URL input
    private JTextField usernameField;     // Username input
    private JPasswordField passwordField; // Password input
    
    // Business Logic Components
    private Connection currentConnection;
    private QueryExecutor queryExecutor;
    private QueryHistoryService historyService;

    public MainWindow() {
        // Initialize services
        historyService = new QueryHistoryService();
        
        // Set up the main window
        initializeWindow();
        
        // Create and arrange GUI components
        initializeComponents();
        
        // Set up event handlers
        setupEventHandlers();
        
        // Make window visible
        setVisible(true);
    }

    /**
     * Initialize main window properties
     */
    private void initializeWindow() {
        setTitle("SQL Visualizer - Interactive Query Tool");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 700);
        setLocationRelativeTo(null); // Center the window
    }

    /**
     * Create and arrange all GUI components
     * This method demonstrates Swing layout management
     */
    private void initializeComponents() {
        // Set layout manager
        setLayout(new BorderLayout());

        // Create top panel for connection controls
        JPanel connectionPanel = createConnectionPanel();
        add(connectionPanel, BorderLayout.NORTH);

        // Create main content area with split pane
        JSplitPane mainSplitPane = createMainContentArea();
        add(mainSplitPane, BorderLayout.CENTER);

        // Create status bar
        statusLabel = new JLabel("Ready - Please connect to a database");
        statusLabel.setBorder(BorderFactory.createLoweredBevelBorder());
        add(statusLabel, BorderLayout.SOUTH);
    }

    /**
     * Creates the connection panel with database connection controls
     */
    private JPanel createConnectionPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.setBorder(BorderFactory.createTitledBorder("Database Connection"));

        // Connection URL field
        panel.add(new JLabel("URL:"));
        connectionUrlField = new JTextField("jdbc:mysql://localhost:3306/VIT", 20);
        panel.add(connectionUrlField);

        // Username field
        panel.add(new JLabel("Username:"));
        usernameField = new JTextField("root", 10);
        panel.add(usernameField);

        // Password field
        panel.add(new JLabel("Password:"));
        passwordField = new JPasswordField(10);
        panel.add(passwordField);

        // Connect button
        connectButton = new JButton("Connect");
        panel.add(connectButton);

        return panel;
    }

    /**
     * Creates the main content area with SQL editor and results table
     */
    private JSplitPane createMainContentArea() {
        // Create SQL editor panel
        JPanel editorPanel = createSqlEditorPanel();
        
        // Create results panel
        JPanel resultsPanel = createResultsPanel();

        // Create split pane with editor on left, results on right
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, editorPanel, resultsPanel);
        splitPane.setDividerLocation(400); // Set initial divider position
        splitPane.setResizeWeight(0.4); // Give 40% space to editor, 60% to results

        return splitPane;
    }

    /**
     * Creates the SQL editor panel
     */
    private JPanel createSqlEditorPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("SQL Query Editor"));

        // Create text area for SQL input
        sqlEditor = new JTextArea();
        sqlEditor.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12)); // Monospaced font for code
        sqlEditor.setText("-- Enter your SQL query here\nSELECT * FROM your_table_name LIMIT 10;");
        
        // Wrap in scroll pane
        JScrollPane editorScrollPane = new JScrollPane(sqlEditor);
        editorScrollPane.setPreferredSize(new Dimension(380, 400));
        panel.add(editorScrollPane, BorderLayout.CENTER);

        // Create button panel
        JPanel buttonPanel = new JPanel(new FlowLayout());
        executeButton = new JButton("Execute Query");
        executeButton.setEnabled(false); // Disabled until connected
        buttonPanel.add(executeButton);

        JButton clearButton = new JButton("Clear");
        clearButton.addActionListener(e -> sqlEditor.setText(""));
        buttonPanel.add(clearButton);

        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    /**
     * Creates the results display panel
     */
    private JPanel createResultsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Query Results"));

        // Create table for displaying results
        tableModel = new DefaultTableModel();
        resultTable = new JTable(tableModel);
        resultTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF); // Allow horizontal scrolling
        resultTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Wrap table in scroll pane
        JScrollPane tableScrollPane = new JScrollPane(resultTable);
        tableScrollPane.setPreferredSize(new Dimension(580, 400));
        panel.add(tableScrollPane, BorderLayout.CENTER);

        // Create info panel for query statistics
        JPanel infoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.add(infoPanel, BorderLayout.SOUTH);

        return panel;
    }

    /**
     * Set up event handlers for GUI components
     * This demonstrates event-driven programming in Swing
     */
    private void setupEventHandlers() {
        // Connect button event handler
        connectButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                connectToDatabase();
            }
        });

        // Execute button event handler
        executeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                executeQuery();
            }
        });

        // Add keyboard shortcut for query execution (Ctrl+Enter)
        InputMap inputMap = sqlEditor.getInputMap(JComponent.WHEN_FOCUSED);
        ActionMap actionMap = sqlEditor.getActionMap();
        
        inputMap.put(KeyStroke.getKeyStroke("ctrl ENTER"), "executeQuery");
        actionMap.put("executeQuery", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (executeButton.isEnabled()) {
                    executeQuery();
                }
            }
        });
    }

    /**
     * Handles database connection
     * This method demonstrates exception handling and user feedback
     */
    private void connectToDatabase() {
        try {
            // Update UI to show connection in progress
            connectButton.setEnabled(false);
            statusLabel.setText("Connecting to database...");
            setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

            // Get connection parameters from UI
            String url = connectionUrlField.getText().trim();
            String username = usernameField.getText().trim();
            String password = new String(passwordField.getPassword());

            // Validate input
            if (url.isEmpty() || username.isEmpty()) {
                throw new IllegalArgumentException("URL and username are required");
            }

            // Create connection configuration
            ConnectionConfig config = new ConnectionConfig(
                1, // Connection ID
                "Main Connection", // Name
                url, // JDBC URL
                username, // Username
                password, // Password
                "com.mysql.cj.jdbc.Driver" // Driver class - assuming MySQL for now
            );

            // Create connection using factory
            currentConnection = ConnectionFactory.createConnection(config);
            
            // Initialize query executor
            queryExecutor = new QueryExecutor(currentConnection);

            // Update UI to show successful connection
            statusLabel.setText("Connected to: " + url);
            executeButton.setEnabled(true);
            connectButton.setText("Disconnect");
            
            // Show success message
            JOptionPane.showMessageDialog(this, 
                "Successfully connected to database!", 
                "Connection Successful", 
                JOptionPane.INFORMATION_MESSAGE);

        } catch (SQLException e) {
            // Handle database connection errors
            String errorMsg = "Failed to connect to database: " + e.getMessage();
            statusLabel.setText("Connection failed");
            JOptionPane.showMessageDialog(this, errorMsg, "Connection Error", JOptionPane.ERROR_MESSAGE);
            
        } catch (IllegalArgumentException e) {
            // Handle validation errors
            statusLabel.setText("Invalid connection parameters");
            JOptionPane.showMessageDialog(this, e.getMessage(), "Input Error", JOptionPane.WARNING_MESSAGE);
            
        } catch (Exception e) {
            // Handle unexpected errors
            String errorMsg = "Unexpected error: " + e.getMessage();
            statusLabel.setText("Connection failed");
            JOptionPane.showMessageDialog(this, errorMsg, "Error", JOptionPane.ERROR_MESSAGE);
            
        } finally {
            // Always restore UI state
            connectButton.setEnabled(true);
            setCursor(Cursor.getDefaultCursor());
        }
    }

    /**
     * Handles query execution
     * This method demonstrates MVC separation - UI calls service layer
     */
    private void executeQuery() {
        if (currentConnection == null || queryExecutor == null) {
            JOptionPane.showMessageDialog(this, 
                "Please connect to a database first", 
                "No Connection", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        String sql = sqlEditor.getText().trim();
        if (sql.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Please enter a SQL query", 
                "Empty Query", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            // Update UI to show query execution in progress
            executeButton.setEnabled(false);
            statusLabel.setText("Executing query...");
            setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

            // Clear previous results
            tableModel.setRowCount(0);
            tableModel.setColumnCount(0);

            // Execute query
            QueryResult result = queryExecutor.executeQuery(sql);
            
            // Add to history
            historyService.addToHistory(currentConnection.getConfig().getConnId(), result);

            // Display results
            displayQueryResult(result);

        } catch (Exception e) {
            String errorMsg = "Error executing query: " + e.getMessage();
            statusLabel.setText("Query execution failed");
            JOptionPane.showMessageDialog(this, errorMsg, "Execution Error", JOptionPane.ERROR_MESSAGE);
            
        } finally {
            // Always restore UI state
            executeButton.setEnabled(true);
            setCursor(Cursor.getDefaultCursor());
        }
    }

    /**
     * Displays query results in the results table
     * This method handles both successful and failed query results
     */
    private void displayQueryResult(QueryResult result) {
        if (result.isSuccessful()) {
            // Display successful results
            List<String> columnNames = result.getColumnNames();
            List<List<Object>> rows = result.getRows();

            // Set up table columns
            for (String columnName : columnNames) {
                tableModel.addColumn(columnName);
            }

            // Add data rows
            for (List<Object> row : rows) {
                Object[] rowArray = row.toArray();
                tableModel.addRow(rowArray);
            }

            // Update status
            statusLabel.setText(String.format("Query executed successfully - %d rows returned in %dms", 
                result.getRowCount(), result.getExecutionTimeMs()));

            // Auto-resize columns to fit content
            resizeTableColumns();

        } else {
            // Display error results
            tableModel.addColumn("Error");
            tableModel.addRow(new Object[]{result.getErrorMessage()});
            
            statusLabel.setText(String.format("Query failed in %dms", result.getExecutionTimeMs()));
            
            JOptionPane.showMessageDialog(this, 
                "Query execution failed:\n" + result.getErrorMessage(), 
                "Query Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Automatically resize table columns to fit content
     */
    private void resizeTableColumns() {
        for (int column = 0; column < resultTable.getColumnCount(); column++) {
            int maxWidth = 0;
            
            // Check header width
            int headerWidth = resultTable.getTableHeader()
                .getFontMetrics(resultTable.getTableHeader().getFont())
                .stringWidth(resultTable.getColumnName(column));
            maxWidth = Math.max(maxWidth, headerWidth);

            // Check cell content width (sample first 10 rows for performance)
            int rowsToCheck = Math.min(10, resultTable.getRowCount());
            for (int row = 0; row < rowsToCheck; row++) {
                Object value = resultTable.getValueAt(row, column);
                if (value != null) {
                    int cellWidth = resultTable.getFontMetrics(resultTable.getFont())
                        .stringWidth(value.toString());
                    maxWidth = Math.max(maxWidth, cellWidth);
                }
            }

            // Set column width with some padding, but cap at 300px
            int columnWidth = Math.min(maxWidth + 20, 300);
            resultTable.getColumnModel().getColumn(column).setPreferredWidth(columnWidth);
        }
    }

    /**
     * Main method to start the application
     */
    public static void main(String[] args) {
        // Set look and feel to system default
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeel());
        } catch (Exception e) {
            // If system L&F fails, use default
            System.err.println("Could not set system look and feel: " + e.getMessage());
        }

        // Create and show GUI on Event Dispatch Thread
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new MainWindow();
            }
        });
    }
}
