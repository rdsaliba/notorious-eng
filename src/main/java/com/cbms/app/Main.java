package com.cbms.app;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("/Systems.fxml"));
        Scene sample = new Scene(root);
        primaryStage.setTitle("CBMS");
        primaryStage.setScene(sample);
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
