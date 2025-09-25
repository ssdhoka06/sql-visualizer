package com.project.sqlviz.services;

import com.project.sqlviz.models.QueryResult;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service class for managing query history
 * This class maintains a history of executed queries in memory
 * In a real application, this could be persisted to a database
 */
public class QueryHistoryService {
    
    /**
     * Inner class to represent a query history entry
     */
    public static class QueryHistoryEntry {
        private int historyId;
        private int connId;
        private String sqlText;
        private LocalDateTime runAt;
        private long durationMs;
        private boolean wasSuccessful;
        private String errorMessage;
        private int rowCount;

        public QueryHistoryEntry(int historyId, int connId, String sqlText, LocalDateTime runAt, 
                               long durationMs, boolean wasSuccessful, String errorMessage, int rowCount) {
            this.historyId = historyId;
            this.connId = connId;
            this.sqlText = sqlText;
            this.runAt = runAt;
            this.durationMs = durationMs;
            this.wasSuccessful = wasSuccessful;
            this.errorMessage = errorMessage;
            this.rowCount = rowCount;
        }

        // Getters
        public int getHistoryId() { return historyId; }
        public int getConnId() { return connId; }
        public String getSqlText() { return sqlText; }
        public LocalDateTime getRunAt() { return runAt; }
        public long getDurationMs() { return durationMs; }
        public boolean wasSuccessful() { return wasSuccessful; }
        public String getErrorMessage() { return errorMessage; }
        public int getRowCount() { return rowCount; }

        @Override
        public String toString() {
            return String.format("[%s] %s (%dms, %d rows)", 
                runAt.toString(), 
                sqlText.length() > 50 ? sqlText.substring(0, 50) + "..." : sqlText,
                durationMs, 
                rowCount);
        }
    }

    private List<QueryHistoryEntry> history;
    private int nextHistoryId;

    public QueryHistoryService() {
        this.history = new ArrayList<>();
        this.nextHistoryId = 1;
    }

    /**
     * Adds a query result to history
     */
    public void addToHistory(int connId, QueryResult result) {
        QueryHistoryEntry entry = new QueryHistoryEntry(
            nextHistoryId++,
            connId,
            result.getSqlQuery(),
            result.getExecutedAt(),
            result.getExecutionTimeMs(),
            result.isSuccessful(),
            result.getErrorMessage(),
            result.isSuccessful() ? result.getRowCount() : 0
        );
        
        history.add(entry);
        
        // Keep only last 100 queries to prevent memory issues
        if (history.size() > 100) {
            history.remove(0);
        }
    }

    /**
     * Gets all query history entries
     */
    public List<QueryHistoryEntry> getAllHistory() {
        return new ArrayList<>(history); // Return copy to prevent external modification
    }

    /**
     * Gets query history for a specific connection
     */
    public List<QueryHistoryEntry> getHistoryForConnection(int connId) {
        return history.stream()
                     .filter(entry -> entry.getConnId() == connId)
                     .collect(Collectors.toList());
    }

    /**
     * Gets recent query history (last n queries)
     */
    public List<QueryHistoryEntry> getRecentHistory(int count) {
        int startIndex = Math.max(0, history.size() - count);
        return history.subList(startIndex, history.size());
    }

    /**
     * Searches query history by SQL text
     */
    public List<QueryHistoryEntry> searchHistory(String searchTerm) {
        return history.stream()
                     .filter(entry -> entry.getSqlText().toLowerCase().contains(searchTerm.toLowerCase()))
                     .collect(Collectors.toList());
    }

    /**
     * Clears all query history
     */
    public void clearHistory() {
        history.clear();
        nextHistoryId = 1;
    }

    /**
     * Gets the total number of queries in history
     */
    public int getHistoryCount() {
        return history.size();
    }

    /**
     * Gets a specific history entry by ID
     */
    public QueryHistoryEntry getHistoryEntry(int historyId) {
        return history.stream()
                     .filter(entry -> entry.getHistoryId() == historyId)
                     .findFirst()
                     .orElse(null);
    }
}