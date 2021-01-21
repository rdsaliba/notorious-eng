package rul.models;
//import com.cbms.app.item.AssetAttribute;
//import com.cbms.rul.models.LinearRegressionModelImpl;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import rul_models.testModel;
import weka.classifiers.Classifier;
import weka.classifiers.functions.LinearRegression;
import weka.core.Instances;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class rulTest {
    private LinearRegressionModelImpl lr;


    @Before
    public void setUp(){
        lr = new LinearRegressionModelImpl();

    }

    @After
    public void tearDown() {
        lr = null;
    }

    @Test
    public void removeInstances() throws IOException {
        FileReader file= new FileReader("/Users/talalbazerbachi/Documents/GitHub/notorious-eng/Dataset/Converted/FD01_Train_RUL.arff");
        Instances set = new Instances(file);
        assertEquals("Size of dataset should be reduced", true, lr.removeInstances(set).numInstances()<set.numInstances());

    }

    @Test
    public void trainModel() throws Exception {
        FileReader file= new FileReader("/Users/talalbazerbachi/Documents/GitHub/notorious-eng/Dataset/Converted/FD01_Train_RUL.arff");
        Instances set = new Instances(file);
        LinearRegression linearRegression = new LinearRegression();
        assertEquals("Should Return Linear Regression Model", linearRegression.getClass(), lr.trainModel(set).getClass());

    }
}
