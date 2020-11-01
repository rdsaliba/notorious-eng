package com.cbms.app;

import com.cbms.preprocessing.DataPrePreprossesorController;
import com.cbms.preprocessing.PrePro2_Fill_RUL_Col;
import weka.core.Instances;

public class StartupController {
    private static StartupController instance = null;

    private StartupController() throws Exception { }

    public static StartupController getInstance() throws Exception {
        if (instance == null)
            instance = new StartupController();
        return instance;
    }

//    1 - query the db and get all of the datasets that have a a train tag and query the model table to get all the models we want to train
//    get a map dataset,model we want to train
//    2- get all the data for each of the datasets and convert them to ARFF
//    list of arff files with models to use on them
//    3- call preprossesorController and reduce data for all the datasets
//    return as Instances objectP
    Instances originalData=   PrePro2_Fill_RUL_Col.loadTrainingData();
    DataPrePreprossesorController dPPC = DataPrePreprossesorController.getInstance();

    Instances reducedData = dPPC.reduceData(originalData);



//    4- give the reduced datasets to the model class selected and return the model for each dataset
//        models for each dataset (4)
//        return an model object
//
//    5- use the model to estimate the RUL
//
//    6- save the rul in the db



}
