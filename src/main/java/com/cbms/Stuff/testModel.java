package com.cbms.Stuff;

import com.cbms.RUL_Models.LinearRegressionModelImpl;
import weka.classifiers.Classifier;
import weka.core.Attribute;
import weka.core.Instance;
import weka.core.Instances;
import weka.classifiers.functions.SMOreg;
import weka.core.converters.ConverterUtils.DataSource;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;


public class testModel {
    public testModel() {
    }

    /**
     This function evaluates the performance of an Artificial intellignece model.
     The parameters of the function are a training dataset, test dataset, and a text file that contains the real RULs.
     The function prints the root mean squared error of a model.
     @author Talal
     */
    public void evaluateModel(Classifier model, Instances testDataset) throws Exception {
      //  LinearRegressionModelImpl linearRegressionModel = new LinearRegressionModelImpl();
        SMOreg lr=(SMOreg) model;//linearRegressionModel.trainModel(trainDataset);
    //    DataSource testSource = new DataSource("Dataset/Converted/test_FD001_withRUL.arff");
        double[] predictedRULs= predictRUL(testDataset, (int) testDataset.lastInstance().value(testDataset.attribute("Engine_Num")),lr);
        double[] realRULs = parseRULs(new FileReader("C:\\Git\\notorious-eng\\Dataset/Real RUL/RUL_FD0012.txt"));
        double rmse= RootMeanSquaredError.calculate(predictedRULs,realRULs);
        System.out.println("Root mean squared error is : "+rmse);
    }

    /**
     This function reads a text file and stores the ruls available in an array.
     The parameter of the function is the text file with the real RULs.
     the function returns the array with the real RULs.
     @author Talal
     TODO remove the 100 and make it dynamic
     */
    private double[] parseRULs(FileReader file) {
        return new double[]{112.0}; // for testing purposes, waiting on the fix to remove - Paul
//        BufferedReader reader;
//        double[] ruls= new double[100];
//        try {
//            reader = new BufferedReader(file);
//            String line= reader.readLine();
//            int i=0;
//            while(line !=null) {
//                System.out.println(i+" "+ line);
//                ruls[i]=Integer.parseInt(line.replaceAll("\\s+",""));
//                i++;
//                line=reader.readLine();
//            }
//            reader.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return ruls;
    }

    /**
     This function loops through the test dataset and predicts the RULs for each system at the last instance.
     The parameters of the functions are the testing data set, the total number of engines in the dataset,
     and the linear regression model.
     The function returns an array with the predicted RUL for each system.
     @author Talal
     */
    protected static double[] predictRUL(Instances testData, int totalEngines, SMOreg lr) throws Exception
    {
        Attribute enginne = testData.attribute("Engine_Num");
        Attribute rul = testData.attribute("RUL");

        double engineNum = 0;
        double[] predicted = new double[totalEngines];
        for (int i = 0; i < testData.numInstances(); i++)
        {
            if(i+1==testData.numInstances()-1) {
                Instance one = testData.instance(i+1);
                double life= lr.classifyInstance(one);
                System.out.println(lr.classifyInstance(one));
                double temp = testData.lastInstance().value(enginne);
                predicted[(int) temp-1] = life;
                break;
            }
            Instance row = testData.instance(i);
            Instance nextRow = testData.instance(i+1);
            if(row.value(enginne) != nextRow.value(enginne)){
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

    /**
     This function print the predicted RULs for each system.
     @author Talal
     */
    private static void showPredictedRUL(double[] max)
    {
        int engineNum = 1;

        for(double e: max)
        {
            System.out.println("Predicted RUL for " + (engineNum++) + ": " + e);
        }
    }
}
