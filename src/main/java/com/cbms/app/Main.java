package com.cbms.app;


import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;


public class Main extends Application {

    public static void main(String[] args) throws Exception {


        ModelController modelController = ModelController.getInstance();
        modelController.initializer();
        modelController.estimate();

        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
<<<<<<< HEAD
        Parent root = FXMLLoader.load(getClass().getResource("/Systems.fxml"));
=======
        Parent root = FXMLLoader.load(getClass().getResource("/SystemInfo.fxml"));
>>>>>>> origin/#84_Backend_Connections
        Scene sample = new Scene(root);
        primaryStage.setTitle("CBMS");
        primaryStage.setScene(sample);
        primaryStage.show();
    }
}
