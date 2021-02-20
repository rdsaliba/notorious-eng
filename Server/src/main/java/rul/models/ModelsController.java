/*
  This Controller is part of the Strategy design pattern it
  allows for a model to be chosen dynamically

  @author Paul Micu
  @last_edit 11/01/2020
 */
package rul.models;

import weka.classifiers.Classifier;
import weka.core.Instances;

public class ModelsController {
    private final ModelStrategy modelStrategy;

    public ModelsController(ModelStrategy modelStrategy) {
        this.modelStrategy = modelStrategy;
    }

    public Classifier trainModel(Instances reducedData) {
        return modelStrategy.trainModel(reducedData);
    }

}
