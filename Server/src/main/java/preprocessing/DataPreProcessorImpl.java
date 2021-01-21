/*
  Implementation of the data pre-processor interface
  the Process function will reduce the data and store it in the reducedDataSet variable

  @author Paul Micu
  @version 1.0
  @last_edit 11/01/2020
 */
package preprocessing;

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

public class DataPreProcessorImpl implements DataPreProcessor {
    private Instances originalDataset;
    private Instances reducedDataset;
    private Instances minimallyReducedDataset;
    private final ArrayList<Integer> removedIndex;

    public DataPreProcessorImpl(Instances originalDataset) {
        this.originalDataset = originalDataset;
        this.reducedDataset = originalDataset;
        this.minimallyReducedDataset = originalDataset;
        this.removedIndex = new ArrayList<>();
    }

    /**  this will add the RUL to the training instances object, this is needed for the model training
     *
     * @author Khaled
     * */
    private static Instances addRUL(Instances trainingData, ArrayList<Double> maxCycles) throws Exception {

        Instance firstRow = trainingData.firstInstance();
        double assetID = firstRow.value(0);
        //int engineNum = 1;
        Attribute engine = trainingData.attribute("Asset_id");
        Instance row;

        int timeCycleIndex = 1;
        int maxCycleIndex = 0;

        for (int i = 0; i < trainingData.numInstances(); i++) {
            row = trainingData.instance(i);

            if (row.value(engine) != assetID) {
                assetID++;
                timeCycleIndex = 1;
                maxCycleIndex++;
            }

            row.setClassValue((maxCycles.get(maxCycleIndex) - timeCycleIndex++));
            //System.out.println(row.toString());           //print each instance after adding RUL column
        }
        return trainingData;
    }


    /**
     * This method takes in instances and parses the maximum cycle of each engine (assetID) in those instances and
     * returns it in an ArrayList.
     * @author Khaled
     * */
    private static ArrayList<Double> getMaxCycles(Instances data) {
        Attribute engine = data.attribute("Asset_id");
        Attribute timeCycle = data.attribute("Time_Cycle");

        Instance firstRow = data.firstInstance();
        Instance lastRow = data.lastInstance();

        double assetID = firstRow.value(0);
        double lastAssetID = lastRow.value(0);

        ArrayList<Double> maxCycles = new ArrayList<>();

        for (int i = 0; i < data.numInstances(); i++) {
            Instance row = data.instance(i);

            if (row.value(engine) - 1 == assetID) {
                Instance prevRow = data.instance(i - 1);
                maxCycles.add(prevRow.value(timeCycle));
                assetID++;
            }

            else if (assetID == lastAssetID) {
                Instance last = data.lastInstance();
                maxCycles.add(last.value(timeCycle));
                break;
            }

            else if(row.value(engine) != assetID) {
                assetID++;
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

        filter.setEvaluator(eval); // set the filter evaluator and search method
        filter.setSearch(search);

        filter.setInputFormat(originalDataset);
        reducedDataset = Filter.useFilter(originalDataset, filter); // this is what takes the data and applies the filter to reduce it

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

            if (stats.stdDev < 0.001)        //want to remove only the attributes that are 0 or very close to 0
                removedIndex.add(i);         //add the index to the list
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
        Add filter = new Add();
        filter.setAttributeIndex("last");
        filter.setAttributeName("RUL");
        filter.setInputFormat(newData);
        newData = Filter.useFilter(newData, filter);
        newData.setClass(newData.attribute("RUL"));

        //Get max cycle of each engine (highest time cycle - 1)
        ArrayList<Double> maxCycles = getMaxCycles(newData);

        newData = addRUL(newData, maxCycles);
        return newData;
    }

    /**Given 2 instances Object, it will remove the attributes that are not shared between the two and return the test set
     *
     * @author Paul Micu
     * */
    public static Instances removeAttributes(Instances trainDataset, Instances testDataset) throws Exception {
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

    private static boolean setContains(Instances dataset, Attribute att) {
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
