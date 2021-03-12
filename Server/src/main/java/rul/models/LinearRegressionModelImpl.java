/*
  Implementation of the model strategy interface, part of the Strategy design pattern
  Contains 2 methods, one to train the data and another private function to remove instances that are too old
  and cannot provide useful information

  @author      Paul Micu
  @last_edit   11/01/2020
 */
package rul.models;

import app.item.parameter.*;
import weka.classifiers.Classifier;
import weka.classifiers.functions.LinearRegression;
import weka.core.Instance;
import weka.core.Instances;

import java.util.Map;

public class LinearRegressionModelImpl extends ModelStrategy {

    //Parameters
//    private IntParameter batchSizePara = new IntParameter(1, "batchSize", false, true, 100);
//    private BoolParameter useQRDecompositionPara = new BoolParameter(1, "useQRDecomposition", false, true, false);
//    private BoolParameter eliminateColinearAttributesPara = new BoolParameter(1, "eliminateColinearAttributes", false, true, true);
//    private FloatParameter ridgePara = new FloatParameter(1, "ridge", false, true, 1.0E-8F);

    private final String BATCH_SIZE_PARAM_DEFAULT = "100";
    private final boolean USE_QR_DECOMPOSITION_DEFAULT = false;
    private final int ELIMINATE_ = 10;
    private final boolean RESUME_PARAM_DEFAULT = false;
    private final float SHRINKAGE_PARAM_DEFAULT = 1.0F;

    private StringParameter batchSizePara;
    private BoolParameter minimizeAbsoluteErrorPara;
    private IntParameter numIterationsPara;
    private BoolParameter resumePara;
    private FloatParameter shrinkagePara;

    private Classifier classifier;

    /**
     * This function takes the filtered training dataset and trains a linear regression regression model,
     * after that it returns the model.
     * To use this method you need to pass the training dataset.
     *
     * @author Talal
     */
    @Override
    public Classifier trainModel(Instances firstTrain) {
        firstTrain.setClassIndex(firstTrain.numAttributes() - 1);
        //removeInstances(firstTrain);
        Classifier lr = new LinearRegression();

        try {
            lr.buildClassifier(firstTrain);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return lr;
    }

    @Override
    public Map<String, Parameter> getDefaultParameters()
    {
        return null;
    }


    /**
     * This function removes the outliers (data that can affect the prediction of the model)
     * from a training dataset.
     * After outliers are removed, new dataset is returned.
     * This function is used to improve the performance of a model.
     * To use this function you need to pass the training dataset as a parameter.
     *
     * @author Talal
     */

    public static Instances removeInstances (Instances trainDataset, int threshold) {
        for (int i = 0; i < trainDataset.numInstances() ; i++) {
            Instance inst = trainDataset.instance(i);
            if (inst.value(inst.classAttribute()) > threshold) {
                trainDataset.delete(i);
            }
        }
        return trainDataset;
    }

    public static Instances removeInstances (Instances trainDataset) {
        return removeInstances(trainDataset,150);
    }
//
//    public int getBatchSizePara()
//    {
//        return batchSizePara.getIntValue();
//    }

//    public void setBatchSizePara(int batchSizeParaVal)
//    {
//        this.batchSizePara.setDefault(batchSizeParaVal == 100);
//        this.batchSizePara.setIntValue(batchSizeParaVal);
//    }

//    public boolean getUseQRDecompositionPara()
//    {
//        return useQRDecompositionPara.getBoolValue();
//    }

//    public void setUseQRDecompositionPara(boolean useQRDecompositionParaVal)
//    {
//        this.useQRDecompositionPara.setDefault(!useQRDecompositionParaVal);
//        this.useQRDecompositionPara.setBoolValue(useQRDecompositionParaVal);
//    }

//    public BoolParameter getEliminateColinearAttributesPara()
//    {
//        return eliminateColinearAttributesPara;
//    }
//
//    public void setEliminateColinearAttributesPara(boolean eliminateColinearAttributesParaVal)
//    {
//        this.eliminateColinearAttributesPara.setDefault(eliminateColinearAttributesParaVal);
//        this.eliminateColinearAttributesPara.setBoolValue(eliminateColinearAttributesParaVal);
//    }

//    public FloatParameter getRidgePara()
//    {
//        return ridgePara;
//    }

//    public void setRidgePara(float ridgeParaVal)
//    {
//        this.ridgePara.setDefault(ridgeParaVal == 1.0F);
//        this.ridgePara.setFloatValue(ridgeParaVal);
//    }

//    public void resetAllToDefault()
//    {
//        this.batchSizePara.setDefault(true);
//        this.batchSizePara.setIntValue(100);
//        this.useQRDecompositionPara.setDefault(true);
//        this.useQRDecompositionPara.setBoolValue(false);
//        this.eliminateColinearAttributesPara.setDefault(true);
//        this.eliminateColinearAttributesPara.setBoolValue(true);
//        this.ridgePara.setDefault(true);
//        this.ridgePara.setFloatValue(1.0F);
//    }

    @Override
    public Classifier getClassifier()
    {
        return classifier;
    }

    @Override
    public void setClassifier(Classifier classifier)
    {
        this.classifier = classifier;
    }
}


