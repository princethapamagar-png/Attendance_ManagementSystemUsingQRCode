package com.attendance.view;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import java.sql.*;

import com.attendance.databse.DatabaseConnection;

public class LoginView extends Application {

    @Override
    public void start(Stage stage) {
        // --- 1. Background (Radial Gradient matching Register Page) ---
        VBox root = new VBox(25);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(50));
        root.setStyle("-fx-background-color: #1e3c72(focus-angle 45deg, focus-distance 20%, " +
                      "center 50% 50%, radius 70%, #8e44ad, #2c3e50);");

        // --- 2. Title ---
        Label titleLabel = new Label("Smart Attendance");
        titleLabel.setStyle("-fx-font-size: 36px; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-family: 'Arial';");

        Label subtitle = new Label("Please login to scan your ID");
        subtitle.setStyle("-fx-text-fill: rgba(255,255,255,0.7); -fx-font-size: 14px;");

        // --- 3. Input Fields ---
        TextField userField = new TextField();
        userField.setPromptText("Username");
        styleGlassInput(userField);

        PasswordField passField = new PasswordField();
        passField.setPromptText("Password");
        styleGlassInput(passField);

        // --- 4. Login Button ---
        Button loginBtn = new Button("LOGIN");
        loginBtn.setStyle("-fx-background-color: white; " +
                         "-fx-text-fill: #2c3e50; " +
                         "-fx-font-weight: bold; " +
                         "-fx-font-size: 16px; " +
                         "-fx-background-radius: 30px; " +
                         "-fx-min-width: 300px; " +
                         "-fx-min-height: 45px; " +
                         "-fx-cursor: hand;");

        // --- 5. Footer (Link to Register) ---
        Label noAccount = new Label("New student?");
        noAccount.setStyle("-fx-text-fill: white;");
        
        Hyperlink regLink = new Hyperlink("Create an Account");
        regLink.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-underline: false;");
        regLink.setOnAction(e -> {
            new RegisterView().start(new Stage());
            stage.close();
        });

        HBox footer = new HBox(5, noAccount, regLink);
        footer.setAlignment(Pos.CENTER);

        Label statusLabel = new Label();
        statusLabel.setStyle("-fx-text-fill: #ff4757; -fx-font-weight: bold;");

        // --- 6. Database Logic ---
        loginBtn.setOnAction(e -> {
            String query = "SELECT * FROM users WHERE username=? AND password=?";
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(query)) {
                
                stmt.setString(1, userField.getText());
                stmt.setString(2, passField.getText());
                
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    // Pass the username and role to the dashboard
                    new DashboardView().show(rs.getString("username"), rs.getString("role"));
                    stage.close();
                } else {
                    statusLabel.setText("Invalid Username or Password!");
                }
            } catch (SQLException ex) {
                statusLabel.setText("Database Connection Error!");
                ex.printStackTrace();
            }
        });

        root.getChildren().addAll(titleLabel, subtitle, userField, passField, loginBtn, footer, statusLabel);

        Scene scene = new Scene(root, 480, 650);
        stage.setTitle("Attendance System - Login");
        stage.setScene(scene);
        stage.show();
    }

    // Modern Rounded Glass Style helper
    private void styleGlassInput(TextField field) {
        field.setStyle("-fx-background-color: rgba(255, 255, 255, 0.1); " + 
                      "-fx-border-color: rgba(255, 255, 255, 0.4); " +
                      "-fx-border-radius: 25px; " +
                      "-fx-background-radius: 25px; " +
                      "-fx-text-fill: white; " +
                      "-fx-prompt-text-fill: #ccc; " +
                      "-fx-min-width: 320px; " +
                      "-fx-min-height: 48px; " +
                      "-fx-padding: 0 20 0 20;");
    }

    public static void main(String[] args) {
        launch(args);
    }
}