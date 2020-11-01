package com.cbms.preprocessing;

import weka.attributeSelection.BestFirst;
import weka.attributeSelection.CfsSubsetEval;
import weka.core.Instances;
import weka.filters.Filter;
import weka.filters.supervised.attribute.AttributeSelection;

public class DataPreProcessorImpl implements DataPreProcessor {
    private Instances originalDataset;
    private Instances reducedDataset;

    public DataPreProcessorImpl(Instances originalDataset) {
        this.originalDataset = originalDataset;
    }

    /**
     * This method will filter the attributes and remove the ones that do not provide useful information
     * To use this method you need to choose an evaluation method and a search method
     * for now i choose the default CfsSubsetEval for the evaluator and BestFirst for the search method
     * the Weka docs contains other types of evaluators and search methods we can try in the future
     *
     * @author Paul
     */
    @Override
    public void process() {
        AttributeSelection filter = new AttributeSelection();  // a filter needs an evaluator and a search method
        CfsSubsetEval eval = new CfsSubsetEval(); // the evaluator chosen is the CfsSubsetEval
        BestFirst search = new BestFirst(); // the search method used is BestFirst
        filter.setEvaluator(eval); // set the filter evaluator and search method
        filter.setSearch(search);
        Instances reducedDataset = originalDataset;

        try {
            filter.setInputFormat(originalDataset);
            reducedDataset = Filter.useFilter(originalDataset, filter); // this is what takes the data and applies the filter to reduce it
        } catch (Exception e) {
            e.printStackTrace();
        }

        // print out the attributes that are kept
        System.out.println("After performing CfsSubset evaluator with BestFirst search method (on train_withRUL), " +
                "the following attributes are kept:");

        for (int i = 0; i < reducedDataset.numAttributes(); i++) {
            System.out.println(reducedDataset.attribute(i).name());
        }
    }

    @Override
    public Instances getReducedDataset() {
        return reducedDataset;
    }
}
