/**
 * This Controller will handle the preprocessing of the data.
 * it requires the dataset to reduce in an Instances object
 * it will return an Instances object with the attributes and their corresponding data selected by the algorithm removed
 *
 * @author Paul Micu
 * @version 1.0
 * @last_edit 11/01/2020
 */
package com.cbms.preprocessing;

import weka.core.Instances;

public class DataPrePreprossesorController {
    private static DataPrePreprossesorController instance = null;
    private DataPreProcessorImpl dataPreProcessorImpl = null;

    private DataPrePreprossesorController() {
    }

    public static DataPrePreprossesorController getInstance() {
        if (instance == null)
            instance = new DataPrePreprossesorController();
        return instance;
    }


    public Instances reduceData(Instances originalData) throws Exception {
        dataPreProcessorImpl = new DataPreProcessorImpl(originalData);
        dataPreProcessorImpl.processFullReduction();
        return dataPreProcessorImpl.getReducedDataset();
    }

    public Instances minimallyReduceData(Instances originalData) throws Exception {
        dataPreProcessorImpl = new DataPreProcessorImpl(originalData);
        dataPreProcessorImpl.processMinimalReduction();
        return dataPreProcessorImpl.getMinimallyReducedDataset();
    }

    public Instances addRULCol(Instances toADD) throws Exception {
        return dataPreProcessorImpl.addRULCol(toADD);
    }
}
