package local;

import app.item.AssetType;

import java.util.ArrayList;

public interface AssetTypeDAO {

    int getAssetTypeIdCount(String assetTypeID, boolean isLive);

    String getAssetTypeBoundary(String asset_type_id, String boundary_type);

    void insertAssetType(AssetType assetType);

    ArrayList<AssetType> getAssetTypeList();

    String getNameFromID(String id);

    void updateAssetType(AssetType assetType);

}
