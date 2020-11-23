/*
  The asset Attribute class contains all the details of the asset attribute
  this includes the id of the asset, its name and all the measurements we have for it

  @author Paul Micu
 * @version 1.0
 * @last_edit 11/08/2020
 */
package com.cbms.app.item;

import java.util.Map;
import java.util.TreeMap;

public class AssetAttribute {
    private int id;
    private String name;
    private Map<Integer, Double> measurements;

    public AssetAttribute() {
        measurements = new TreeMap<>();
    }

    public void addMeasurement(int time, double measurement) {
        measurements.put(time, measurement);
    }

    public Map<Integer, Double> getMeasurements() {
        return measurements;
    }

    public Double getMeasurements(int time) {
        return measurements.get(time);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "AssetAttribute{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", measurements=" + measurements +
                '}';
    }
}
