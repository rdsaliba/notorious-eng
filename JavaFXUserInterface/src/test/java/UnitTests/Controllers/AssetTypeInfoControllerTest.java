package UnitTests.Controllers;

import Controllers.AssetTypeInfoController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.layout.Pane;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class AssetTypeInfoControllerTest {
    private AssetTypeInfoController assetTypeInfoController;
    private ObservableList<Pane> paneObservableList;
    private Pane pane1, pane2, pane3;

    @Before
    public void setUp() {
        assetTypeInfoController = new AssetTypeInfoController();
        paneObservableList = FXCollections.observableArrayList();
        pane1 = new Pane();
        pane2 = new Pane();
        pane3 = new Pane();
        paneObservableList.addAll(pane1, pane2, pane3);
    }

    @After
    public void tearDown() {
        assetTypeInfoController = null;
        paneObservableList.removeAll();
        paneObservableList = null;
        pane1 = null;
        pane2 = null;
        pane3 = null;
    }
}