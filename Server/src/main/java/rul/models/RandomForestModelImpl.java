/* Third strategy design pattern and implementation of Model Strategy.
 * This class is to be used for the model RandomForest
 *
 * @author Khaled
 * @last_edit 02/14/2021
 */

package rul.models;

import weka.classifiers.Classifier;
import weka.classifiers.trees.RandomForest;
import weka.core.Instances;

public class RandomForestModelImpl extends ModelStrategy {
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
            e.printStackTrace();
            return null;
        }

        return randomForest;
    }

}