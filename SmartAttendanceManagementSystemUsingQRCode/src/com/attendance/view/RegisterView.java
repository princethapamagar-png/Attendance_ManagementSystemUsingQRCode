package com.attendance.view;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import java.sql.*;

import com.attendance.databse.DatabaseConnection;

public class RegisterView {

    public void start(Stage stage) {
        // --- 1. Background (Matching Radial Gradient) ---
        VBox root = new VBox(20);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(40));
        root.setStyle("-fx-background-color: #1e3c72(focus-angle 45deg, focus-distance 20%, center 50% 50%, radius 70%, #8e44ad, #2c3e50);");

        Label titleLabel = new Label("Student Registration");
        titleLabel.setStyle("-fx-font-size: 32px; -fx-text-fill: white; -fx-font-weight: bold;");

        // --- 2. Input Fields ---
        TextField fullNameField = new TextField();
        fullNameField.setPromptText("Full Name");
        styleGlassInput(fullNameField);

        TextField userField = new TextField();
        userField.setPromptText("Choose Username");
        styleGlassInput(userField);

        PasswordField passField = new PasswordField();
        passField.setPromptText("Password");
        styleGlassInput(passField);

        PasswordField confirmField = new PasswordField();
        confirmField.setPromptText("Confirm Password");
        styleGlassInput(confirmField);

        // --- 3. Role Selection (Student or Staff) ---
        ComboBox<String> roleBox = new ComboBox<>();
        roleBox.getItems().addAll("Student", "Staff");
        roleBox.setValue("Student");
        roleBox.setStyle("-fx-background-color: rgba(255,255,255,0.2); -fx-text-fill: white; -fx-background-radius: 20px;");
        roleBox.setMinWidth(320);

        // --- 4. Register Button ---
        Button regBtn = new Button("CREATE ACCOUNT");
        regBtn.setStyle("-fx-background-color: white; " +
                        "-fx-text-fill: #2c3e50; " +
                        "-fx-font-weight: bold; " +
                        "-fx-font-size: 15px; " +
                        "-fx-background-radius: 30px; " +
                        "-fx-min-width: 320px; " +
                        "-fx-min-height: 45px; " +
                        "-fx-cursor: hand;");

        // --- 5. Footer & Status ---
        Hyperlink loginLink = new Hyperlink("Already registered? Login here");
        loginLink.setStyle("-fx-text-fill: white; -fx-font-size: 12px;");
        loginLink.setOnAction(e -> {
            try {
                new LoginView().start(new Stage());
                stage.close();
            } catch (Exception ex) { ex.printStackTrace(); }
        });

        Label statusLabel = new Label();
        statusLabel.setStyle("-fx-text-fill: #ff4757;");

        // --- 6. Registration Logic ---
        regBtn.setOnAction(e -> {
            if (userField.getText().isEmpty() || passField.getText().isEmpty()) {
                statusLabel.setText("Please fill all fields!");
                return;
            }
            if (!passField.getText().equals(confirmField.getText())) {
                statusLabel.setText("Passwords do not match!");
                return;
            }

            String query = "INSERT INTO users (username, password, full_name, role) VALUES (?, ?, ?, ?)";
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, userField.getText());
                stmt.setString(2, passField.getText());
                stmt.setString(3, fullNameField.getText());
                stmt.setString(4, roleBox.getValue());
                
                stmt.executeUpdate();
                statusLabel.setStyle("-fx-text-fill: #2ed573;");
                statusLabel.setText("Registration Successful!");
            } catch (SQLException ex) {
                statusLabel.setText("Username already taken.");
            }
        });

        root.getChildren().addAll(titleLabel, fullNameField, userField, passField, confirmField, roleBox, regBtn, loginLink, statusLabel);

        Scene scene = new Scene(root, 480, 700);
        stage.setTitle("Attendance Registration");
        stage.setScene(scene);
        stage.show();
    }

    private void styleGlassInput(TextField field) {
        field.setStyle("-fx-background-color: rgba(255, 255, 255, 0.1); " +
                      "-fx-border-color: rgba(255, 255, 255, 0.5); " +
                      "-fx-border-radius: 25px; " +
                      "-fx-background-radius: 25px; " +
                      "-fx-text-fill: white; " +
                      "-fx-prompt-text-fill: #ddd; " +
                      "-fx-min-width: 320px; " +
                      "-fx-min-height: 45px; " +
                      "-fx-padding: 0 20 0 20;");
    }
}