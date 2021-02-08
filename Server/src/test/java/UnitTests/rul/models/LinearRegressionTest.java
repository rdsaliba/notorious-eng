package UnitTests.rul.models;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import rul.models.LinearRegressionModelImpl;
import rul.models.ModelsController;
import weka.classifiers.functions.LinearRegression;
import weka.core.Instances;

import java.io.FileReader;
import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class LinearRegressionTest {
    //private LinearRegressionModelImpl lr;
    private ModelsController modelsController;


    @Before
    public void setUp() {
        modelsController = new ModelsController(new LinearRegressionModelImpl());

    }

    @After
    public void tearDown() {
        modelsController = null;
    }

    @Test
    public void removeInstances() throws IOException {
        FileReader file = new FileReader("src/test/resources/FD01_Train_RUL.arff");
        Instances set = new Instances(file);
        file = new FileReader("src/test/resources/FD01_Train_RUL.arff");
        Instances set2 = new Instances(file);
        set.setClassIndex(set.numAttributes() - 1);
        set2.setClassIndex(set2.numAttributes() - 1);
        assertTrue("Size of dataset should be reduced", LinearRegressionModelImpl.removeInstances(set, 5).numInstances() < set2.numInstances());

    }

    @Test
    public void trainModel() throws Exception {
        FileReader file = new FileReader("src/test/resources/FD01_Train_RUL.arff");
        Instances set = new Instances(file);
        set.setClassIndex(set.numAttributes() - 1);
        LinearRegression linearRegression = new LinearRegression();
        assertEquals("Should Return Linear Regression Model", linearRegression.getClass(), modelsController.trainModel(set).getClass());

    }
}
