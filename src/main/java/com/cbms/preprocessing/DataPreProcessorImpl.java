/*
  Implementation of the data pre-processor interface
  the Process function will reduce the data and store it in the reducedDataSet variable

  @author Paul Micu
 * @version 1.0
 * @last_edit 11/01/2020
 */
package com.cbms.preprocessing;

import weka.attributeSelection.BestFirst;
import weka.attributeSelection.CfsSubsetEval;
import weka.core.Attribute;
import weka.core.AttributeStats;
import weka.core.Instance;
import weka.core.Instances;
import weka.experiment.Stats;
import weka.filters.Filter;
import weka.filters.supervised.attribute.AttributeSelection;
import weka.filters.unsupervised.attribute.Add;
import weka.filters.unsupervised.attribute.Remove;

import java.util.ArrayList;

import static com.cbms.AppConstants.SYSOUT_DEBUG;
import static com.cbms.AppConstants.SYSTEM_NAME;

public class DataPreProcessorImpl implements DataPreProcessor {
    private final Instances originalDataset;
    private Instances reducedDataset;
    private Instances minimallyReducedDataset;
    private final ArrayList<Integer> removedIndex;

    public DataPreProcessorImpl(Instances originalDataset) throws Exception {
        this.originalDataset = originalDataset;
        this.reducedDataset = originalDataset;
        this.minimallyReducedDataset = originalDataset;
        this.removedIndex = new ArrayList<>();
    }

    /**  this will add the RUL to the training instances object, this is needed for the model training
     *
     * @author Khaled
     * */
    private static Instances addRUL(Instances trainingData, double[] maxCycles) throws Exception {

      /*  Add filter = new Add();
        filter.setAttributeIndex("last");
        filter.setAttributeName("@RUL");
        filter.setInputFormat(trainingData);
        trainingData = Filter.useFilter(trainingData, filter);*/
        int engineNum = 1;
        Attribute engine = trainingData.attribute(SYSTEM_NAME);
        Instance row;

        int timeCycleIndex = 1;

        for (int i = 0; i < trainingData.numInstances(); i++) {
            row = trainingData.instance(i);

            if (row.value(engine) != engineNum) {
                engineNum++;
                timeCycleIndex = 1;
            }

            row.setClassValue((maxCycles[engineNum - 1] - timeCycleIndex++));
        }
        return trainingData;
    }

    /**
     * @author Khaled
     * */
    private static double[] getMaxCycles(Instances trainingData, int totalEngines) {
        Attribute engine = trainingData.attribute(SYSTEM_NAME);
        Attribute timeCycle = trainingData.attribute("Time_Cycle");

        double engineNum = 1;
        double[] maxCycles = new double[totalEngines];

        for (int i = 0; i < trainingData.numInstances(); i++) {
            Instance row = trainingData.instance(i);
            if (row.value(engine) - 1 == engineNum) {
                Instance prevRow = trainingData.instance(i - 1);
                maxCycles[(int) engineNum - 1] = prevRow.value(timeCycle);
                engineNum++;
            } else if (engineNum == 100) {
                Instance lastRow = trainingData.lastInstance();
                maxCycles[(int) engineNum - 1] = lastRow.value(timeCycle);
            }
        }
        return maxCycles;
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
    public void processFullReduction() throws Exception {
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
        if (SYSOUT_DEBUG) {
            System.out.println("After performing CfsSubset evaluator with BestFirst search method on the dataset, " +
                    "the following attributes are kept:");

            for (int i = 0; i < reducedDataset.numAttributes(); i++) {
                System.out.println(reducedDataset.attribute(i).name());
            }
        }
        reducedDataset = addRULCol(reducedDataset);
    }

    /**
     * Using the AttributeSelection filter from Weka library can eliminate attributes that could potentially hold information
     * that aren't insignificant. The following method guarantees that ONLY the attributes that have no fluctuation (i.e
     * standard deviation of 0) are removed and the rest are kept to produce a dataset that is minimally reduced.
     *
     * @author Khaled
     */
    @Override
    public void processMinimalReduction() throws Exception {

        for (int i = 0; i < originalDataset.numAttributes(); i++) {
            AttributeStats as = originalDataset.attributeStats(i);
            Stats stats = as.numericStats;

            if (SYSOUT_DEBUG)
                System.out.println("Standard deviation of attribute: " + originalDataset.attribute(i).name() + ": " + stats.stdDev);

            if (stats.stdDev < 0.001)        //want to remove only the attributes that are 0 or very close to 0
            {
                removedIndex.add(i);         //add the index to the list
            }

        }

        if (SYSOUT_DEBUG) {
            System.out.println("The removed attributes will be: ");
            for (Integer e : removedIndex) {
                System.out.println(originalDataset.attribute(e).name());
            }
        }

        //Remove filter to remove the attributes
        Remove remove = new Remove();
        int[] indicesToDelete = removedIndex.stream().mapToInt(i -> i).toArray();   //convert Integer list to int array
        remove.setAttributeIndicesArray(indicesToDelete);

        remove.setInputFormat(originalDataset);
        minimallyReducedDataset = Filter.useFilter(originalDataset, remove);
        minimallyReducedDataset = addRULCol(minimallyReducedDataset);
    }

    /**Given an Instance object, this will add an RUL attribute at the end of the other attributes
     *
     * @author Khaled
     * */
    public Instances addRULCol(Instances newData) throws Exception {
        Instance lastRow = newData.lastInstance();
        Add filter = new Add();
        filter.setAttributeIndex("last");
        filter.setAttributeName("RUL");
        filter.setInputFormat(newData);
        newData = Filter.useFilter(newData, filter);
        newData.setClass(newData.attribute("RUL"));

        int totalEngines = (int) lastRow.value(0);
        double[] maxCycles = getMaxCycles(newData, totalEngines);

        newData = addRUL(newData, maxCycles);
        return newData;

    }

    /**Given 2 instances Object, it will remove the attributes that are not shared between the two and return the test set
     *
     * @author Paul Micu
     * */
    public Instances removeAttributes(Instances trainDataset, Instances testDataset) throws Exception {
        ArrayList<Integer> indexes = new ArrayList<>();
        for (int i = 0; i < testDataset.numAttributes(); i++) {
            if (!setContains(trainDataset, testDataset.attribute(i))) {
                indexes.add(i);
            }
        }
        int[] arr = new int[indexes.size()];

        for (int i = 0; i < indexes.size(); i++)
            arr[i] = indexes.get(i);

        Remove remove = new Remove();
        remove.setAttributeIndicesArray(arr);
        remove.setInputFormat(testDataset);
        return Filter.useFilter(testDataset, remove);
    }

    private boolean setContains(Instances dataset, Attribute att) {
        for (int i = 0; i < dataset.numAttributes(); i++) {
            if (att.name().equals(dataset.attribute(i).name())) {
                return true;
            }

        }
        return false;
    }

    @Override
    public Instances getReducedDataset() {
        return reducedDataset;
    }

    @Override
    public Instances getMinimallyReducedDataset() {
        return minimallyReducedDataset;
    }

    @Override
    public Remove getRemovedIndexList() throws Exception {
        Remove remove = new Remove();
        int[] indicesToDelete = removedIndex.stream().mapToInt(i -> i).toArray();   //convert Integer list to int array
        remove.setAttributeIndicesArray(indicesToDelete);

        remove.setInputFormat(originalDataset);
        return remove;
    }



}
