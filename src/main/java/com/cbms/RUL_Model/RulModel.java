package com.cbms.RUL_Model;

import weka.core.Attribute;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;
import weka.classifiers.functions.SMOreg;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import java.io.FileReader;

public class RulModel {
    /**
     This function takes the filtered training dataset and trains a linear regression regression model,
     after that it returns the model.
     To use this method you need to pass the training dataset.
     @author Talal
     */
    public static Classifier trainModel(Instances firstTrain) throws Exception {
        firstTrain.setClassIndex(firstTrain.numAttributes()-1);
        Instances trainDataset = removeInstances(firstTrain);
        SMOreg lr=new SMOreg();
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
    private static Instances removeInstances (Instances trainDataset) {
        Attribute rul = trainDataset.attribute("RUL");
        for (int i = 0; i < trainDataset.numInstances() ; i++) {
            Instance inst = trainDataset.instance(i);
            if (inst.value(inst.classAttribute())>175) {
                trainDataset.delete(i);
            }
        }
        return trainDataset;
    }
}


