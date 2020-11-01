package com.cbms.preprocessing;

import weka.core.Instances;

public class DataPrePreprossesorController {
    private static DataPrePreprossesorController instance = null;
    private  DataPreProcessorImpl dataPreProcessorImpl = null;

    private DataPrePreprossesorController() { }

    public static DataPrePreprossesorController getInstance(){
        if (instance == null)
            instance = new DataPrePreprossesorController();
        return instance;
    }

    public Instances reduceData(Instances originalData) {
        dataPreProcessorImpl = new DataPreProcessorImpl(originalData);
        dataPreProcessorImpl.process();
        return dataPreProcessorImpl.getReducedDataset();
    }
}
