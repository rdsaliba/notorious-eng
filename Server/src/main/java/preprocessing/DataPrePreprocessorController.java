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

    public DataPrePreprocessorController() {
        // Default Constructor
    }

    public static DataPrePreprocessorController getInstance() {
        if (instance == null)
            instance = new DataPrePreprocessorController();
        return instance;
    }

    /*
        Reduced data is returned from the implementation methods after using Weka's built in
        filtering that eliminates columns that don't correlate with the RUL values.
     */
    public Instances reduceData(Instances originalData) {
        dataPreProcessorImpl = new DataPreProcessorImpl(originalData);
        dataPreProcessorImpl.processFullReduction();
        return dataPreProcessorImpl.getReducedDataset();
    }

    /*
       Minimally reduced data is returned from the implementation methods after manually eliminating
       columns that have standard deviation of 0 or close to 0.
    */
    public Instances minimallyReduceData(Instances originalData) {
        dataPreProcessorImpl = new DataPreProcessorImpl(originalData);
        dataPreProcessorImpl.processMinimalReduction();
        return dataPreProcessorImpl.getMinimallyReducedDataset();
    }

    /*
       Instances in their original data state (no RUL values) are passed in, then the implementation
       method is called and returned which adds an RUL column with filled in values of RUL at each
       instance based on the max cycle of each asset. These RUL values are needed to train the
       models.
     */
    public Instances addRULCol(Instances toADD) throws Exception {
        return DataPreProcessorImpl.addRULCol(toADD);
    }

    /*
        Training and Testing instances are passed in, if they don't contain the same columns, the
        attributes not shared between them will get eliminated and the instances without
        these attributes will be returned.
     */
    public Instances removeAttributes(Instances trainDataset, Instances testDataset) throws Exception {
        return DataPreProcessorImpl.removeAttributes(trainDataset,testDataset);
    }
}
