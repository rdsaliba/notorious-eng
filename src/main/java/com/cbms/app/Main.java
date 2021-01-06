package com.cbms.app;


import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;


public class Main extends Application {

    public static void main(String[] args) {
        ModelController modelController = ModelController.getInstance();
        modelController.initializer();
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/Systems.fxml"));
        Scene sample = new Scene(root);
        primaryStage.setTitle("CBMS");
        primaryStage.initStyle(StageStyle.UNDECORATED);
        primaryStage.setScene(sample);
        primaryStage.show();
        primaryStage.setOnCloseRequest(e -> {
            try {
                closeProgram();
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        });
    }

    private void closeProgram() throws Exception {
        System.out.println("Program closing.");
        Platform.exit();
        System.exit(0);
    }
}
