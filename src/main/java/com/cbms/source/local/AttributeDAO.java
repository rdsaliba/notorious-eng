package com.cbms.source.local;

import com.cbms.app.item.Measurement;

import java.util.ArrayList;

public interface AttributeDAO {

    ArrayList<Measurement> getLastXMeasurementsByAssetIDAndAttributeID(String assetID,String sensorID, int limiter );
}
