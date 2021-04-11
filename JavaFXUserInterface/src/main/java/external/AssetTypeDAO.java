package external;

import app.item.AssetType;

import java.util.ArrayList;

public interface AssetTypeDAO {

    int getAssetTypeIdCount(String assetTypeID, boolean isLive);

    String getAssetTypeThreshold(String assetTypeId, String thresholdType);

    int insertAssetType(AssetType assetType);

    ArrayList<AssetType> getAssetTypeList();

    String getNameFromID(String id);

    int getIDFromName(String name);

    void updateAssetType(AssetType assetType);

    void deleteAssetTypeByID(String assetTypeID);

}
