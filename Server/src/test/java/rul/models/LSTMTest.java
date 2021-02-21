package rul.models;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import weka.classifiers.functions.Dl4jMlpClassifier;
import weka.core.Instances;

import java.io.FileReader;

import static org.junit.Assert.assertEquals;

public class LSTMTest
{
    private ModelsController modelsController;


    @Before
    public void setUp(){
        modelsController = new ModelsController(new LSTMModelImpl());

    }

    @After
    public void tearDown() {
        modelsController = null;
    }

    @Test
    public void trainModel() throws Exception {
        FileReader file= new FileReader("src/test/resources/FD01_Train_RUL.arff");
        Instances set = new Instances(file);
        set.setClassIndex(set.numAttributes()-1);
        Dl4jMlpClassifier LSTM = new Dl4jMlpClassifier();
        assertEquals("Should Return Linear Regression Model", LSTM.getClass(), modelsController.trainModel(set).getClass());

    }
}
