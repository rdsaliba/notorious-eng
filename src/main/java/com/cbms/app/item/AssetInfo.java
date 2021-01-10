/*
  This class can be extended to the system need.
  Will include all information relevant to an asset such as sensors information, data source origin,
  RUL estimates and others.
  It contains a reference to the asset attributes (sensors, operational settings)
  It contains a reference to the RUL estimates calculated for the asset including when
  that estimation was made (timestamp) as well as the value attached to it.

  @author Roy, Saliba, Paul Micu, Jeremie Chouteau
  @version 2.1
  @last_edit 12/24/2020
 */
package com.cbms.app.item;

import java.util.*;

public class AssetInfo {
    private final ArrayList<AssetAttribute> assetAttributes;
    private final ArrayList<RULEstimate> RULEstimates;
    private Date lastRULDate;

    public AssetInfo() {
        assetAttributes = new ArrayList<>();
        RULEstimates = new ArrayList<>();
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

    public void addRULEstimate(double estimate) {
        Calendar cal = Calendar.getInstance();
        lastRULDate = cal.getTime();
        RULEstimate RULeNew = new RULEstimate(lastRULDate, estimate);
        RULEstimates.add(RULeNew);
    }

    public double getRULEstimate() {
        for (RULEstimate rulEstimate : RULEstimates) {
            if (rulEstimate.getTimestamp().equals(lastRULDate)) {
                return rulEstimate.getValue();
            }
        }
        return 0;
    }

    @Override
    public String toString() {
        return "AssetInfo{" +
                "assetAttributes=" + assetAttributes.toString() +
                ", estimates=" + RULEstimates.toString() +
                '}';
    }
}