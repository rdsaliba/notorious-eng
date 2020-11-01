package com.cbms.RUL_Model;

import weka.classifiers.functions.SMOreg;
import weka.core.Attribute;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ConverterUtils;

import java.io.FileReader;

public class EnginePerformance {
    public static double[] healthAssesment()throws Exception{
        ConverterUtils.DataSource testSource = new ConverterUtils.DataSource("Dataset/Converted/test_FD001_withRUL.arff");
        Instances testDataset = testSource.getDataSet();
        int totalEngines= (int) testDataset.lastInstance().value(testDataset.attribute("Engine_Num"));
        double[] enginePerformance = new double[totalEngines];
        double[] primeRUL= getPrimeRUL(testDataset, totalEngines, SMOreg lr);
        double[] lastRUL=testModel.predictRUL(testDataset, totalEngines,SMOreg lr);
        for(int i=0;i<primeRUL.length;i++){
            enginePerformance[i]=(primeRUL[i]-lastRUL[i]/primeRUL[i])*100;
        }
        return enginePerformance;
    }
    public static double[] getPrimeRUL(Instances testData, int totalEngines, SMOreg lr) throws Exception
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
