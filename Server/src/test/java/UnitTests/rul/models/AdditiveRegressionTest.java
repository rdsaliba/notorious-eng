package UnitTests.rul.models;

import app.item.parameter.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import rul.models.AdditiveRegressionModelImpl;
import rul.models.ModelsController;
import weka.classifiers.Classifier;
import weka.classifiers.meta.AdditiveRegression;
import weka.core.Instances;

import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class AdditiveRegressionTest
{
    private ModelsController modelsController;
    private StringParameter batchSizePara;
    private BoolParameter minimizeAbsoluteErrorPara;
    private IntParameter numIterationsPara;
    private BoolParameter resumePara;
    private FloatParameter shrinkagePara;
    private Map<String, Parameter> parameters;

    @Before
    public void setUp()
    {
        modelsController = new ModelsController(new AdditiveRegressionModelImpl());
        batchSizePara = new StringParameter("Batch Size", "456");
        minimizeAbsoluteErrorPara = new BoolParameter("Minimize Absolute Error", true);
        numIterationsPara = new IntParameter("Number of Iterations", 22);
        resumePara = new BoolParameter("Resume", false);
        shrinkagePara = new FloatParameter("Shrinkage", 4.5f);

        parameters = new HashMap();
        parameters.put(batchSizePara.getParamName(), batchSizePara);
        parameters.put(minimizeAbsoluteErrorPara.getParamName(), minimizeAbsoluteErrorPara);
        parameters.put(numIterationsPara.getParamName(), numIterationsPara);
        parameters.put(resumePara.getParamName(), resumePara);
        parameters.put(shrinkagePara.getParamName(), shrinkagePara);
    }

    @After
    public void tearDown()
    {
        modelsController = null;
    }

    @Test
    public void trainModel() throws Exception
    {
        FileReader trainFile = new FileReader("src/test/resources/FD01_Train_RUL.arff");
        Instances  trainData = new Instances(trainFile);
        trainData.setClassIndex(trainData.numAttributes() - 1);

        Classifier additiveRegression = new AdditiveRegression();
        assertEquals("Should Return Additive Regression Model", additiveRegression.getClass(),
                     modelsController.trainModel(trainData).getClass());
    }

    @Test
    public void updateParam() throws Exception
    {
        FileReader trainFile = new FileReader("src/test/resources/FD01_Train_RUL.arff");
        Instances  trainData = new Instances(trainFile);
        trainData.setClassIndex(trainData.numAttributes() - 1);

        modelsController.setParameters(parameters);
        modelsController.trainModel(trainData);

        assertEquals("Asserting the BatchSize parameter was changed",((AdditiveRegressionModelImpl) modelsController.getModelStrategy()).getAdditiveRegressionObject().getBatchSize(), batchSizePara.getStringValue());
        assertEquals("Asserting the MinimizeAbsoluteError parameter was changed",((AdditiveRegressionModelImpl) modelsController.getModelStrategy()).getAdditiveRegressionObject().getMinimizeAbsoluteError(), minimizeAbsoluteErrorPara.getBoolValue());
        assertEquals("Asserting the NumIterations parameter was changed",((AdditiveRegressionModelImpl) modelsController.getModelStrategy()).getAdditiveRegressionObject().getNumIterations(), numIterationsPara.getIntValue());
        assertEquals("Asserting the Resume parameter was changed",((AdditiveRegressionModelImpl) modelsController.getModelStrategy()).getAdditiveRegressionObject().getResume(), resumePara.getBoolValue());
        assertEquals("Asserting the Shrinkage parameter was changed",((AdditiveRegressionModelImpl) modelsController.getModelStrategy()).getAdditiveRegressionObject().getShrinkage(), shrinkagePara.getFloatValue(), 0.001f);
    }
}