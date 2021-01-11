package com.cbms.app;


import com.cbms.source.local.AssetDAOImpl;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.Timer;
import java.util.TimerTask;


public class Main extends Application {
    private static int time=1;
    private static boolean realTime=true;
    public static void main(String[] args) {

        ModelController modelController = ModelController.getInstance();

        if (realTime){
            AssetDAOImpl assetDAO = new AssetDAOImpl();
            assetDAO.resetAssetForLive();
            System.out.println("timer start");
            Timer timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    time++;
                    System.out.println(time);
                    if (time >= 50){
                        time = 10000;
                        timer.cancel();
                    }
                }
            }, 0, 1000);
        }
        modelController.initializer();
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/Systems.fxml"));
        Scene sample = new Scene(root);
        primaryStage.setTitle("CBMS");
       // primaryStage.initStyle(StageStyle.UNDECORATED);
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

    public static boolean isRealTime(){
        return realTime;
    }

    public static int getTime(){
        return time;
    }
}
