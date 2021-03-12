/*
  This Controller is part of the Strategy design pattern it
  allows for a model to be chosen dynamically

  @author Paul Micu
  @last_edit 11/01/2020
 */
package rul.models;

import app.item.parameter.Parameter;
import weka.classifiers.Classifier;
import weka.core.Instances;

import java.util.ArrayList;
import java.util.Map;

public class ModelsController
{
    private final ModelStrategy modelStrategy;

    public ModelsController(ModelStrategy modelStrategy)
    {
        this.modelStrategy = modelStrategy;
    }

    public Classifier trainModel(Instances reducedData)
    {
        return modelStrategy.trainModel(reducedData);
    }

    public ModelStrategy getModelStrategy()
    {
        return modelStrategy;
    }

    public Map<String, Parameter> getParameters()
    {
        return modelStrategy.getParameters();
    }

    public Map<String, Parameter> getDefaultParameters()
    {
        return modelStrategy.getDefaultParameters();
    }

    public void setParameters(Map<String, Parameter> newParam)
    {
        modelStrategy.setParameters(newParam);
    }


}
