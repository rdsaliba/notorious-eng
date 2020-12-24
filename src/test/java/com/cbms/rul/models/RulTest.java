//package com.cbms.rul.models;
//import com.cbms.app.item.AssetAttribute;
//import com.cbms.rul.models.LinearRegressionModelImpl;
//import org.junit.After;
//import org.junit.Before;
//import org.junit.Test;
//import weka.classifiers.Classifier;
//import weka.classifiers.functions.LinearRegression;
//import weka.core.Instances;
//
//import java.RUL_Models.RootMeanSquaredError;
//import java.RUL_Models.testModel;
//import java.io.FileNotFoundException;
//import java.io.FileReader;
//import java.io.IOException;
//import java.util.ArrayList;
//import java.util.List;
//
//import static org.junit.Assert.*;
//
//public class rulTest {
//    private LinearRegressionModelImpl lr;
//    private RootMeanSquaredError error;
//    private testModel testMod;
//
//    @Before
//    public void setUp(){
//        lr = new LinearRegressionModelImpl();
//        error = new RootMeanSquaredError();
//        testMod = new testModel();
//
//    }
//
//    @After
//    public void tearDown() {
//        lr = null;
//        error = null;
//        testMod = null;
//    }
//
//    @Test
//    public void removeInstances() throws IOException {
//        FileReader file= new FileReader("/Users/talalbazerbachi/Documents/GitHub/notorious-eng/Dataset/Converted/FD01_Train_RUL.arff");
//        Instances set = new Instances(file);
//        assertEquals("Size of dataset should be reduced", true, lr.removeInstances(set).numInstances()<set.numInstances());
//
//    }
//
//    @Test
//    public void trainModel() throws Exception {
//        FileReader file= new FileReader("/Users/talalbazerbachi/Documents/GitHub/notorious-eng/Dataset/Converted/FD01_Train_RUL.arff");
//        Instances set = new Instances(file);
//        LinearRegression linearRegression = new LinearRegression();
//        assertEquals("Should Return Linear Regression Model", linearRegression.getClass(), lr.trainModel(set).getClass());
//
//    }
//
//    @Test
//    public void calculate() {
//        double[] first= new double[]{1.1, 1.2, 1.3};
//        Double[] second= new Double[]{2.1, 2.2, 2.3};
//        assertEquals("Function should return ",1 , error.calculate(first, second));
//    }
//
//    @Test
//    public void parseRULs() throws IOException {
//        FileReader read= new FileReader("/Users/talalbazerbachi/Documents/GitHub/notorious-eng/Dataset/Converted/FD01_Train_RUL.arff");
//        Double[] t = new Double[0];
//        assertEquals("Should return list", t.getClass(), testMod.parseRULs(read).getClass());
//    }
//
//    @Test
//    public void showPredictedRULs() {
//        double[] max = new double[0];
//        assertEquals("Should return done", "done", testMod.showPredictedRUL(max));
//    }
//}
