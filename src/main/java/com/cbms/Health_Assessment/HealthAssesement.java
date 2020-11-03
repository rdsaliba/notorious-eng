package com.cbms.Health_Assessment;

import test.java.RUL_Models.testModel;
import weka.classifiers.Classifier;
import weka.classifiers.functions.LinearRegression;
import weka.core.Attribute;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ConverterUtils;

public class HealthAssesement {

    /**
     This function calculates the performance of each engine.
     The function it takes as a parameter the linear regression model.
     @author Talal
     */
    public static double[] healthAssesment(LinearRegression lr)throws Exception{
        ConverterUtils.DataSource testSource = new ConverterUtils.DataSource("Dataset/Converted/test_FD001_withRUL.arff");
        Instances testDataset = testSource.getDataSet();
        int totalEngines= (int) testDataset.lastInstance().value(testDataset.attribute("Engine_Num"));
        double[] enginePerformance = new double[totalEngines];
        double[] primeRUL= getPrimeRUL(testDataset, totalEngines, lr);
        double[] lastRUL= testModel.predictRUL(testDataset, totalEngines,lr);
        for(int i=0;i<primeRUL.length;i++){
            enginePerformance[i]=(primeRUL[i]-lastRUL[i]/primeRUL[i])*100;
        }
        return enginePerformance;
    }

    /**
     This function predicts the value of the RUL when the system is in it's prime.
     the function returns an array of the prime RULs of the systems.
     @author Talal
     */
    public static double[] getPrimeRUL(Instances testData, int totalEngines, Classifier lr) throws Exception
    {
        Attribute enginne = testData.attribute("Engine_Num");

        double engineNum = 1;
        double[] predicted = new double[totalEngines];
        for (int i = 0; i < testData.numInstances(); i++)
        {
            Instance row = testData.instance(i);
            Instance nextRow = testData.instance(i+1);
            if(row.value(enginne) ==engineNum){
                double engine=row.value(testData.attribute("Engine_Num"));
                // Instance one = testData.instance(i+1);
                double life= lr.classifyInstance(row);
                System.out.println(lr.classifyInstance(row));
                predicted[(int) engineNum] = life;
                engineNum++;
            }
        }
        return predicted;
    }
}
