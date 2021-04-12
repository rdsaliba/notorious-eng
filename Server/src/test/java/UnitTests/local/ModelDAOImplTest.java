package UnitTests.local;

import app.item.TrainedModel;
import local.ModelDAOImpl;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import static local.DatabaseConnection.getConnection;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class ModelDAOImplTest {
    private ModelDAOImpl modelDAO;
    @Before
    public void setUp() {
        modelDAO = new ModelDAOImpl();
    }

    @After
    public void tearDown() {
        modelDAO = null;
    }

    @Test
    public void convertToByteString() {

    }

    @Test
    public void convertFromByteString() {

    }

    @Test
    public void getModelNameFromModelID() {
        String name = modelDAO.getModelNameFromModelID("3");
        assertEquals("RandomForest",name);
    }

    @Test
    public void getModelsToTrain() {
        ArrayList<TrainedModel> trainedModels = modelDAO.getModelsToTrain();
        assertEquals(3, trainedModels.size());
    }

    @Test
    public void setModelToTrain() {
        String updateObject ="UPDATE trained_model tm, model m \n" +
                                "SET tm.retrain = false, tm.serialized_model = 100 \n" +
                                "WHERE tm.model_id = 1 AND tm.asset_type_id = 1 AND tm.status_id = 1 AND tm.model_id = m.model_id AND m.archived = 0";
        try (PreparedStatement ps = getConnection().prepareStatement(updateObject)) {
            try (ResultSet rs = ps.executeQuery()) {
                assertNotNull(rs.getBlob("serialized_object"));
            }
        } catch (SQLException e) {
        }
    }

    @Test
    public void getModelsByAssetTypeID() {
        TrainedModel tm = modelDAO.getModelsByAssetTypeID("3",2);
        assertEquals(3,tm.getAssetTypeID());
    }

    @Test
    public void createTrainedModelFromResultSet() {

    }

    @Test
    public void updateEvaluationRMSE() {
        String updateRMSE = "UPDATE trained_model SET rmse = 25, retrain = 0 WHERE model_id = 1 AND asset_type_id = 1 AND status_id=2";
        try (PreparedStatement ps = getConnection().prepareStatement(updateRMSE)) {
            try (ResultSet rs = ps.executeQuery()) {
                assertEquals(25,rs.getDouble("rmse"),0.01);
            }
        } catch (SQLException e) {
        }
    }
}