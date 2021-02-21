/* A Feed Forward Neural Network, unlike LSTM which is Recurrent. Multilayer Perceptron (MLP)
 * is learnt using back propogation to classify instances. It's part of Weka's Classifiers and
 * not part of DL4J.
 *
 * @author Khaled
 * @last_edit 02/14/2021
 */

package rul.models;

import weka.classifiers.Classifier;
import weka.classifiers.functions.MultilayerPerceptron;
import weka.core.Instances;

public class MultilayerPerceptronModelImpl implements ModelStrategy{
    /**
     * This function takes the assets as the training dataset, and returns the trained
     * Multilayer Perceptron classifier.
     * @author Khaled
     */
    @Override
    public Classifier trainModel(Instances dataToTrain) {
        Classifier mlp = new MultilayerPerceptron();
        dataToTrain.setClassIndex(dataToTrain.numAttributes() - 1);

        try {
            mlp.buildClassifier(dataToTrain);
        }

        catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        return mlp;
    }

}
