/* Third strategy design pattern and implementation of Model Strategy.
 * This class is to be used for the model RandomForest
 *
 * @author Khaled
 * @last_edit 02/14/2021
 */

package rul.models;

import app.item.parameter.Parameter;
import weka.classifiers.Classifier;
import weka.classifiers.trees.RandomForest;
import weka.core.Instances;

import java.util.Map;

public class RandomForestModelImpl extends ModelStrategy {

    private int bagSizePercentParameter;
    private int batchSize;
    private boolean breakTiesRandomlyParameter;
    private boolean calcOutBagParameter;
    private boolean computeAttributeImportanceParameter;
    private int maxDepthParameter;
    private int numExecutionSlotsParamter;
    private int numFeaturesParameter;
    private int numIterationsParameter;
    private boolean storeOutOfBagPredictionsParameter;

    //default
    public RandomForestModelImpl()
    {
        bagSizePercentParameter = 100;
        batchSize = 100;
        breakTiesRandomlyParameter = false;
        calcOutBagParameter = false;
        computeAttributeImportanceParameter = false;
        maxDepthParameter = 0;
        numExecutionSlotsParamter = 1;
        numFeaturesParameter = 0;
        numIterationsParameter = 100;
        storeOutOfBagPredictionsParameter = false;
    }
    /**
     * This function takes the assets as the training dataset, and returns the trained
     * Random Forest classifier.
     *
     * @author Khaled
     */

    @Override
    public Classifier trainModel(Instances dataToTrain) {
        RandomForest randomForest = new RandomForest();
        dataToTrain.setClassIndex(dataToTrain.numAttributes() - 1);

        try {
            randomForest.buildClassifier(dataToTrain);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        return randomForest;
    }

    @Override
    public Map<String, Parameter> getDefaultParameters()
    {
        return null;
    }

}