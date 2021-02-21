package local;

import java.util.HashMap;

public interface AssetTypeDAO {

    HashMap<String, Double> getAssetTypeBoundaries(String assetTypeId);
}
