/**
 * Implementation of the model strategy interface, part of the Strategy design pattern
 * Contains 2 methods, one to train the data and another private function to remove instances that are too old
 * and cannot provide useful information
 *
 * @author      Paul Micu
 * @version     1.0
 * @last_edit   11/01/2020
 */
package com.cbms.RUL_Models;

import weka.classifiers.functions.LinearRegression;
import weka.core.Attribute;
import weka.core.Instance;
import weka.core.Instances;
import weka.classifiers.functions.SMOreg;
import weka.classifiers.Classifier;

public class LinearRegressionModelImpl implements ModelStrategy {
    /**
     This function takes the filtered training dataset and trains a linear regression regression model,
     after that it returns the model.
     To use this method you need to pass the training dataset.
     @author Talal
     */
    @Override
    public Classifier trainModel(Instances firstTrain) throws Exception {
        firstTrain.setClassIndex(firstTrain.numAttributes()-1);
        Instances trainDataset = removeInstances(firstTrain);
        LinearRegression lr=new LinearRegression();
        lr.buildClassifier(trainDataset);
        return lr;
    }

    /**
     This function removes the outliers (data that can affect the prediction of the model)
     from a training dataset.
     After outliers are removed, new dataset is returned.
     This function is used to improve the performance of a model.
     To use this function you need to pass the training dataset as a parameter.
     @author Talal
     */
    private Instances removeInstances (Instances trainDataset) {
        for (int i = 0; i < trainDataset.numInstances() ; i++) {
            Instance inst = trainDataset.instance(i);
            if (inst.value(inst.classAttribute())>150) {
                trainDataset.delete(i);
            }
        }
        return trainDataset;
    }
}


