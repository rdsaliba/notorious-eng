package local;

import app.item.AssetType;

import java.util.ArrayList;

public interface AssetTypeDAO {
    void insertAssetType(AssetType assetType);
    ArrayList<String> getAssetTypeList();
    String getNameFromID(String id);
}
