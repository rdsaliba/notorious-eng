package UnitTests.preprocessing;

import org.junit.Before;
import org.junit.Test;
import preprocessing.DataPrePreprocessorController;
import weka.core.Instances;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.Remove;

import java.io.FileReader;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

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

    @Test
    public void addRULColTest() throws Exception {
        FileReader file = new FileReader("src/test/resources/FD01_Train_RUL.arff");
        Instances data = new Instances(file);
        data.setClassIndex(data.numAttributes() - 1);

        //Remove RUL column from the locally stored arff file (which have already been preprocessed)
        //to match with the assets data in the DB (no RULs in DB).
        Remove removeFilter = new Remove();
        int[]  indices      = {data.numAttributes() - 1};
        removeFilter.setAttributeIndicesArray(indices);
        removeFilter.setInputFormat(data);
        Instances dataWithoutRUL = Filter.useFilter(data, removeFilter);

        Instances dataWithRUL = dataPrePreprocessorController.addRULCol(dataWithoutRUL);

        assertTrue("Data after applying the addRULCol should have one more attribute", dataWithRUL.numAttributes() == (dataWithoutRUL.numAttributes() + 1));

    }

}
