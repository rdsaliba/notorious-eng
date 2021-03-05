/*
  Interface for the data pre-processor
  the Process function will reduce the data and store it in the reducedDataSet variable

  @author      Paul Micu
  @last_edit   11/01/2020
 */
package preprocessing;

import weka.core.Instances;
import weka.filters.unsupervised.attribute.Remove;

public interface DataPreProcessor {

    void processFullReduction();
    void processMinimalReduction();
    Instances getReducedDataset();
    Instances getMinimallyReducedDataset();
    Remove getRemovedIndexList();
}
