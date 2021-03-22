/* Model strategy implementation for Random SubSpace Model. Ensemble learning method aka
 * attribute/feature bagging.
 *
 * @author Khaled
 * @last_edit 03/12/2021
 */

package rul.models;

import app.item.parameter.FloatParameter;
import app.item.parameter.IntParameter;
import app.item.parameter.Parameter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import weka.classifiers.Classifier;
import weka.classifiers.meta.RandomSubSpace;
import weka.core.Instances;

import java.util.HashMap;
import java.util.Map;

public class RandomSubSpaceModelImpl extends ModelStrategy {
    //Default Parameters
    private static final float SUBSPACE_SIZE_PARAM_DEFAULT = 0.5F;
    private static final int NUM_EXECUTION_SLOTS_PARAM_DEFAULT = 1;
    private static final int NUM_ITERATIONS_PARAM_DEFAULT = 10;
    private static final int BATCH_SIZE_PARAM_DEFAULT = 100;

    //Parameters
    private final FloatParameter subSpaceSizePara;
    private final IntParameter numExecutionSlotsPara;
    private final IntParameter numIterationsPara;
    private final IntParameter batchSizePara;

    private RandomSubSpace randomSubSpace;

    public RandomSubSpaceModelImpl() {
        subSpaceSizePara = new FloatParameter("SubSpace Size", SUBSPACE_SIZE_PARAM_DEFAULT);
        numExecutionSlotsPara = new IntParameter("Number of Execution Slots", NUM_EXECUTION_SLOTS_PARAM_DEFAULT);
        numIterationsPara = new IntParameter("Number of Iterations", NUM_ITERATIONS_PARAM_DEFAULT);
        batchSizePara = new IntParameter("Batch Size", BATCH_SIZE_PARAM_DEFAULT);

        addParameter(subSpaceSizePara);
        addParameter(numExecutionSlotsPara);
        addParameter(numIterationsPara);
        addParameter(batchSizePara);
    }

    static Logger logger = LoggerFactory.getLogger(RandomSubSpaceModelImpl.class);

    /**
     * This function takes the assets as the training dataset, and returns the trained
     * Random SubSpace classifier.
     *
     * @author Khaled
     */
    @Override
    public Classifier trainModel(Instances dataToTrain) {
        randomSubSpace = new RandomSubSpace();
        dataToTrain.setClassIndex(dataToTrain.numAttributes() - 1);

        randomSubSpace.setSubSpaceSize(((FloatParameter) getParameters().get(subSpaceSizePara.getParamName())).getFloatValue());
        randomSubSpace.setNumExecutionSlots(((IntParameter) getParameters().get(numExecutionSlotsPara.getParamName())).getIntValue());
        randomSubSpace.setNumIterations(((IntParameter) getParameters().get(numIterationsPara.getParamName())).getIntValue());
        randomSubSpace.setBatchSize(String.valueOf(((IntParameter) getParameters().get(batchSizePara.getParamName())).getIntValue()));

        try {
            randomSubSpace.buildClassifier(dataToTrain);
        } catch (Exception e) {
            logger.error("Exception: ", e);
        }

        setClassifier(randomSubSpace);
        return randomSubSpace;
    }

    @Override
    public Map<String, Parameter> getDefaultParameters() {
        FloatParameter subSpaceSizeParaDefault = new FloatParameter("SubSpace Size", SUBSPACE_SIZE_PARAM_DEFAULT);
        IntParameter numExecutionSlotsParaDefault = new IntParameter("Number of Execution Slots", NUM_EXECUTION_SLOTS_PARAM_DEFAULT);
        IntParameter numIterationsParaDefault = new IntParameter("Number of Iterations", NUM_ITERATIONS_PARAM_DEFAULT);
        IntParameter batchSizeParaDefault = new IntParameter("Batch Size", BATCH_SIZE_PARAM_DEFAULT);

        Map<String, Parameter> parameters = new HashMap<>();
        parameters.put(subSpaceSizeParaDefault.getParamName(), subSpaceSizeParaDefault);
        parameters.put(numExecutionSlotsParaDefault.getParamName(), numExecutionSlotsParaDefault);
        parameters.put(numIterationsParaDefault.getParamName(), numIterationsParaDefault);
        parameters.put(batchSizeParaDefault.getParamName(), batchSizeParaDefault);

        return parameters;
    }

    public RandomSubSpace getRandomSubSpaceObject() {
        return this.randomSubSpace;
    }

}
