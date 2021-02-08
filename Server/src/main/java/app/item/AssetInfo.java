/*
  This class can be extended to the system need.
  Will include all information relevant to an asset such as sensors information, data source origin,
  RUL estimates and others.
  It contains a reference to the asset attributes (sensors, operational settings)
  It contains a reference to the RUL estimates calculated for the asset including when
  that estimation was made (timestamp) as well as the value attached to it.

  @author Roy Saliba, Paul Micu, Jeremie Chouteau
  @last_edit 02/7/2020
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

}