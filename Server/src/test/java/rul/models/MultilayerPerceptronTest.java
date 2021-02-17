package rul.models;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import weka.classifiers.Classifier;
import weka.classifiers.functions.MultilayerPerceptron;
import weka.core.Instances;

import java.io.FileReader;

import static org.junit.Assert.assertEquals;

public class MultilayerPerceptronTest {
    private ModelsController modelsController;

    @Before
    public void setUp() {
        modelsController = new ModelsController(new MultilayerPerceptronModelImpl());
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

        Classifier mlp = new MultilayerPerceptron();
        assertEquals("Should Return Multilayer Perceptron Model", mlp.getClass(),
                     modelsController.trainModel(trainData).getClass());
    }
}
