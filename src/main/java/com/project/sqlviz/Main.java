package com.project.sqlviz;

import com.project.sqlviz.gui.MainWindow;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;


/**
 * Main class to launch the SQL Visualizer application
 * This class serves as the entry point for the application
 */
public class Main {
    public static void main(String[] args) {
        System.out.println("Starting SQL Visualizer...");
        
        // Set system look and feel for better native appearance
        try {
            try {
                for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                    if ("Nimbus".equals(info.getName())) {
                        javax.swing.UIManager.setLookAndFeel(info.getClassName());
                        break;
                    }
                }
            } catch (Exception e) {
               
            }
        } catch (Exception e) {
            System.err.println("Warning: Could not set system look and feel");
        }

        // Launch GUI on Event Dispatch Thread (EDT)
        // This is the proper way to start Swing applications
        SwingUtilities.invokeLater(() -> {
            try {
                new MainWindow();
                System.out.println("SQL Visualizer GUI launched successfully");
            } catch (Exception e) {
                System.err.println("Error launching GUI: " + e.getMessage());
                e.printStackTrace();
            }
        });
    }
}