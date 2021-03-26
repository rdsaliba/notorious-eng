package UnitTests.rul.models;

import app.item.parameter.FloatParameter;
import app.item.parameter.IntParameter;
import app.item.parameter.Parameter;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import rul.models.ModelsController;
import rul.models.SMORegModelImpl;
import weka.classifiers.Classifier;
import weka.classifiers.functions.SMOreg;
import weka.core.Instances;

import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class SMORegTest {
    private ModelsController modelsController;
    private FloatParameter cComplexityPara;
    private IntParameter batchSizePara;
    private Map<String, Parameter> parameters;

    @Before
    public void setUp() {
        modelsController = new ModelsController(new SMORegModelImpl());
        cComplexityPara = new FloatParameter("C Complexity", 1.1F);
        batchSizePara = new IntParameter("Batch Size", 50);

        parameters = new HashMap();
        parameters.put(cComplexityPara.getParamName(), cComplexityPara);
        parameters.put(batchSizePara.getParamName(), batchSizePara);
    }

    @After
    public void tearDown() {
        modelsController = null;
    }

    @Test
    public void trainModel() throws Exception {
        FileReader trainFile = new FileReader("src/test/resources/FD01_Train_RUL.arff");
        Instances trainData = new Instances(trainFile);
        trainData.setClassIndex(trainData.numAttributes() - 1);

        Classifier smoReg = new SMOreg();
        assertEquals("Should Return SMOreg Model", smoReg.getClass(), modelsController.trainModel(trainData).getClass());
    }

    @Test
    public void updateParam() throws Exception {
        FileReader trainFile = new FileReader("src/test/resources/FD01_Train_RUL.arff");
        Instances trainData = new Instances(trainFile);
        trainData.setClassIndex(trainData.numAttributes() - 1);

        modelsController.setParameters(parameters);
        modelsController.trainModel(trainData);

        assertEquals("Asserting the CComplexity parameter was changed", ((SMORegModelImpl) modelsController.getModelStrategy()).getSmOregObject().getC(), cComplexityPara.getFloatValue(), 0.1f);
        assertEquals("Asserting the BatchSize parameter was changed", ((SMORegModelImpl) modelsController.getModelStrategy()).getSmOregObject().getBatchSize(), String.valueOf(batchSizePara.getIntValue()));
    }
}