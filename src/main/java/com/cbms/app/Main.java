package com.cbms.app;


import com.cbms.source.local.Database;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.ArrayList;


public class Main extends Application{

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("/SystemInfo.fxml"));
        Scene sample = new Scene(root);
        primaryStage.setTitle("CBMS");
        primaryStage.setScene(sample);
        primaryStage.show();
    }


    public static void main(String[] args) throws Exception {


        ModelController modelController = ModelController.getInstance();
        modelController.initializer();
        modelController.evaluate();





        //launch(args);
        //Classifier trainedModel;
       //StartupController startupController = new StartupController();
        //trainedModel= startupController.generateModels();


    }
}
