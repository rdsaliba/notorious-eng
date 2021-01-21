/*
  The HealthAssessment class is responsible to handle any assessment requests coming from the assessment Controller

  @author Paul Micu
  @version 1.0
  @last_edit 11/08/2020
 */
package rul.assessment;

import weka.classifiers.Classifier;
import weka.core.Attribute;
import weka.core.Instance;
import weka.core.Instances;

public class HealthAssessment {

    /** When given an test Instances and a Classifier, this method will return an estimated RUL
     * 
     * @author Talal
     * */
    public double predictRUL(Instances testData, Classifier lr) throws Exception
    {
        Attribute engine = testData.attribute("Asset_id");

        double predicted=-1.0;
        for (int i = 0; i < testData.numInstances(); i++)
        {
            if(i+1==testData.numInstances()-1) {
                Instance one = testData.instance(i+1);
                double life= lr.classifyInstance(one);
                testData.lastInstance().value(engine);
                predicted = life;
                break;
            }
            Instance row = testData.instance(i);
            Instance nextRow = testData.instance(i+1);
            if(row.value(engine) != nextRow.value(engine)){
                predicted = lr.classifyInstance(row);
            }
        }
        return predicted;
    }
}
