package UnitTests.assessment;

import org.junit.Before;
import org.junit.Test;
import rul.assessment.AssessmentController;
import rul.models.LinearRegressionModelImpl;
import weka.core.Instances;

import java.io.FileReader;

import static org.junit.Assert.assertEquals;

public class HealthAssessmentTest {
    private AssessmentController assessmentController;

    @Before
    public void setup() {
        assessmentController = new AssessmentController();
    }

    @Test
    public void testLinearRegressionRulEstimation() throws Exception {
        FileReader file = new FileReader("src/test/resources/FD01_Train_RUL.arff");
        Instances toTrain = new Instances(file);
        file = new FileReader("src/test/resources/FD01_Test_RUL.arff");
        Instances toTest = new Instances(file);
        assessmentController.estimateRUL(toTest, new LinearRegressionModelImpl().trainModel(toTrain));
        assertEquals(135, (int) assessmentController.estimateRUL(toTest, new LinearRegressionModelImpl().trainModel(toTrain)));
    }

}
