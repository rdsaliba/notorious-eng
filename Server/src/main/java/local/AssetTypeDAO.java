/*
  Interface for the assetTypeDAO object

  @author
  @last_edit 02/7/2020
 */
package local;

import java.util.HashMap;

public interface AssetTypeDAO {

    HashMap<String, Double> getAssetTypeBoundaries(String assetTypeId);
}
