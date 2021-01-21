/*
  Interface for the data pre-processor
  the Process function will reduce the data and store it in the reducedDataSet variable

  @author      Paul Micu
  @version     1.0
  @last_edit   11/01/2020
 */
package preprocessing;

import weka.core.Instances;
import weka.filters.unsupervised.attribute.Remove;

public interface DataPreProcessor {
    void processFullReduction() throws Exception;
    void processMinimalReduction() throws Exception;
    Instances getReducedDataset();
    Instances getMinimallyReducedDataset();
    Remove getRemovedIndexList() throws Exception;
}
