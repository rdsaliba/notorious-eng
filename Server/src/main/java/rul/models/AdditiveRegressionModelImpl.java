/* Another regression model strategy implementation. This model is more flexible than Linear
 * Regression (which is a special case of Additive Regression).
 *
 * @author Khaled
 * @last_edit 02/14/2021
 */
package rul.models;

import app.item.parameter.BoolParameter;
import app.item.parameter.FloatParameter;
import app.item.parameter.IntParameter;
import weka.classifiers.Classifier;
import weka.classifiers.meta.AdditiveRegression;
import weka.core.Instances;

import java.io.FileReader;
import java.io.IOException;

public class AdditiveRegressionModelImpl extends ModelStrategy {

    //Parameters set to default values;
    private IntParameter batchSizePara = new IntParameter(1, "batchSize", false, true, 100);
    private BoolParameter minimizeAbsoluteErrorPara = new BoolParameter(1, "minimizeAbsoluteError", false, true, false);
    private IntParameter numIterationsPara = new IntParameter(1, "numIterations", false, true, 10);
    private BoolParameter resumePara = new BoolParameter(1, "resume", false, true, false);
    private FloatParameter shrinkagePara = new FloatParameter(1, "shrinkage", false, true, 1.0F);

    private Classifier classifier;

    /**
     * This function takes the assets as the training dataset, and returns the trained
     * Additive Regression classifier.
     *
     * @author Khaled
     */
    @Override
    public Classifier trainModel(Instances dataToTrain) {
        AdditiveRegression additiveRegression = new AdditiveRegression();
        dataToTrain.setClassIndex(dataToTrain.numAttributes() - 1);

        additiveRegression.setBatchSize("" + getBatchSizePara());
        additiveRegression.setMinimizeAbsoluteError(getMinimizeAbsoluteErrorPara());
        additiveRegression.setNumIterations(getNumIterationsPara());
        additiveRegression.setResume(getResumePara());
        additiveRegression.setShrinkage(getShrinkagePara());

        try {
            additiveRegression.buildClassifier(dataToTrain);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        this.classifier = additiveRegression;

        return additiveRegression;
    }

    //getter for batchSizeObj
    public int getBatchSizePara()
    {
        return this.batchSizePara.getIntValue();
    }

    public void setBatchSizePara(int batchSizeVal)
    {
        this.batchSizePara.setDefault((batchSizeVal == 100));
        this.batchSizePara.setIntValue(batchSizeVal);
    }

    public boolean getMinimizeAbsoluteErrorPara()
    {
        return this.minimizeAbsoluteErrorPara.getBoolValue();
    }

    public void setMinimizeAbsoluteErrorPara(boolean minimizeAbsoluteErrorVal)
    {
        this.batchSizePara.setDefault((!minimizeAbsoluteErrorVal));
        this.minimizeAbsoluteErrorPara.setBoolValue(minimizeAbsoluteErrorVal);
    }

    public int getNumIterationsPara()
    {
        return this.numIterationsPara.getIntValue();
    }

    public void setNumIterationsPara(int numIterationsVal)
    {
        this.numIterationsPara.setDefault(numIterationsVal == 10);
        this.numIterationsPara.setIntValue(numIterationsVal);
    }

    public boolean getResumePara()
    {
        return this.resumePara.getBoolValue();
    }

    public void setResumePara(boolean resumeParaVal)
    {
        this.resumePara.setDefault(!resumeParaVal);
        this.resumePara.setBoolValue(resumeParaVal);
    }

    public float getShrinkagePara()
    {
        return this.shrinkagePara.getFloatValue();
    }

    public void setShrinkagePara(float shrinkageParaVal)
    {
        this.shrinkagePara.setDefault(shrinkageParaVal == 1.0F);
        this.shrinkagePara.setFloatValue(shrinkageParaVal);
    }

    public void resetAllToDefault()
    {
        this.batchSizePara.setDefault(true);
        this.batchSizePara.setIntValue(100);
        this.minimizeAbsoluteErrorPara.setDefault(true);
        this.minimizeAbsoluteErrorPara.setBoolValue(false);
        this.numIterationsPara.setDefault(true);
        this.numIterationsPara.setIntValue(10);
        this.resumePara.setDefault(true);
        this.resumePara.setBoolValue(false);
        this.shrinkagePara.setDefault(true);
        this.shrinkagePara.setFloatValue(1.0F);
    }

    @Override
    public Classifier getClassifier() {
        return this.classifier;
    }

    @Override
    public void setClassifier(Classifier classifier)
    {
        this.classifier = classifier;
    }

    public static void main(String[] args) throws IOException
    {
        FileReader trainFile = new FileReader("Dataset/Converted/train_FD001_withRUL.arff");
        Instances   trainData = new Instances(trainFile);
        //trainData.setClassIndex(trainData.numAttributes() - 1);

        AdditiveRegressionModelImpl modelStrategy = new AdditiveRegressionModelImpl();
        //AdditiveRegression classifier = (AdditiveRegression) modelStrategy.trainModel(trainData);
        //modelStrategy.getClassifier();

        modelStrategy.setBatchSizePara(10);
        modelStrategy.resetAllToDefault();
        modelStrategy.setClassifier(modelStrategy.trainModel(trainData));

        //modelStrategy.setBatchSizePara(200);
       // modelStrategy.setNumIterationsPara(100);
        //System.out.println(modelStrategy.getNumIterationsPara());

        System.out.println(modelStrategy.getBatchSizePara());
        //modelStrategy.trainModel(trainData);

    }

}