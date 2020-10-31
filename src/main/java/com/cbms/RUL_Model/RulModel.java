package com.cbms.RUL_Model;

import weka.core.Attribute;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;
import weka.classifiers.functions.LinearRegression;
import weka.classifiers.functions.SMOreg;
import weka.classifiers.meta.AdaBoostM1;
import weka.classifiers.meta.Bagging;
import weka.classifiers.meta.Stacking;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import weka.filters.Filter;
import weka.filters.unsupervised.attribute.Normalize;
import weka.filters.unsupervised.attribute.Standardize;

//import java.io.BufferedReader;
//import java.io.File;
//import java.io.FileNotFoundException;
//import  java.io.FileReader;
//import java.io.IOException;
public class RulModel {
    public static void main(String[] args) throws Exception {
        DataSource trainSource = new DataSource("Dataset/Converted/train_FD001_withRUL.arff");
        DataSource testSource = new DataSource("Dataset/Converted/test_FD001_withRUL.arff");
        FileReader file= new FileReader("Dataset/Real RUL/RUL_FD001.txt");
        Instances trainDataset = trainSource.getDataSet();
        Instances testDataset = testSource.getDataSet();
        trainDataset.setClassIndex(trainDataset.numAttributes()-1);
        testDataset.setClassIndex(testDataset.numAttributes()-1);
        //SMOreg lr=new SMOreg();
        LinearRegression lr = new LinearRegression();
        lr.buildClassifier(trainDataset);
        Evaluation ev =new Evaluation(trainDataset);
        ev.evaluateModel(lr, testDataset);
        double a=  testDataset.instance(30).value(2);
        System.out.println(a);
        Instance one = testDataset.instance(30);
        double life= lr.classifyInstance(one);
        double[] predictedRULs=getMaxRUL(testDataset,100,lr);
        System.out.println("---"+life);
        showPredictedRUL(predictedRULs);
        System.out.println("asdf"+predictedRULs[0]);
        int[] realRULs = parseRULs(file);
        double[] errors= getErrors(predictedRULs,realRULs);
        showErrors(errors);

    }

    private static void removeInstances (Instances trainDataset) {
        Attribute rul = trainDataset.attribute("RUL");
        for (int i = trainDataset.numInstances() - 1; i >= 0; i--) {
            Instance inst = trainDataset.instance(i);
            if (inst.value(rul)>175) {
                trainDataset.delete(i);
            }
        }
    }

    private static double[] getErrors(double[] predictedRuls, int[] realRuls) {
        double[] errors=new double[100];
        for(int i=0;i<realRuls.length;i++) {
            double error= (predictedRuls[i] - realRuls[i]) / realRuls[i];
            errors[i]=error*100;
        }

        return errors;
    }

    private static int[] parseRULs(FileReader file) {
        BufferedReader reader;
        int[] ruls= new int[100];
        try {
            reader = new BufferedReader(file);
            String line= reader.readLine();
            int i=0;
            while(line !=null) {
                System.out.println(i+" "+ line);
                ruls[i]=Integer.parseInt(line.replaceAll("\\s+",""));
                i++;
                line=reader.readLine();
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ruls;
    }

    private static double[] getMaxRUL(Instances trainingData, int totalEngines, LinearRegression  lr) throws Exception
    {
        Attribute time = trainingData.attribute("Engine_Num");
        Attribute rul = trainingData.attribute("RUL");

        double engineNum = 1;
        double[] predicted = new double[totalEngines];
        for (int i = 0; i < trainingData.numInstances(); i++)
        {
            if(i==trainingData.numInstances()-2) {
                Instance one = trainingData.instance(i+1);
                double life= lr.classifyInstance(one);
                predicted[99] = life;
                break;
            }
            Instance row = trainingData.instance(i+1);
            Instance nextRow = trainingData.instance(i+2);
            if(row.value(time) != nextRow.value(time)){
                double engine=row.value(trainingData.attribute("Engine_Num"));
                Instance one = trainingData.instance(i);
                double life= lr.classifyInstance(one);
                predicted[(int) engine] = life;
                engineNum++;
            }

        }
        return predicted;
    }


    private static void showPredictedRUL(double[] max)
    {
        int engineNum = 1;

        for(double e: max)
        {
            System.out.println("Predicted RUL for " + (engineNum++) + ": " + e);
        }
    }

    private static void showErrors(double[] errors)
    {
        int engineNum = 1;

        for(double e: errors)
        {
            System.out.println("Error for " + (engineNum++) + ": " + e);
        }
    }

//private static Instances addHI(Instances trainingData, double[] maxRUL)
//{
//    int engineNum = 1;
//    Attribute engine = trainingData.attribute("Engine_Num");
//    Instance row;
//
//    int timeCycleIndex = 1;
//
//    for(int i = 0; i < trainingData.numInstances(); i++)
//    {
//        row = trainingData.instance(i);
//
//        if(row.value(engine) != engineNum)
//        {
//            engineNum++;
//            timeCycleIndex = 1;
//        }
//        trainingData.
//        double hiValue = row.value(trainingData.numAttributes()-1)/maxRUL[engineNum - 1];
//        row.setValue("HI", hiValue);
//        row.setClassValue((maxRUL[engineNum - 1] - timeCycleIndex++));
//    }
}


