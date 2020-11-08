/**
 * Interface for the data pre-processor
 * the Process function will reduce the data and store it in the reducedDataSet variable 
 * 
 * @author      Paul Micu
 * @version     1.0
 * @last_edit   11/01/2020
 */
package com.cbms.preprocessing;

import weka.core.Instances;
import weka.filters.unsupervised.attribute.Remove;

public interface DataPreProcessor {
    Instances reducedDataset = null;
    Instances minimallyReducedDataset = null;
    Instances removedIndex = null;
    
    public void processFullReduction() throws Exception;
    public void processMinimalReduction() throws Exception;
    public Instances getReducedDataset();
    public Instances getMinimallyReducedDataset();
    public Remove getRemovedIndexList() throws Exception;
}
