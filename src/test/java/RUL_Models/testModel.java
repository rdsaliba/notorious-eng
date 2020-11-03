package test.java.RUL_Models;
import test.java.RUL_Models.RootMeanSquaredError;
import weka.classifiers.Classifier;
import weka.classifiers.functions.LinearRegression;
import weka.core.Attribute;
import weka.core.Instance;
import weka.core.Instances;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;


public class testModel {
    public testModel() {
    }

    /**
     This function evaluates the performance of an Artificial intellignece model.
     The parameters of the function are a training dataset, test dataset, and a text file that contains the real RULs.
     The function prints the root mean squared error of a model.
     @author Talal
     */
    public void evaluateModel(Classifier model, Instances testDataset, FileReader realRUls) throws Exception {
        LinearRegression lr=(LinearRegression) model;
        double[] predictedRULs= predictRUL(testDataset, (int) testDataset.lastInstance().value(testDataset.attribute("Engine_Num")),lr);
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
        List<Double> ruls=new ArrayList<Double>();
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
        Attribute enginne = testData.attribute("Engine_Num");

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
                System.out.println("a b c d"+row.value(enginne));
                double engine=row.value(testData.attribute("Engine_Num"));
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
