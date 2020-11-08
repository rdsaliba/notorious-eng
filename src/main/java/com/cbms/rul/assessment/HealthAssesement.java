package com.cbms.rul.assessment;

import weka.classifiers.Classifier;
import weka.classifiers.functions.LinearRegression;
import weka.core.Attribute;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ConverterUtils;

import static com.cbms.AppConstants.SYSOUT_DEBUG;
import static com.cbms.AppConstants.SYSTEM_NAME;

public class HealthAssesement {

   /*
     This function calculates the performance of each engine.
     The function it takes as a parameter the linear regression model.
     @author Talal

    public double[] healthAssesment(LinearRegression lr)throws Exception{
        ConverterUtils.DataSource testSource = new ConverterUtils.DataSource("Dataset/Converted/test_FD001_withRUL.arff");
        Instances testDataset = testSource.getDataSet();
        int totalEngines= (int) testDataset.lastInstance().value(testDataset.attribute(SYSTEM_NAME));
        double[] enginePerformance = new double[totalEngines];
        double[] primeRUL= getPrimeRUL(testDataset, totalEngines, lr);
        double[] lastRUL= predictRUL(testDataset, totalEngines,lr);
        for(int i=0;i<primeRUL.length;i++){
            enginePerformance[i]=(primeRUL[i]-lastRUL[i]/primeRUL[i])*100;
        }
        return enginePerformance;
    }
*/
    /**
     This function predicts the value of the RUL when the system is in it's prime.
     the function returns an array of the prime RULs of the systems.
     @author Talal
     */
    public double[] getPrimeRUL(Instances testData, int totalEngines, Classifier lr) throws Exception
    {
        Attribute engine = testData.attribute(SYSTEM_NAME);

        double engineNum = 1;
        double[] predicted = new double[totalEngines];
        for (int i = 0; i < testData.numInstances(); i++)
        {
            Instance row = testData.instance(i);
            if(row.value(engine) ==engineNum){
                double life= lr.classifyInstance(row);
                System.out.println(lr.classifyInstance(row));
                predicted[(int) engineNum] = life;
                engineNum++;
            }
        }
        return predicted;
    }

    /**
     This function loops through the test dataset and predicts the RULs for each system at the last instance.
     The parameters of the functions are the testing data set, the total number of engines in the dataset,
     and the linear regression model.
     The function returns an array with the predicted RUL for each system.
     @author Talal
     */
    public double[] predictRUL(Instances testData, int totalEngines, LinearRegression lr) throws Exception
    {
        Attribute enginne = testData.attribute(SYSTEM_NAME);

        double engineNum = 0;
        double[] predicted = new double[totalEngines];
        for (int i = 0; i < testData.numInstances(); i++)
        {
            if(i+1==testData.numInstances()-1) {
                Instance one = testData.instance(i+1);
                double life= lr.classifyInstance(one);
                if (SYSOUT_DEBUG)
                    System.out.println(lr.classifyInstance(one));
                double temp = testData.lastInstance().value(enginne);
                predicted[(int) temp-1] = life;
                break;
            }
            Instance row = testData.instance(i);
            Instance nextRow = testData.instance(i+1);
            if(row.value(enginne) != nextRow.value(enginne)){
                if (SYSOUT_DEBUG)
                    System.out.println("a b c d"+row.value(enginne));
                double engine=row.value(testData.attribute(SYSTEM_NAME));
                double life= lr.classifyInstance(row);
                if (SYSOUT_DEBUG)
                    System.out.println(lr.classifyInstance(row));
                predicted[(int) engineNum] = life;
                engineNum++;
            }
        }
        return predicted;
    }

    public double predictRUL(Instances testData, Classifier lr) throws Exception
    {
        Attribute engine = testData.attribute(SYSTEM_NAME);

        double engineNum = 0;
        double predicted=-1.0;
        for (int i = 0; i < testData.numInstances(); i++)
        {
            if(i+1==testData.numInstances()-1) {
                Instance one = testData.instance(i+1);
                double life= lr.classifyInstance(one);
                if (SYSOUT_DEBUG)
                    System.out.println(lr.classifyInstance(one));
                double temp = testData.lastInstance().value(engine);
                predicted = life;
                break;
            }
            Instance row = testData.instance(i);
            Instance nextRow = testData.instance(i+1);
            if(row.value(engine) != nextRow.value(engine)){
                if (SYSOUT_DEBUG)
                    System.out.println("a b c d"+row.value(engine));
                double life= lr.classifyInstance(row);
                if (SYSOUT_DEBUG)
                    System.out.println(lr.classifyInstance(row));
                predicted = life;
                engineNum++;
            }
        }
        return predicted;
    }
}
