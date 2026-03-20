package com.attendance.databse;

import java.sql.Connection;
import java.sql.DriverManager;

public class DatabaseConnection {
    public static Connection getConnection() {
        try {
            String url = "jdbc:mysql://localhost:3306/SmartAttendance";
            String user = "root";
            String password = "tatotunturi_1919"; 
            return DriverManager.getConnection(url, user, password);
        } catch (Exception e) {
            System.out.println("Connection Failed!");
            return null;
        }
    }
}