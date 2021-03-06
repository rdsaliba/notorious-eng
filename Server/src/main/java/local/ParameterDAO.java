package local;

import app.item.parameter.Parameter;

import java.util.ArrayList;
import java.util.Map;

public interface ParameterDAO {
    ArrayList<Parameter> getParametersForModelAssetType(int modelID, int assetTypeID);
}
