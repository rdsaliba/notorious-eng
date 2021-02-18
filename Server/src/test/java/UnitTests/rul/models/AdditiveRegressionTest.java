package UnitTests.rul.models;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import rul.models.AdditiveRegressionModelImpl;
import rul.models.ModelsController;
import weka.classifiers.Classifier;
import weka.classifiers.meta.AdditiveRegression;
import weka.core.Instances;

import java.io.FileReader;

import static org.junit.Assert.assertEquals;

public class AdditiveRegressionTest {
    private ModelsController modelsController;

    @Before
    public void setUp() {
        modelsController = new ModelsController(new AdditiveRegressionModelImpl());
    }

    @After
    public void tearDown() {
        modelsController = null;
    }

    @Test
    public void trainModel() throws Exception {
        FileReader trainFile = new FileReader("src/test/resources/FD01_Train_RUL.arff");
        Instances  trainData = new Instances(trainFile);
        trainData.setClassIndex(trainData.numAttributes() - 1);

        Classifier additiveRegression = new AdditiveRegression();
        assertEquals("Should Return Additive Regression Model", additiveRegression.getClass(),
                     modelsController.trainModel(trainData).getClass());
    }
}