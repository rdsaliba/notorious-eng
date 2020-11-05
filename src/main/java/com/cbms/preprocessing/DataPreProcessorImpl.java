/**
 * Implementation of the data pre-processor interface
 * the Process function will reduce the data and store it in the reducedDataSet variable
 *
 * @author      Paul Micu
 * @version     1.0
 * @last_edit   11/01/2020
 */
package com.cbms.preprocessing;

import weka.attributeSelection.BestFirst;
import weka.attributeSelection.CfsSubsetEval;
import weka.attributeSelection.GreedyStepwise;
import weka.core.AttributeStats;
import weka.core.Instances;
import weka.experiment.Stats;
import weka.filters.Filter;
import weka.filters.supervised.attribute.AttributeSelection;
import weka.filters.unsupervised.attribute.Remove;

import java.util.ArrayList;

public class DataPreProcessorImpl implements DataPreProcessor {
    private Instances originalDataset;
    private Instances reducedDataset;
    private Instances minimallyReducedDataset;

    public DataPreProcessorImpl(Instances originalDataset) {
        this.originalDataset = originalDataset;
        this.reducedDataset = originalDataset;
        this.minimallyReducedDataset = originalDataset;
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
    public void process() throws Exception {
        AttributeSelection filter = new AttributeSelection();  // a filter needs an evaluator and a search method
        CfsSubsetEval eval = new CfsSubsetEval(); // the evaluator chosen is the CfsSubsetEval
        BestFirst search = new BestFirst(); // the search method used is BestFirst

        //GreedyStepwise search = new GreedyStepwise();
        /* Could also use GreedyStepWise search along with this evaluator.
            Difference between using GreedyStepwise and BestFirst searches: BestFirst removes one extra sensor
            (Sensor 6, std dev of 0.001)
         */

        filter.setEvaluator(eval); // set the filter evaluator and search method
        filter.setSearch(search);

        filter.setInputFormat(originalDataset);
        reducedDataset = Filter.useFilter(originalDataset, filter); // this is what takes the data and applies the filter to reduce it


        // print out the attributes that are kept
        System.out.println("After performing CfsSubset evaluator with BestFirst search method on the dataset, " +
                "the following attributes are kept:");

        for (int i = 0; i < reducedDataset.numAttributes(); i++) {
            System.out.println(reducedDataset.attribute(i).name());
        }
    }

    /**
     * Using the AttributeSelection filter from Weka library can eliminate attributes that could potentially hold information
     * that aren't insignificant. The following method guarantees that ONLY the attributes that have no fluctuation (i.e
     * standard deviation of 0) are removed and the rest are kept to produce a dataset that is minimally reduced.
     * @author Khaled
     */
    @Override
    public void process2() throws Exception{

        ArrayList<Integer> removeIndex = new ArrayList<>();     //keep indices to delete in a list.

        for(int i = 0; i < originalDataset.numAttributes(); i++)
        {
            AttributeStats as = originalDataset.attributeStats(i);
            Stats stats = as.numericStats;

            System.out.println("Standard deviation of attribute: " + originalDataset.attribute(i).name() + ": " + stats.stdDev);

            if(stats.stdDev < 0.001)        //want to remove only the attributes that are 0 or very close to 0
            {
                removeIndex.add(i);         //add the index to the list
            }

        }

        System.out.println("The removed attributes will be: ");
        for(Integer e: removeIndex)
        {
            System.out.println(originalDataset.attribute(e).name());
        }

        //Remove filter to remove the attributes
        Remove remove = new Remove();
        int[] indicesToDelete = removeIndex.stream().mapToInt(i->(int) i).toArray();   //convert Integer list to int array
        remove.setAttributeIndicesArray(indicesToDelete);

        remove.setInputFormat(originalDataset);
        minimallyReducedDataset = Filter.useFilter(originalDataset, remove);
    }


    @Override
    public Instances getReducedDataset() {
        return reducedDataset;
    }

    @Override
    public Instances getMinimallyReducedDataset() {
        return minimallyReducedDataset;
    }

}
