package rul.models;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import weka.classifiers.Classifier;
import weka.classifiers.trees.RandomForest;
import weka.core.Instances;

import java.io.FileReader;

import static org.junit.Assert.assertEquals;

public class RandomForestTest {
    private ModelsController modelsController;

    @Before
    public void setUp() {
        modelsController = new ModelsController(new RandomForestModelImpl());
    }

    @After
    public void tearDown() {
        modelsController = null;
    }

    @Test
    public void trainModel() throws Exception {
        FileReader trainFile = new FileReader("src/test/resources/FD01_Train_RUL.arff");
        Instances trainData  = new Instances(trainFile);
        trainData.setClassIndex(trainData.numAttributes() - 1);

        Classifier randomForest = new RandomForest();
        assertEquals("Should Return Random Forest Model", randomForest.getClass(), modelsController.trainModel(trainData).getClass());

    }
}
