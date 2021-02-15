package rul.models;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import weka.classifiers.Classifier;
import weka.classifiers.meta.RandomSubSpace;
import weka.core.Instances;

import java.io.FileReader;

import static org.junit.Assert.assertEquals;

public class RandomSubSpaceTest {
    private ModelsController modelsController;

    @Before
    public void setUp() {
        modelsController = new ModelsController(new RandomSubSpaceModelImpl());
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

        Classifier randomSubSpace = new RandomSubSpace();
        assertEquals("Should Return Random SubSpace Model", randomSubSpace.getClass(),
                     modelsController.trainModel(trainData).getClass());
    }
}