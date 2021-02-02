package local;

import app.item.Measurement;

import java.util.ArrayList;

public interface AttributeDAO {

    ArrayList<Measurement> getLastXMeasurementsByAssetIDAndAttributeID(String assetID,String sensorID, int limiter );
}
