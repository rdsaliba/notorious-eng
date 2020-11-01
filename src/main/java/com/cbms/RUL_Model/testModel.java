package com.cbms.RUL_Model;

import weka.core.Attribute;
import weka.core.Instance;
import weka.core.Instances;
import weka.classifiers.functions.SMOreg;
import weka.core.converters.ConverterUtils.DataSource;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;


public class testModel {
    public static void main(String[] args) throws Exception {
        SMOreg lr=(SMOreg) RulModel.trainModel();
        DataSource testSource = new DataSource("Dataset/Converted/test_FD001_withRUL.arff");
        FileReader file= new FileReader("Dataset/Real RUL/RUL_FD001.txt");
        Instances testDataset = testSource.getDataSet();
        double[] predictedRULs= predictRUL(testDataset, (int) testDataset.lastInstance().value(testDataset.attribute("Engine_Num")),lr);
        double[] realRULs = parseRULs(file);
        double rmse=RootMeanSquaredError.calculate(predictedRULs,realRULs);
        System.out.println("Root mean squared error is : "+rmse);
    }

    private static double[] parseRULs(FileReader file) {
        BufferedReader reader;
        double[] ruls= new double[100];
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

    protected static double[] predictRUL(Instances trainingData, int totalEngines, SMOreg lr) throws Exception
    {
        Attribute enginne = trainingData.attribute("Engine_Num");
        Attribute rul = trainingData.attribute("RUL");

        double engineNum = 0;
        double[] predicted = new double[totalEngines];
        for (int i = 0; i < trainingData.numInstances(); i++)
        {
            if(i+1==trainingData.numInstances()-1) {
                Instance one = trainingData.instance(i+1);
                double life= lr.classifyInstance(one);
                System.out.println(lr.classifyInstance(one));
                double temp = trainingData.lastInstance().value(enginne);
                predicted[(int) temp] = life;
                break;
            }
            Instance row = trainingData.instance(i);
            Instance nextRow = trainingData.instance(i+1);
            if(row.value(enginne) != nextRow.value(enginne)){
                double engine=row.value(trainingData.attribute("Engine_Num"));
                // Instance one = trainingData.instance(i+1);
                double life= lr.classifyInstance(row);
                System.out.println(lr.classifyInstance(row));
                predicted[(int) engineNum] = life;
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
}
