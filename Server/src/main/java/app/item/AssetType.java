package app.item;

import java.util.ArrayList;

public class AssetType {
    private String id;
    private String name;
    private ArrayList<AssetTypeParameter> thresholdList;

    public AssetType() {
    }

    public AssetType(String name) {
        this.name = name;
        thresholdList = new ArrayList<>();
    }

    public AssetType(String name, ArrayList<AssetTypeParameter> thresholdList) {
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

    public ArrayList<AssetTypeParameter> getThresholdList() {
        return thresholdList;
    }

    public void setThresholdList(ArrayList<AssetTypeParameter> thresholdList) {
        this.thresholdList = thresholdList;
    }

    @Override
    public String toString() {
        return "AssetType{" +
                "name='" + name + '\'' +
                ", thresholdList=" + thresholdList +
                '}';
    }
}
