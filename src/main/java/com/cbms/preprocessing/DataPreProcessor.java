package com.cbms.preprocessing;


import weka.core.Instances;

public interface DataPreProcessor {
    Instances reducedDataset = null;
    
    public void process();
    public Instances getReducedDataset();
}
