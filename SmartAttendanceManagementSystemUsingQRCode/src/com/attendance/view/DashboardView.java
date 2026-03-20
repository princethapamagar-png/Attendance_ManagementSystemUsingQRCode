package com.attendance.view;

import com.attendance.dao.AttendanceDAO;
import com.attendance.util.QRGenerator;
import com.attendance.util.QRScanner;
import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamResolution;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import java.io.File;
import java.awt.image.BufferedImage;

public class DashboardView {
    private AttendanceDAO attendanceDAO = new AttendanceDAO();
    private ImageView qrView = new ImageView(); 

    public void show(String username, String role) {
        Stage stage = new Stage();
        VBox root = new VBox(20);
        root.setAlignment(Pos.TOP_CENTER);
        root.setPadding(new Insets(30));
        root.setStyle("-fx-background-color: radial-gradient(focus-angle 45deg, focus-distance 20%, center 50% 50%, radius 70%, #2c3e50, #000000);");

        Label titleLabel = new Label("ATTENDANCE DASHBOARD");
        titleLabel.setStyle("-fx-font-size: 28px; -fx-text-fill: white; -fx-font-weight: bold;");

        Label userDetail = new Label("User: " + username + " | Role: " + role);
        userDetail.setStyle("-fx-text-fill: #bdc3c7;");

        // --- Action Button ---
        Button mainActionBtn = new Button(role.equalsIgnoreCase("Admin") ? "GENERATE STUDENT QR" : "OPEN SCANNER");
        mainActionBtn.setStyle("-fx-background-color: " + (role.equalsIgnoreCase("Admin") ? "#3498db" : "#2ecc71") + 
                               "; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 20px;");
        mainActionBtn.setPrefSize(250, 45);

        // --- QR Preview (For Admin) ---
        qrView.setFitWidth(180);
        qrView.setFitHeight(180);
        qrView.setPreserveRatio(true);
        VBox qrContainer = new VBox(10, new Label("Last Generated QR:"), qrView);
        qrContainer.setAlignment(Pos.CENTER);
        qrContainer.setVisible(false);
        qrContainer.setStyle("-fx-text-fill: white;");

        // --- Attendance Table ---
        TableView<String[]> table = new TableView<>();
        table.setPrefHeight(300);
        table.setStyle("-fx-background-color: rgba(255,255,255,0.1);");

        TableColumn<String[], String> colName = new TableColumn<>("Name");
        colName.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue()[0]));
        
        TableColumn<String[], String> colStatus = new TableColumn<>("Status");
        colStatus.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue()[1]));

        TableColumn<String[], String> colTime = new TableColumn<>("Time");
        colTime.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue()[2]));

        table.getColumns().addAll(colName, colStatus, colTime);

        // --- Button Logic ---
        mainActionBtn.setOnAction(e -> {
            if (role.equalsIgnoreCase("Admin")) {
                TextInputDialog input = new TextInputDialog();
                input.setHeaderText("Enter Student Username to Generate QR");
                input.showAndWait().ifPresent(target -> {
                    String path = target + "_QR.png";
                    QRGenerator.generateQRCode(target, path);
                    qrView.setImage(new Image(new File(path).toURI().toString()));
                    qrContainer.setVisible(true);
                });
            } else {
                openWebcamScanner(username, table);
            }
        });

        Button logoutBtn = new Button("Logout");
        logoutBtn.setOnAction(e -> { stage.close(); new LoginView().start(new Stage()); });

        root.getChildren().addAll(titleLabel, userDetail, mainActionBtn, qrContainer, new Label("Attendance Logs"), table, logoutBtn);
        stage.setScene(new Scene(root, 700, 850));
        stage.show();
        
        // Load initial data
        table.setItems(FXCollections.observableArrayList(attendanceDAO.getLogs()));
    }

    private void openWebcamScanner(String studentUser, TableView<String[]> table) {
        Stage scannerStage = new Stage();
        Webcam webcam = Webcam.getDefault();
        if (webcam == null) {
            new Alert(Alert.AlertType.ERROR, "No Webcam Found!").show();
            return;
        }
        webcam.setViewSize(WebcamResolution.VGA.getSize());

        ImageView liveFeed = new ImageView();
        VBox layout = new VBox(10, new Label("Show your QR Code to the camera"), liveFeed);
        layout.setAlignment(Pos.CENTER);

        Thread thread = new Thread(() -> {
            webcam.open();
            while (webcam.isOpen()) {
                BufferedImage image = webcam.getImage();
                if (image != null) {
                    Platform.runLater(() -> liveFeed.setImage(SwingFXUtils.toFXImage(image, null)));
                    
                    String result = QRScanner.decodeQR(image);
                    if (result != null && result.equals(studentUser)) {
                        attendanceDAO.markAttendance(studentUser);
                        Platform.runLater(() -> {
                            webcam.close();
                            scannerStage.close();
                            table.setItems(FXCollections.observableArrayList(attendanceDAO.getLogs()));
                            new Alert(Alert.AlertType.INFORMATION, "Attendance Marked!").show();
                        });
                        break;
                    }
                }
            }
        });
        thread.setDaemon(true);
        thread.start();

        scannerStage.setScene(new Scene(layout, 650, 500));
        scannerStage.setOnCloseRequest(e -> webcam.close());
        scannerStage.show();
    }
}