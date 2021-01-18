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
package app.item;

import java.util.ArrayList;

public class AssetInfo {
    private ArrayList<AssetAttribute> assetAttributes;

    public AssetInfo() {
        assetAttributes = new ArrayList<>();
    }

    public void addAttribute(AssetAttribute newAtt) {
        assetAttributes.add(newAtt);
    }

    public void setAssetAttributes(ArrayList<AssetAttribute>  newAtt) {
        assetAttributes = newAtt;
    }

    public ArrayList<AssetAttribute> getAssetAttributes() {
        return assetAttributes;
    }

    public int getLastRecorderTimeCycle() {
        if(assetAttributes.size() > 0)
            return assetAttributes.get(0).getMeasurements().size();
        else
            return 0;
    }

}