/* Third strategy design pattern and implementation of Model Strategy.
 * This class is to be used for the model RandomForest
 *
 * @author Khaled
 * @last_edit 02/14/2021
 */

package rul.models;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import weka.classifiers.Classifier;
import weka.classifiers.trees.RandomForest;
import weka.core.Instances;

public class RandomForestModelImpl implements ModelStrategy {

    static Logger logger = LoggerFactory.getLogger(RandomForestModelImpl.class);

    /**
     * This function takes the assets as the training dataset, and returns the trained
     * Random Forest classifier.
     *
     * @author Khaled
     */

    @Override
    public Classifier trainModel(Instances dataToTrain) {
        Classifier randomForest = new RandomForest();
        dataToTrain.setClassIndex(dataToTrain.numAttributes() - 1);

        try {
            randomForest.buildClassifier(dataToTrain);
        } catch (Exception e) {
            logger.error("Exception: ", e);
            return null;
        }

        return randomForest;
    }

}