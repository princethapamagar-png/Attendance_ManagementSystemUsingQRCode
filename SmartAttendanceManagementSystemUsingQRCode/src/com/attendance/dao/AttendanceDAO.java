package com.attendance.dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import com.attendance.databse.DatabaseConnection;

public class AttendanceDAO {

    // Method to mark attendance (used when QR is scanned)
    public boolean markAttendance(String username) {
        String query = "INSERT INTO attendance (user_id, status) SELECT id, 'Present' FROM users WHERE username = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, username);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Method to get attendance logs for the table
    public List<String[]> getLogs() {
        List<String[]> logs = new ArrayList<>();
        String query = "SELECT u.full_name, a.status, a.scan_time FROM attendance a " +
                       "JOIN users u ON a.user_id = u.id ORDER BY a.scan_time DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                logs.add(new String[]{
                    rs.getString("full_name"),
                    rs.getString("status"),
                    rs.getString("scan_time")
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return logs;
    }
}