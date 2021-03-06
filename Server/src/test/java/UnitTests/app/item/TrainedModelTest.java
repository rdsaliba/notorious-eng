package UnitTests.app.item;

import app.item.TrainedModel;
import app.item.parameter.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.*;

public class TrainedModelTest {
    private TrainedModel trainedModel;

    @Before
    public void setUp() {
        trainedModel = new TrainedModel();
        ArrayList<Parameter> parameters = new ArrayList<>();
        ListParameter listParameter = new ListParameter(5, "list param",true,false);
        listParameter.addToList("list value 1");
        listParameter.addToList("list value 2");
        listParameter.addToList("list value 3");
        listParameter.setSelectedValue("list value 2");
        ListParameter listParameterDefault = new ListParameter(6, "list param",false,true);
        listParameterDefault.addToList("list value 4");
        listParameterDefault.addToList("list value 5");
        listParameterDefault.addToList("list value 6");
        listParameterDefault.setSelectedValue("list value 6");
        parameters.add(listParameter);
        parameters.add(listParameterDefault);
        parameters.add(new IntParameter(1, "this is an int param", true, false, 100));
        parameters.add(new FloatParameter(2, "this is an float param", true, true, 345.345f));
        parameters.add(new StringParameter(3, "this is an String param", false, true, "this is string value"));
        parameters.add(new BoolParameter(4, "this is an bool param", false, false, false));

        trainedModel.setParameterList(parameters);
    }

    @After
    public void tearDown() {
        trainedModel = null;
    }

    @Test
    public void defaultConst() {
        TrainedModel temp = new TrainedModel();
        assertNotNull("new trainedModel should be created", temp);
    }

    @Test
    public void setAndGetModelID() {
        trainedModel.setModelID(999);
        assertEquals("new serial number should be '999'", 999, trainedModel.getModelID());
    }

    @Test
    public void setAndGettrainedModelType() {
        trainedModel.setAssetTypeID(888);
        assertEquals("new trainedModelType should be '888'", 888, trainedModel.getAssetTypeID());
    }

    @Test
    public void setAndGetRetrain() {
        trainedModel.setRetrain(true);
        assertTrue("new Location should be 'true'", trainedModel.isRetrain());
    }

    @Test
    public void setAndGetClassifier() {
        trainedModel.setModelClassifier(null);
        assertNull("new description should be 'null'", trainedModel.getModelClassifier());
    }


    @Test
    public void trainedModelToString() {
        assertEquals("TrainedModel{modelID=0, assetTypeID=0, retrain=false, modelClassifier=null}", trainedModel.toString());
    }

    @Test
    public void returnAllParameters() {
        ArrayList<Parameter> returnedList = trainedModel.getParameterList();
        assertEquals(6,returnedList.size());
    }

    @Test
    public void returnDefaultParameters() {
        ArrayList<Parameter> returnedList = trainedModel.getDefaultParameterList();
        assertEquals(3,returnedList.size());
    }

    @Test
    public void returnLiveParameters() {
        ArrayList<Parameter> returnedList = trainedModel.getLiveParameterList();
        assertEquals(3,returnedList.size());
    }

    @Test
    public void returnEvalParameters() {
        ArrayList<Parameter> returnedList = trainedModel.getEvalParameterList();
        assertEquals(3,returnedList.size());
    }

    @Test
    public void getSpecificParameter(){
        ArrayList<Parameter> returnedList = trainedModel.getParameter("list param");
        assertEquals(2, returnedList.size());
    }
}