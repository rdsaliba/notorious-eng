package com.cbms.app;


import com.cbms.source.local.Database;
import com.cbms.source.local.DatabaseConnection;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import weka.classifiers.Classifier;

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


        StartupController startupController = new StartupController();
        startupController.initializer();

        //  launch(args);

        Database db = new Database();

        ArrayList<Integer> datasets = db.getTrainDatasets();
        for (Integer set: datasets
             ) {
            System.out.println(set.toString());
        }
        db.createInstances(1);

        //launch(args);
        //Classifier trainedModel;
       //StartupController startupController = new StartupController();
        //trainedModel= startupController.generateModels();


    }
}
