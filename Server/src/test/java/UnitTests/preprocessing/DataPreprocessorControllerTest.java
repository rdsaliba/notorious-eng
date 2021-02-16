package UnitTests.preprocessing;

import org.junit.Before;
import org.junit.Test;
import preprocessing.DataPrePreprocessorController;
import weka.core.Instances;

import java.io.FileReader;

import static org.junit.Assert.assertEquals;

public class DataPreprocessorControllerTest {
    DataPrePreprocessorController dataPrePreprocessorController;

    @Before
    public void setup() {
        dataPrePreprocessorController = DataPrePreprocessorController.getInstance();
    }

    @Test
    public void minimalReductionTest() throws Exception {
        FileReader file = new FileReader("src/test/resources/FD01_Train_RUL.arff");
        Instances toReduce = new Instances(file);

        Instances reduced = dataPrePreprocessorController.minimallyReduceData(toReduce);
        assertEquals("Reduced instance should have 19 attributes", 19, reduced.numAttributes());

    }

    @Test
    public void fullReductionTest() throws Exception {
        FileReader file = new FileReader("src/test/resources/FD01_Train_RUL.arff");
        Instances toReduce = new Instances(file);

        Instances reduced = dataPrePreprocessorController.reduceData(toReduce);
        assertEquals("Reduced instance should have 19 attributes", 15, reduced.numAttributes());

    }
}
