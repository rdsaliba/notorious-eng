/*
  Implementation of the model strategy interface, part of the Strategy design pattern
  Contains 2 methods, one to train the data and another private function to remove instances that are too old
  and cannot provide useful information

  @author      Paul Micu
  @last_edit   03/12/2021
 */
package rul.models;

import app.item.parameter.BoolParameter;
import app.item.parameter.FloatParameter;
import app.item.parameter.Parameter;
import app.item.parameter.StringParameter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import weka.classifiers.Classifier;
import weka.classifiers.functions.LinearRegression;
import weka.core.Instance;
import weka.core.Instances;

import java.util.HashMap;
import java.util.Map;

public class LinearRegressionModelImpl extends ModelStrategy
{

    //Default Parameters
    private final boolean USE_QR_DECOMPOSITION_PARAM_DEFAULT = false;
    private final boolean ELIMINATE_COLINEAR_ATTRIBUTES_PARAM_DEFAULT = true;
    private final float RIDGE_PARAM_DEFAULT = 1.0E-8F;
    private final String BATCH_SIZE_PARAM_DEFAULT = "100";

    private BoolParameter useQRDecompositionPara;
    private BoolParameter eliminateColinearAttributesPara;
    private FloatParameter ridgePara;
    private StringParameter batchSizePara;

    private LinearRegression linearRegression;

    public LinearRegressionModelImpl()
    {
        useQRDecompositionPara = new BoolParameter("Use QR Decomposition", USE_QR_DECOMPOSITION_PARAM_DEFAULT);
        eliminateColinearAttributesPara = new BoolParameter("Eliminate Colinear Attributes", ELIMINATE_COLINEAR_ATTRIBUTES_PARAM_DEFAULT);
        ridgePara = new FloatParameter("Ridge", RIDGE_PARAM_DEFAULT);
        batchSizePara = new StringParameter("Batch Size", BATCH_SIZE_PARAM_DEFAULT);

        addParameter(useQRDecompositionPara);
        addParameter(eliminateColinearAttributesPara);
        addParameter(ridgePara);
        addParameter(batchSizePara);
    }

    static Logger logger = LoggerFactory.getLogger(LinearRegressionModelImpl.class);

    /**
     * This function takes the filtered training dataset and trains a linear regression regression model,
     * after that it returns the model.
     * To use this method you need to pass the training dataset.
     *
     * @author Talal
     */
    @Override
    public Classifier trainModel(Instances firstTrain)
    {
        firstTrain.setClassIndex(firstTrain.numAttributes() - 1);
        linearRegression = new LinearRegression();

        linearRegression.setUseQRDecomposition(((BoolParameter) getParameters().get(useQRDecompositionPara.getParamName())).getBoolValue());
        linearRegression.setEliminateColinearAttributes(((BoolParameter) getParameters().get(eliminateColinearAttributesPara.getParamName())).getBoolValue());
        linearRegression.setRidge(((FloatParameter) getParameters().get(ridgePara.getParamName())).getFloatValue());
        linearRegression.setBatchSize(((StringParameter) getParameters().get(batchSizePara.getParamName())).getStringValue());

        try
        {
            linearRegression.buildClassifier(firstTrain);
        }
        catch (Exception e)
        {
            logger.error("Exception: ", e);
        }

        setClassifier(linearRegression);
        return linearRegression;
    }

    @Override
    public Map<String, Parameter> getDefaultParameters()
    {
        BoolParameter   useQRDecompositionParaDefault          = new BoolParameter("Use QR Decomposition", USE_QR_DECOMPOSITION_PARAM_DEFAULT);
        BoolParameter   eliminateColinearAttributesParaDefault = new BoolParameter("Eliminate Colinear Attributes", ELIMINATE_COLINEAR_ATTRIBUTES_PARAM_DEFAULT);
        FloatParameter  ridgeParaDefault                       = new FloatParameter("Ridge", RIDGE_PARAM_DEFAULT);
        StringParameter batchSizeParaDefault                   = new StringParameter("Batch Size", BATCH_SIZE_PARAM_DEFAULT);

        Map<String, Parameter> parameters = new HashMap<>();
        parameters.put(useQRDecompositionParaDefault.getParamName(), useQRDecompositionParaDefault);
        parameters.put(eliminateColinearAttributesParaDefault.getParamName(), eliminateColinearAttributesParaDefault);
        parameters.put(ridgeParaDefault.getParamName(), ridgeParaDefault);
        parameters.put(batchSizeParaDefault.getParamName(), batchSizeParaDefault);

        return parameters;
    }

    public LinearRegression getLinearRegressionObject()
    {
        return this.linearRegression;
    }

    public static Instances removeInstances(Instances trainDataset, int threshold) {
        for (int i = 0; i < trainDataset.numInstances(); i++) {
            Instance inst = trainDataset.instance(i);
            if(inst.value(inst.classAttribute()) > threshold)
            {
                trainDataset.delete(i);
            }
        }
        return trainDataset;
    }

    public static Instances removeInstances(Instances trainDataset) {
        return removeInstances(trainDataset, 150);
    }

}


