/*
  Interface for the AttributeDAO object

  @author
  @last_edit 02/7/2020
 */
package local;

import app.item.Measurement;

import java.util.ArrayList;

public interface AttributeDAO {

    ArrayList<Measurement> getLastXMeasurementsByAssetIDAndAttributeID(String assetID, String attributeID, int limiter);
}
