/*
  Implementation of the data pre-processor interface
  the Process function will reduce the data and store it in the reducedDataSet variable

  @author Paul Micu
  @version 1.0
  @last_edit 11/01/2020
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


    /**  This method takes in a dataset and shows all of its attribute values for every row of data.
     *
     * @author Khaled
     * */
    private static void showRUL(Instances data) {
        for(int i = 0; i < data.numInstances(); i++) {
            System.out.println(data.instance(i).toString());
        }
    }

    /**  This method takes in an array containing max cycle values (e.g getMaxCycles) an displays the maxCycle of
     * each engine.
     *
     * @author Khaled
     * */
    private static void showMaxCycles(double[] max) {
        int engineNum = 1;

        for(double e: max) {
            System.out.println("Max cycle of engine " + (engineNum++) + ": " + e);
        }
    }

    /**  this will add the RUL to the training instances object, this is needed for the model training
     *
     * @author Khaled
     * */
    private static Instances addRUL(Instances trainingData, double[] maxCycles) throws Exception {

        int engineNum = 1;
        Attribute engine = trainingData.attribute(SYSTEM_NAME);
        Instance row;

        int timeCycleIndex = 1;
        int engineIndex = 0;

        for (int i = 0; i < trainingData.numInstances(); i++) {
            row = trainingData.instance(i);

            if (engineNum == 100 && trainingData.instance(i - 1).classValue() == 0) {   //2nd dataset
                engineNum = 201;
                timeCycleIndex = 1;
                engineIndex++;
            }

            else if (engineNum == 460 && trainingData.instance(i - 1).classValue() == 0) {  //3rd dataset
                engineNum = 720;
                timeCycleIndex = 1;
                engineIndex++;
            }

            else if (engineNum == 819 && trainingData.instance(i - 1).classValue() == 0) {  //4th dataset
                engineNum = 920;
                timeCycleIndex = 1;
                engineIndex++;
            }

            if (row.value(engine) != engineNum) {
                engineNum++;
                engineIndex++;
                timeCycleIndex = 1;
            }

            row.setClassValue((maxCycles[engineIndex] - timeCycleIndex++));
            //to print out each row along with its RUL
            //System.out.println(row.toString());
        }
        return trainingData;
    }

    /**This method takes in the dataset and total number of engines in the dataset (1168) to parse the maximum cycle
     * of each engine in the dataset and returns it in an array.
     *
     * @author Khaled
     * */
    private static double[] getMaxCycles(Instances trainingData, int totalEngines) {
        Attribute engine = trainingData.attribute(0);
        Attribute timeCycle = trainingData.attribute("Time_Cycle");

        double engineNum = 1;
        double[] maxCycles = new double[totalEngines - 459]; //-459 to account for the AssetID discontinuity gaps
        int index = 0;

        for (int i = 0; i < trainingData.numInstances(); i++) {         //every instance
            Instance row = trainingData.instance(i);                    //current instance

            if (row.value(engine) - 1 == engineNum) {                   //if val of current engine - 1 is same as
                Instance prevRow = trainingData.instance(i - 1);
                maxCycles[index++] = prevRow.value(timeCycle);
                engineNum++;
            }
            else if (engineNum == 100) {                        //moving to second dataset
                maxCycles[index++] = 200;
                engineNum = 201;
            }
            else if(engineNum == 460) {                          //moving to 3rd dataset
                maxCycles[index++] = 316;
                engineNum = 720;
            }
            else if(engineNum == 819) {                         //moving to 4th dataset
                maxCycles[index++] = 152;
                engineNum = 920;
            }

            else if(engineNum == 1168) {                         //end (last asset id = 1168)
                maxCycles[index++] = 255;
                break;
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
    public static Instances addRULCol(Instances newData) throws Exception {
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