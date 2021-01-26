package local;

import app.item.AssetType;

import java.util.ArrayList;

public interface AssetTypeDAO {
    int insertAssetType(AssetType assetType);
    ArrayList<AssetType> getAssetTypeList();
    String getNameFromID(String id);
}
