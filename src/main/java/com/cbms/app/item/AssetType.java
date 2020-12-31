package com.cbms.app.item;

import java.util.ArrayList;

public class AssetType {
    private String name;
    private ArrayList<Double> thresholdList;

    public AssetType(String name) {
        this.name = name;
        thresholdList = new ArrayList<>();
    }

    public AssetType(String name, ArrayList<Double> thresholdList) {
        this.name = name;
        this.thresholdList = thresholdList;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<Double> getThresholdList() {
        return thresholdList;
    }

    public void setThresholdList(ArrayList<Double> thresholdList) {
        this.thresholdList = thresholdList;
    }

    public void addThresholdValue(double threshold) {
        thresholdList.add(threshold);
    }

    @Override
    public String toString() {
        return "AssetType{" +
                "name='" + name + '\'' +
                ", thresholdList=" + thresholdList +
                '}';
    }
}
