/**
 * This Controller is the first thing to run as the programm is started
 * It will setup the database and train the models
 *
 * @author      Paul Micu
 * @version     1.0
 * @last_edit   11/01/2020
 */
package com.cbms.app;

import com.cbms.RUL_Models.LinearRegressionModelImpl;
import com.cbms.RUL_Models.ModelsController;
import test.java.RUL_Models.testModel;
import com.cbms.preprocessing.DataPrePreprossesorController;
import com.cbms.source.local.LocalDataSource;
import weka.classifiers.Classifier;
import weka.core.Instances;
import java.io.File;
import java.io.FileReader;
import java.util.Scanner;

//    TODO 1 - query the db and get all of the datasets that have a a train tag and query the model table to get all the models we want to train get a map dataset,model we want to train
//    TODO 2- get all the data for each of the datasets and convert them to ARFF, list of arff files with models to use on them
//    TODO 3- call preprossesorController and reduce data for all the datasets return as Instances objectP
//    TODO 4- give the reduced datasets to the model class selected and return the model for each dataset
//        models for each dataset (4)
//        return an model object

public class StartupController {
    private static StartupController instance = null;
    LocalDataSource localDataSource = null;

    StartupController() throws Exception {
        localDataSource = new LocalDataSource();
    }

    public static StartupController getInstance() throws Exception {
        if (instance == null)
            instance = new StartupController();
        return instance;
    }

    /**
     * Work in progress
     *
     * @author Paul
     * */
    public Classifier generateModel() throws Exception {
        Instances testingData;
        FileReader file;
        Scanner user = new Scanner(System.in);
        System.out.println("Do you want to predict the value of RUL for one engine only? (yes/no)");
        String choice=user.nextLine();
        System.out.println("Which file do you want to train? (you can only train the first file)");
        String fileNum=user.nextLine();
        if(choice.equals("yes")){
        System.out.println("Which engine do you want to predict?");
        String engine=user.nextLine();
        testingData = LocalDataSource.loadTrainingData("Dataset/Converted/engine"+engine+".arff");
        file=new FileReader("Dataset/Real RUL/engine"+engine+".txt");
        }
        else{
            testingData = LocalDataSource.loadTrainingData("Dataset/Converted/test_FD00"+fileNum+"_withRUL.arff");
            file=new FileReader("Dataset/Real RUL/RUL_FD00"+fileNum+".txt");
        }

//        LocalDataSource.convertToArff(new File("Dataset/Train/train_FD0012.txt"),"train");
//        LocalDataSource.convertToArff(new File("Dataset/Test/test.txt"),"test");
        Instances originalData = LocalDataSource.loadTrainingData("Dataset/Converted/train_FD00"+fileNum+"_withRUL.arff");

        DataPrePreprossesorController dPPC = DataPrePreprossesorController.getInstance();
        ModelsController mC = new ModelsController(new LinearRegressionModelImpl());

        Instances reducedData = dPPC.reduceData(originalData);  // reduce data

        //To only remove data that 100% don't hold any valuable information use minimallyReducedData
        Instances minimallyReducedData = dPPC.minimallyReduceData(originalData);

        Classifier trained = mC.trainModel(originalData); // train model and return Classifier

        testModel tm = new testModel();
        tm.evaluateModel(trained,testingData, file );


        System.out.println("trained");
        return trained;
    }
}
