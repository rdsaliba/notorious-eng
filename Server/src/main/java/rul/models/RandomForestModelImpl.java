/* Third strategy design pattern and implementation of Model Strategy.
 * This class is to be used for the model RandomForest
 *
 * @author Khaled
 * @last_edit 03/12/2021
 */

package rul.models;

import app.item.parameter.BoolParameter;
import app.item.parameter.IntParameter;
import app.item.parameter.Parameter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import weka.classifiers.Classifier;
import weka.classifiers.trees.RandomForest;
import weka.core.Instances;

import java.util.HashMap;
import java.util.Map;

public class RandomForestModelImpl extends ModelStrategy {
    private static final long serialVersionUID = -4142301725621985749L;

    //Default Parameters
    private static final boolean STORE_OUT_OF_BAG_PREDICTIONS_PARAM_DEFAULT = false;
    private static final boolean BREAK_TIES_RANDOMLY_PARAM_DEFAULT = false;
    private static final boolean CALC_OUT_BAGS_PARAM_DEFAULT = false;
    private static final boolean COMPUTE_ATTRIBUTE_IMPORTANCE_PARAM_DEFAULT = false;

    private static final int BAG_SIZE_PERCENT_PARAM_DEFAULT = 100;
    private static final int MAX_DEPTH_PARAM_DEFAULT = 0;
    private static final int NUM_EXECUTION_SLOTS_PARAM_DEFAULT = 1;
    private static final int NUM_FEATURES_PARAM_DEFAULT = 0;
    private static final int NUM_ITERATIONS_PARAM_DEFAULT = 100;

    private static final int BATCH_SIZE_PARAM_DEFAULT = 100;
    static Logger logger = LoggerFactory.getLogger(RandomForestModelImpl.class);
    //Parameters
    private final BoolParameter storeOutOfBagPredictionsPara;
    private final BoolParameter breakTiesRandomlyPara;
    private final BoolParameter calcOutBagPara;
    private final BoolParameter computeAttributeImportancePara;
    private final IntParameter bagSizePercentPara;
    private final IntParameter maxDepthPara;
    private final IntParameter numExecutionSlotsPara;
    private final IntParameter numFeaturesPara;
    private final IntParameter numIterationsPara;
    private final IntParameter batchSizePara;
    private RandomForest randomForest;

    public RandomForestModelImpl() {
        storeOutOfBagPredictionsPara = new BoolParameter("Store out of Bag Predictions", STORE_OUT_OF_BAG_PREDICTIONS_PARAM_DEFAULT);
        breakTiesRandomlyPara = new BoolParameter("Break Ties Randomly", BREAK_TIES_RANDOMLY_PARAM_DEFAULT);
        calcOutBagPara = new BoolParameter("Calc out of Bag", CALC_OUT_BAGS_PARAM_DEFAULT);
        computeAttributeImportancePara = new BoolParameter("Compute Attribute Importance", COMPUTE_ATTRIBUTE_IMPORTANCE_PARAM_DEFAULT);

        bagSizePercentPara = new IntParameter("Bag Size Percent", BAG_SIZE_PERCENT_PARAM_DEFAULT);
        maxDepthPara = new IntParameter("Max Depth", MAX_DEPTH_PARAM_DEFAULT);
        numExecutionSlotsPara = new IntParameter("Number of Execution Slots", NUM_EXECUTION_SLOTS_PARAM_DEFAULT);
        numFeaturesPara = new IntParameter("Number of Features", NUM_FEATURES_PARAM_DEFAULT);
        numIterationsPara = new IntParameter("Number of Iterations", NUM_ITERATIONS_PARAM_DEFAULT);

        batchSizePara = new IntParameter("Batch Size", BATCH_SIZE_PARAM_DEFAULT);

        addParameter(storeOutOfBagPredictionsPara);
        addParameter(breakTiesRandomlyPara);
        addParameter(calcOutBagPara);
        addParameter(computeAttributeImportancePara);

        addParameter(bagSizePercentPara);
        addParameter(maxDepthPara);
        addParameter(numExecutionSlotsPara);
        addParameter(numFeaturesPara);
        addParameter(numIterationsPara);

        addParameter(batchSizePara);
    }

