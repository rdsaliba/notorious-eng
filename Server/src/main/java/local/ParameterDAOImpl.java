package local;

import app.item.parameter.*;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ParameterDAOImpl extends DAO implements ParameterDAO {
    private static final String GET_ALL_PARAMS = "SELECT * from parameter_model_type_value_assoc pmtvs, model_parameter mp, parameter_value pv where pmtvs.model_id = 1 and pmtvs.asset_type_id=1 and pmtvs.parameter_id=mp.parameter_id and pmtvs.value_id=pv.value_id";

    /**
     * When given an model and asset type id, this function will get all the parameters that match the ids
     * after we can filter by default, live, etc to get the exact parameters to use
     *
     * @author Paul
     */
    @Override
    public ArrayList<Parameter> getParametersForModelAssetType(int modelID, int assetTypeID) {
        ArrayList<Parameter> parameterList = new ArrayList<>();

        try (PreparedStatement ps = getConnection().prepareStatement(GET_ALL_PARAMS)) {
            ps.setInt(1, modelID);
            ps.setInt(2, assetTypeID);
            try (ResultSet queryResult = ps.executeQuery()) {
                while (queryResult.next()) {
                    if (queryResult.getBoolean("is_string")) {
                        StringParameter stringParameter = new StringParameter(queryResult.getInt("parameter_id"), queryResult.getString("name"), queryResult.getBoolean("is_live"), queryResult.getBoolean("is_default"), queryResult.getString("value"));
                        parameterList.add(stringParameter);
                    } else if (queryResult.getBoolean("is_int")) {
                        IntParameter intParameter = new IntParameter(queryResult.getInt("parameter_id"), queryResult.getString("name"), queryResult.getBoolean("is_live"), queryResult.getBoolean("is_default"), queryResult.getInt("value"));
                        parameterList.add(intParameter);
                    } else if (queryResult.getBoolean("is_float")) {
                        FloatParameter floatParameter = new FloatParameter(queryResult.getInt("parameter_id"), queryResult.getString("name"), queryResult.getBoolean("is_live"), queryResult.getBoolean("is_default"), queryResult.getFloat("value"));
                        parameterList.add(floatParameter);
                    } else if (queryResult.getBoolean("is_boolean")) {
                        BoolParameter booleanParameter = new BoolParameter(queryResult.getInt("parameter_id"), queryResult.getString("name"), queryResult.getBoolean("is_live"), queryResult.getBoolean("is_default"), queryResult.getBoolean("value"));
                        parameterList.add(booleanParameter);
                    } else if (queryResult.getBoolean("is_list")) {
                        ListParameter listParameter = null;
                        for (Parameter p : parameterList) {
                            if (p instanceof ListParameter && p.getParamName().equals(queryResult.getString("name")) && p.isLive() == queryResult.getBoolean("is_live") && p.isDefault() == queryResult.getBoolean("is_default"))
                                listParameter = (ListParameter) p;
                        }
                        if (listParameter == null) {
                            listParameter = new ListParameter(queryResult.getInt("parameter_id"), queryResult.getString("name"), queryResult.getBoolean("is_live"), queryResult.getBoolean("is_default"));
                            parameterList.add(listParameter);
                        }

                        listParameter.addToList(queryResult.getString("value"));
                        if (queryResult.getBoolean("is_list_selected")) {
                            listParameter.setSelectedValue(queryResult.getString("value"));
                        }
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return parameterList;
    }
}
