/*
  This Controller will handle the preprocessing of the data.
  it requires the dataset to reduce in an Instances object
  it will return an Instances object with the attributes and their corresponding data selected by the algorithm removed

  @author Paul Micu
  @last_edit 11/01/2020
 */
package preprocessing;

import weka.core.Instances;

public class DataPrePreprocessorController {
    private static DataPrePreprocessorController instance = null;
    private DataPreProcessorImpl dataPreProcessorImpl = null;

    private DataPrePreprocessorController() {
    }

    public static DataPrePreprocessorController getInstance() {
        if (instance == null)
            instance = new DataPrePreprocessorController();
        return instance;
    }


    public Instances reduceData(Instances originalData) throws Exception {
        dataPreProcessorImpl = new DataPreProcessorImpl(originalData);
        dataPreProcessorImpl.processFullReduction();
        return dataPreProcessorImpl.getReducedDataset();
    }

    public Instances minimallyReduceData(Instances originalData) throws Exception {
        dataPreProcessorImpl = new DataPreProcessorImpl(originalData);
        dataPreProcessorImpl.processMinimalReduction();
        return dataPreProcessorImpl.getMinimallyReducedDataset();
    }

    public Instances addRULCol(Instances toADD) throws Exception {
        return DataPreProcessorImpl.addRULCol(toADD);
    }

    public Instances removeAttributes(Instances trainDataset, Instances testDataset) throws Exception {
        return DataPreProcessorImpl.removeAttributes(trainDataset,testDataset);
    }
}
