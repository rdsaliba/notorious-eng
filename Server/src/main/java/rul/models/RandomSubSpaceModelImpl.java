/* Model strategy implementation for Random SubSpace Model. Ensemble learning method aka
 * attribute/feature bagging.
 *
 * @author Khaled
 * @last_edit 02/14/2021
 */

package rul.models;

import weka.classifiers.Classifier;
import weka.classifiers.meta.RandomSubSpace;
import weka.core.Instances;

public class RandomSubSpaceModelImpl implements ModelStrategy{

    /**
     * This function takes the assets as the training dataset, and returns the trained
     * Random SubSpace classifier.
     * @author Khaled
     */
    @Override
    public  Classifier trainModel(Instances dataToTrain)
    {
        Classifier randomSubSpace = new RandomSubSpace();
        dataToTrain.setClassIndex(dataToTrain.numAttributes() - 1);

        try {
            randomSubSpace.buildClassifier(dataToTrain);
        }

        catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        return randomSubSpace;
    }
}
