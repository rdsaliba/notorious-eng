/*
  Model for the asset types.

  @author
  @last_edit 02/7/2020
 */
package app.item;

import java.util.ArrayList;
import java.util.List;

public class AssetType {
    private String id;
    private String name;
    private String description;
    private List<AssetTypeParameter> thresholdList;

    public AssetType() {

    }

    public AssetType(AssetType assetType) {
        this.id = assetType.getId();
        this.name = assetType.getName();
        this.description = assetType.getDescription();
        this.thresholdList = assetType.getThresholdList();
    }

    public AssetType(String name) {
        this.name = name;
        thresholdList = new ArrayList<>();
    }

    public AssetType(String name, List<AssetTypeParameter> thresholdList) {
        this.name = name;
        this.thresholdList = thresholdList;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<AssetTypeParameter> getThresholdList() {
        return thresholdList;
    }

    public void setThresholdList(List<AssetTypeParameter> thresholdList) {
        this.thresholdList = thresholdList;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "AssetType{" +
                "name='" + name + '\'' +
                ", thresholdList=" + thresholdList +
                '}';
    }
}