    /**
     * This function takes the assets as the training dataset, and returns the trained
     * Random Forest classifier.
     *
     * @author Khaled
     */
    @Override
    public Classifier trainModel(Instances dataToTrain) {
        randomForest = new RandomForest();
        dataToTrain.setClassIndex(dataToTrain.numAttributes() - 1);

        randomForest.setStoreOutOfBagPredictions(((BoolParameter) getParameters().get(storeOutOfBagPredictionsPara.getParamName())).getBoolValue());
        randomForest.setBreakTiesRandomly(((BoolParameter) getParameters().get(breakTiesRandomlyPara.getParamName())).getBoolValue());
        randomForest.setCalcOutOfBag(((BoolParameter) getParameters().get(calcOutBagPara.getParamName())).getBoolValue());
        randomForest.setComputeAttributeImportance(((BoolParameter) getParameters().get(computeAttributeImportancePara.getParamName())).getBoolValue());

        randomForest.setBagSizePercent(((IntParameter) getParameters().get(bagSizePercentPara.getParamName())).getIntValue());
        randomForest.setMaxDepth(((IntParameter) getParameters().get(maxDepthPara.getParamName())).getIntValue());
        randomForest.setNumExecutionSlots(((IntParameter) getParameters().get(numExecutionSlotsPara.getParamName())).getIntValue());
        randomForest.setNumFeatures(((IntParameter) getParameters().get(numFeaturesPara.getParamName())).getIntValue());
        randomForest.setNumIterations(((IntParameter) getParameters().get(numIterationsPara.getParamName())).getIntValue());

        randomForest.setBatchSize(String.valueOf(((IntParameter) getParameters().get(batchSizePara.getParamName())).getIntValue()));

        try {
            randomForest.buildClassifier(dataToTrain);
        } catch (Exception e) {
            logger.error("Exception: ", e);
        }

        setClassifier(randomForest);
        return randomForest;
    }

    @Override
    public Map<String, Parameter> getDefaultParameters() {
        BoolParameter storeOutOfBagPredictionsParaDefault = new BoolParameter("Store out of Bag Predictions", STORE_OUT_OF_BAG_PREDICTIONS_PARAM_DEFAULT);
        BoolParameter breakTiesRandomlyParaDefault = new BoolParameter("Break Ties Randomly", BREAK_TIES_RANDOMLY_PARAM_DEFAULT);
        BoolParameter calcOutBagParaDefault = new BoolParameter("Calc out of Bag", CALC_OUT_BAGS_PARAM_DEFAULT);
        BoolParameter computeAttributeImportanceParaDefault = new BoolParameter("Compute Attribute Importance", COMPUTE_ATTRIBUTE_IMPORTANCE_PARAM_DEFAULT);

        IntParameter bagSizePercentParaDefault = new IntParameter("Bag Size Percent", BAG_SIZE_PERCENT_PARAM_DEFAULT);
        IntParameter maxDepthParaDefault = new IntParameter("Max Depth", MAX_DEPTH_PARAM_DEFAULT);
        IntParameter numExecutionSlotsParaDefault = new IntParameter("Number of Execution Slots", NUM_EXECUTION_SLOTS_PARAM_DEFAULT);
        IntParameter numFeaturesParaDefault = new IntParameter("Number of Features", NUM_FEATURES_PARAM_DEFAULT);
        IntParameter numIterationsParaDefault = new IntParameter("Number of Iterations", NUM_ITERATIONS_PARAM_DEFAULT);

        IntParameter batchSizeParaDefault = new IntParameter("Batch Size", BATCH_SIZE_PARAM_DEFAULT);

        Map<String, Parameter> parameters = new HashMap<>();
        parameters.put(storeOutOfBagPredictionsParaDefault.getParamName(), storeOutOfBagPredictionsParaDefault);
        parameters.put(breakTiesRandomlyParaDefault.getParamName(), breakTiesRandomlyParaDefault);
        parameters.put(calcOutBagParaDefault.getParamName(), calcOutBagParaDefault);
        parameters.put(computeAttributeImportanceParaDefault.getParamName(), computeAttributeImportanceParaDefault);

        parameters.put(bagSizePercentParaDefault.getParamName(), bagSizePercentParaDefault);
        parameters.put(maxDepthParaDefault.getParamName(), maxDepthParaDefault);
        parameters.put(numExecutionSlotsParaDefault.getParamName(), numExecutionSlotsParaDefault);
        parameters.put(numFeaturesParaDefault.getParamName(), numFeaturesParaDefault);
        parameters.put(numIterationsParaDefault.getParamName(), numIterationsParaDefault);

        parameters.put(batchSizeParaDefault.getParamName(), batchSizeParaDefault);

        return parameters;
    }

    public RandomForest getRandomForestObject() {
        return this.randomForest;
    }

}