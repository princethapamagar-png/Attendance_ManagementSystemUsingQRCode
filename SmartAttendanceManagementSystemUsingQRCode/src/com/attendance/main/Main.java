package com.attendance.main;

import com.attendance.view.LoginView;
import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        // This launches the Login window immediately
        LoginView login = new LoginView();
        try {
            login.start(primaryStage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        // This is the "Engine" that starts JavaFX
        launch(args);
    }
}