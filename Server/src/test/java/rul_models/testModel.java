package rul_models;

import org.junit.Test;
import rul.models.LSTMModelImpl;
import rul.models.ModelStrategy;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.functions.LinearRegression;
import weka.core.Attribute;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertTrue;

public class testModel {

    static String SYSTEM_NAME = "Asset_id";

    public testModel() {
    }

    public Evaluation LSTMEval() throws Exception
    {
        DataSource testSrc = new DataSource("Dataset/Converted/test1Shortened.arff");
        Instances testSet = testSrc.getDataSet();

        DataSource trainSrc = new DataSource("Dataset/Converted/train_FD001_withRUL.arff");
        Instances trainSet = trainSrc.getDataSet();

        testSet.setClassIndex(testSet.numAttributes() - 1);
        trainSet.setClassIndex(trainSet.numAttributes() - 1);

        ModelStrategy model = new LSTMModelImpl();
        Classifier classifier = model.trainModel(trainSet);

        //FileReader realRUL = new FileReader("Dataset/Real RUL/RUL_FD001.txt");

        Evaluation lstmEval = new Evaluation(trainSet);
        lstmEval.evaluateModel(classifier, testSet);
        System.out.println(lstmEval.toSummaryString());
        return lstmEval;
    }

    @Test
    public void testLSTMEval() throws Exception
    {
        assertTrue("RMSE should be less than 41", LSTMEval().rootMeanSquaredError() < 41);
    }


    /**
     This function evaluates the performance of an Artificial intelligence model.
     The parameters of the function are a training dataset, test dataset, and a text file that contains the real RULs.
     The function prints the root mean squared error of a model.
     @author Talal
     */
    public void evaluateModel(Classifier model, Instances testDataset, FileReader realRUls) throws Exception {
        LinearRegression lr=(LinearRegression) model;
        double[] predictedRULs= predictRUL(testDataset, (int) testDataset.lastInstance().value(testDataset.attribute(SYSTEM_NAME)),lr);
        Double[] realRULs = parseRULs(realRUls);
        double rmse= RootMeanSquaredError.calculate(predictedRULs,realRULs);
        System.out.println("Root mean squared error is : "+rmse);
        showPredictedRUL(predictedRULs);
        System.out.println("predicted rul is:"+predictedRULs[0]+"\n real rul is: "+realRULs[0]);
        //showPredictedRUL(realRULs);
    }


    /**
     This function reads a text file and stores the ruls available in an array.
     The parameter of the function is the text file with the real RULs.
     the function returns the array with the real RULs.
     @author Talal
     TODO remove the 100 and make it dynamic
     */
    private Double[] parseRULs(FileReader file) throws IOException {
        BufferedReader reader;
        List<Double> ruls= new ArrayList<>();
        try {
            reader =  new BufferedReader(file);
            String line= reader.readLine();
            int i=0;
            while(line !=null) {
                System.out.println(i+" "+ line);
                String trimmed= line.trim();
                ruls.add(Double.parseDouble(trimmed));
                i++;
                line=reader.readLine();
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Double[] rulz= new Double[ruls.size()];
        // ArrayList to Array Conversion
        for (int i =0; i < ruls.size(); i++)
            rulz[i] = ruls.get(i);
        return rulz;
    }

    /**
     This function loops through the test dataset and predicts the RULs for each system at the last instance.
     The parameters of the functions are the testing data set, the total number of engines in the dataset,
     and the linear regression model.
     The function returns an array with the predicted RUL for each system.
     @author Talal
     */
    public static double[] predictRUL(Instances testData, int totalEngines, LinearRegression lr) throws Exception
    {
        Attribute asset = testData.attribute(SYSTEM_NAME);

        double engineNum = 0;
        double[] predicted = new double[totalEngines];
        for (int i = 0; i < testData.numInstances(); i++)
        {
            if(i+1==testData.numInstances()-1) {
                Instance one = testData.instance(i+1);
                double life= lr.classifyInstance(one);
                System.out.println(lr.classifyInstance(one));
                double temp = testData.lastInstance().value(asset);
                predicted[(int) temp-1] = life;
                break;
            }
            Instance row = testData.instance(i);
            Instance nextRow = testData.instance(i+1);
            if(row.value(asset) != nextRow.value(asset)){
                System.out.println("a b c d"+row.value(asset));
                double engine=row.value(testData.attribute(SYSTEM_NAME));
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
