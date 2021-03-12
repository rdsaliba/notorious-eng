package UnitTests.rul.models;

import app.item.parameter.BoolParameter;
import app.item.parameter.FloatParameter;
import app.item.parameter.Parameter;
import app.item.parameter.StringParameter;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import rul.models.LinearRegressionModelImpl;
import rul.models.ModelsController;
import weka.classifiers.functions.LinearRegression;
import weka.core.Instances;

import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class LinearRegressionTest {
    private ModelsController modelsController;
    private BoolParameter useQRDecompositionPara;
    private BoolParameter eliminateColinearAttributesPara;
    private FloatParameter ridgePara;
    private StringParameter batchSizePara;
    private Map<String, Parameter> parameters;

    @Before
    public void setUp() {
        modelsController = new ModelsController(new LinearRegressionModelImpl());
        useQRDecompositionPara = new BoolParameter("Use QR Decomposition", true);
        eliminateColinearAttributesPara = new BoolParameter("Eliminate Colinear Attributes", false);
        ridgePara = new FloatParameter("Ridge", 1.0E-5F);
        batchSizePara = new StringParameter("Batch Size", "10");

        parameters = new HashMap();
        parameters.put(useQRDecompositionPara.getParamName(), useQRDecompositionPara);
        parameters.put(eliminateColinearAttributesPara.getParamName(), eliminateColinearAttributesPara);
        parameters.put(ridgePara.getParamName(), ridgePara);
        parameters.put(batchSizePara.getParamName(), batchSizePara);
    }

    @After
    public void tearDown() {
        modelsController = null;
    }

    @Test
    public void removeInstances() throws IOException {
        FileReader file = new FileReader("src/test/resources/FD01_Train_RUL.arff");
        Instances set = new Instances(file);
        file = new FileReader("src/test/resources/FD01_Train_RUL.arff");
        Instances set2 = new Instances(file);
        set.setClassIndex(set.numAttributes() - 1);
        set2.setClassIndex(set2.numAttributes() - 1);
        assertTrue("Size of dataset should be reduced", LinearRegressionModelImpl.removeInstances(set, 5).numInstances() < set2.numInstances());

    }

    @Test
    public void trainModel() throws Exception {
        FileReader file = new FileReader("src/test/resources/FD01_Train_RUL.arff");
        Instances set = new Instances(file);
        set.setClassIndex(set.numAttributes() - 1);
        LinearRegression linearRegression = new LinearRegression();
        assertEquals("Should Return Linear Regression Model", linearRegression.getClass(), modelsController.trainModel(set).getClass());
    }

    @Test
    public void updateParam() throws Exception
    {
        FileReader trainFile = new FileReader("src/test/resources/FD01_Train_RUL.arff");
        Instances  trainData = new Instances(trainFile);
        trainData.setClassIndex(trainData.numAttributes() - 1);

        modelsController.setParameters(parameters);
        modelsController.trainModel(trainData);

        assertEquals("Asserting the UseQRDecomposition parameter was changed",((LinearRegressionModelImpl) modelsController.getModelStrategy()).getLinearRegressionObject().getUseQRDecomposition(), useQRDecompositionPara.getBoolValue());
        assertEquals("Asserting the EliminateColinearAttributes parameter was changed",((LinearRegressionModelImpl) modelsController.getModelStrategy()).getLinearRegressionObject().getEliminateColinearAttributes(), eliminateColinearAttributesPara.getBoolValue());
        assertEquals("Asserting the BatchSize parameter was changed", ((LinearRegressionModelImpl) modelsController.getModelStrategy()).getLinearRegressionObject().getBatchSize(), batchSizePara.getStringValue());
    }
}