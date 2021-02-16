package UnitTests.rul.models;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import rul.models.ModelsController;
import rul.models.RandomCommitteeModelImpl;
import weka.classifiers.Classifier;
import weka.classifiers.meta.RandomCommittee;
import weka.core.Instances;

import java.io.FileReader;

import static org.junit.Assert.assertEquals;

public class RandomCommitteeTest {
    private ModelsController modelsController;

    @Before
    public void setUp() {
        modelsController = new ModelsController(new RandomCommitteeModelImpl());
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

        Classifier randomCommittee = new RandomCommittee();
        assertEquals("Should Return Random Committee Model", randomCommittee.getClass(), modelsController.trainModel(trainData).getClass());

    }

}
