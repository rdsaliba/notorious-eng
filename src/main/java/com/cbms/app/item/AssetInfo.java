/**
 * This class can be extended to the system need.
 * Will include all sensors information, data source origin and whatever is relevant to the asset
 *
 * @author Roy Saliba
 * @version 1.0
 * @last_edit 11/07/2020
 *
 * Added a reference to the asset attributes
 * Added a map containing a reference to all the RUL measurements and when they were taken
 * @author Paul Micu
 * @version 1.1
 * @last_edit 11/08/2020
 */
package com.cbms.app.item;

import java.util.*;

public class AssetInfo {
    private ArrayList<AssetAttribute> assetAttributes;
    private Map<Date, Double> estimates;
    private Date lastRULDate;

    public AssetInfo() {
        assetAttributes = new ArrayList<>();
        estimates = new TreeMap<>();
    }

    public void addAttribute(AssetAttribute newAtt) {

        assetAttributes.add(newAtt);
    }

    public ArrayList<AssetAttribute> getAssetAttributes() {

        return assetAttributes;
    }

    public int getLastRecorderTimeCycle() {

        return assetAttributes.get(0).getMeasurements().size();
    }

    public void addRULMeasurement(double estimate) {
        Calendar cal = Calendar.getInstance();
        lastRULDate = cal.getTime();
        estimates.put(lastRULDate, estimate);
    }

    public double getRULMeasurement() {
        return estimates.get(lastRULDate);
    }

    @Override
    public String toString() {
        return "AssetInfo{" +
                "assetAttributes=" + assetAttributes.toString() +
                ", estimates=" + estimates.toString() +
                '}';
    }
}