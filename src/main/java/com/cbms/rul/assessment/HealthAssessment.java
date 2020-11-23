/*
  The HealthAssessment class is responsible to handle any assessment requests coming from the assessment Controller

  @author Paul Micu
 * @version 1.0
 * @last_edit 11/08/2020
 */
package com.cbms.rul.assessment;

import weka.classifiers.Classifier;
import weka.core.Attribute;
import weka.core.Instance;
import weka.core.Instances;

import static com.cbms.AppConstants.SYSOUT_DEBUG;
import static com.cbms.AppConstants.SYSTEM_NAME;

public class HealthAssessment {

    public HealthAssessment() {
    }

    /** When given an test Instances and a Classifier, this method will return an estimated RUL
     * 
     * @author Talal
     * */
    public double predictRUL(Instances testData, Classifier lr) throws Exception
    {
        Attribute engine = testData.attribute(SYSTEM_NAME);

        double engineNum = 0;
        double predicted=-1.0;
        for (int i = 0; i < testData.numInstances(); i++)
        {
            if(i+1==testData.numInstances()-1) {
                Instance one = testData.instance(i+1);
                double life= lr.classifyInstance(one);
                if (SYSOUT_DEBUG)
                    System.out.println(lr.classifyInstance(one));
                double temp = testData.lastInstance().value(engine);
                predicted = life;
                break;
            }
            Instance row = testData.instance(i);
            Instance nextRow = testData.instance(i+1);
            if(row.value(engine) != nextRow.value(engine)){
                if (SYSOUT_DEBUG)
                    System.out.println("a b c d"+row.value(engine));
                double life= lr.classifyInstance(row);
                if (SYSOUT_DEBUG)
                    System.out.println(lr.classifyInstance(row));
                predicted = life;
                engineNum++;
            }
        }
        return predicted;
    }
}
