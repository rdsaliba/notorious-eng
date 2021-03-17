/*
  This class can be extended to the asset need.
  Will include all information relevant to an asset such as attributes information, data source origin,
  RUL estimates and others.
  It contains a reference to the asset attributes (attributes, operational settings)
  It contains a reference to the RUL estimates calculated for the asset including when
  that estimation was made (timestamp) as well as the value attached to it.

  @author Roy Saliba, Paul Micu, Jeremie Chouteau
  @last_edit 02/7/2020
 */
package app.item;

import java.util.ArrayList;
import java.util.List;

public class AssetInfo {
    private List<AssetAttribute> assetAttributes;

    public AssetInfo() {
        assetAttributes = new ArrayList<>();
    }

    public void addAttribute(AssetAttribute newAtt) {
        assetAttributes.add(newAtt);
    }

    public List<AssetAttribute> getAssetAttributes() {
        return assetAttributes;
    }

    public void setAssetAttributes(List<AssetAttribute> newAtt) {
        assetAttributes = newAtt;
    }
}