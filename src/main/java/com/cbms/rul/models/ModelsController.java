/**
 * This Controller is part of the Strategy design pattern it
 * allows for a model to be chosen dynamically
 *
 * @author Paul Micu
 * @version 1.0
 * @last_edit 11/01/2020
 */
package com.cbms.rul.models;

import weka.classifiers.Classifier;
import weka.core.Instances;

public class ModelsController {
    private ModelStrategy modelStrategy;

    public ModelsController(ModelStrategy modelStrategy) {
        this.modelStrategy = modelStrategy;
    }

    public Classifier trainModel(Instances reducedData) throws Exception {
        return modelStrategy.trainModel(reducedData);
    }

}
