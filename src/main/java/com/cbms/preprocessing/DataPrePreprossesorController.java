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

import weka.core.Attribute;
import weka.core.Instances;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.Remove;

import java.util.ArrayList;

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

    public Instances removeAttributes(Instances trainDataset, Instances testDataset) throws Exception {
        ArrayList<Integer> indexes = new ArrayList<>();
        for (int i = 0; i < testDataset.numAttributes(); i++) {
            if (!setContains(trainDataset, testDataset.attribute(i))) {
                indexes.add(i);
            }
        }
        int[] arr = new int[indexes.size()];

        for (int i = 0; i < indexes.size(); i++)
            arr[i] = indexes.get(i);

        Remove remove = new Remove();
        remove.setAttributeIndicesArray(arr);
        remove.setInputFormat(testDataset);
        return Filter.useFilter(testDataset, remove);
    }

    private boolean setContains(Instances dataset, Attribute att) {
        for (int i = 0; i < dataset.numAttributes(); i++) {
            if (att.name().equals(dataset.attribute(i).name())) {
                return true;
            }

        }
        return false;
    }
}
