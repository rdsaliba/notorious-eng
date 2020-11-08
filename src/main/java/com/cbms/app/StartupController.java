/**
 * This Controller is the first thing to run as the programm is started
 * It will setup the database and train the models
 *
 * @author      Paul Micu
 * @version     1.0
 * @last_edit   11/01/2020
 */
package com.cbms.app;

import com.cbms.rul.assessment.HealthAssesement;
import com.cbms.rul.models.LinearRegressionModelImpl;
import com.cbms.rul.models.ModelsController;
import com.cbms.preprocessing.DataPrePreprossesorController;
import com.cbms.source.local.Database;
import com.cbms.source.local.LocalDataSource;
import weka.classifiers.Classifier;
import weka.classifiers.functions.LinearRegression;
import weka.core.Instances;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.Remove;

import java.sql.SQLException;
import java.util.ArrayList;

//    TODO 1 - query the db and get all of the datasets that have a a train tag and query the model table to get all the models we want to train get a map dataset,model we want to train
//    TODO 2- get all the data for each of the datasets and convert them to ARFF, list of arff files with models to use on them
//    TODO 3- call preprossesorController and reduce data for all the datasets return as Instances objectP
//    TODO 4- give the reduced datasets to the model class selected and return the model for each dataset
//        models for each dataset (4)
//        return an model object

public class StartupController {
    private static StartupController instance = null;
    LocalDataSource localDataSource = null;
    Database db = new Database();

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
    public void initializer() throws SQLException {
        ArrayList<Integer> trainingSets = db.getTrainDatasets();

        for (Integer setID: trainingSets) {

        }

    }
    public Classifier generateModels() throws Exception {
        // todo get a list of instances that need training
        Instances originalData = LocalDataSource.loadTrainingData("Dataset/Converted/train_FD001_withRUL.arff");
        DataPrePreprossesorController dataPrePreprossesorController = DataPrePreprossesorController.getInstance();
        ModelsController modelsController = new ModelsController(new LinearRegressionModelImpl());

        Instances reducedData = dataPrePreprossesorController.reduceData(originalData);  // reduce data
        Instances testData = LocalDataSource.loadTrainingData("Dataset/Converted/engine2.arff");

        //To only remove data that 100% don't hold any valuable information use minimallyReducedData
        Instances minimallyReducedData = dataPrePreprossesorController.minimallyReduceData(originalData);

        Classifier trained = modelsController.trainModel(minimallyReducedData); // train model and return Classifier
        HealthAssesement healthAssesement = new HealthAssesement();
        Remove remove= dataPrePreprossesorController.getRemovedIndexList();
        testData = Filter.useFilter(testData, remove);
        double  results =  healthAssesement.predictRUL(testData,trained);

        System.out.println(results);

        System.out.println("trained");
        return trained;
    }
}
