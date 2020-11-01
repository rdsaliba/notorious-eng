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
    public static Classifier trainModel() throws Exception {
        DataSource trainSource = new DataSource("Dataset/Converted/train_FD001_withRUL.arff");
        DataSource testSource = new DataSource("Dataset/Converted/test_FD001_withRUL.arff");
        Instances firstTrain = trainSource.getDataSet();
        Instances testDataset = testSource.getDataSet();
        firstTrain.setClassIndex(firstTrain.numAttributes()-1);
        testDataset.setClassIndex(testDataset.numAttributes()-1);
        Instances trainDataset = removeInstances(firstTrain);
        SMOreg lr=new SMOreg();
        lr.buildClassifier(trainDataset);
        return lr;
    }

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


