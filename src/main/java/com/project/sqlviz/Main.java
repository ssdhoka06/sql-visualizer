package com.project.sqlviz;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Main {
    public static void main(String[] args) {
        String url = "jdbc:mysql://localhost:3306/VIT";  
        String user = "root";                              
        String password = "1@mSHAKTI";                      
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
          
            Connection conn = DriverManager.getConnection(url, user, password);
            System.out.println("Connection successful!");

            conn.close();
        } catch (ClassNotFoundException e) {
            System.out.println("Driver not found: " + e.getMessage());
        } catch (SQLException e) {
            System.out.println("SQL Error: " + e.getMessage());
        }
    }
}
