package external;

import app.item.AssetType;

import java.util.ArrayList;

public interface AssetTypeDAO {

    int getAssetTypeIdCount(String assetTypeID, boolean isLive);

    String getAssetTypeBoundary(String assetTypeId, String boundaryType);

    int insertAssetType(AssetType assetType);

    ArrayList<AssetType> getAssetTypeList();

    String getNameFromID(String id);

    void updateAssetType(AssetType assetType);

    void deleteAssetTypeByID(String assetTypeID);

}
