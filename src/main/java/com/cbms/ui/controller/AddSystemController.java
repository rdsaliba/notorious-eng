package com.cbms.ui.controller;

import com.cbms.app.item.Asset;
import com.cbms.app.item.AssetType;
import com.cbms.source.local.AssetDAOImpl;
import com.cbms.source.local.AssetTypeDAOImpl;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.util.StringConverter;

import java.net.URL;
import java.util.*;

public class AddSystemController implements Initializable {

    @FXML
    public Button systemMenuBtn;
    @FXML
    private Button cancelBtn;
    @FXML
    private Button saveBtn;
    @FXML
    private ChoiceBox<AssetType> systemTypeChoiceBox;
    @FXML
    private TextField systemNameInput;
    @FXML
    private TextArea systemDescriptionTextArea;
    @FXML
    private TextField serialNumberInput;
    @FXML
    private TextField manufacturerInput;
    @FXML
    private TextField categoryInput;
    @FXML
    private TextField siteInput;
    @FXML
    private TextField locationInput;




    private static ObservableList<AssetType> assetTypeNamesList;
    private AssetDAOImpl assetDAOImpl;
    private AssetTypeDAOImpl assetTypeDAOImpl;
    private UIUtilities uiUtilities;
    private AssetType selectedAssetType;

    /**
     * Initialize runs before the scene is displayed.
     * It initializes elements and data in the scene.
     *
     * @param url
     * @param resourceBundle
     *
     * @author Jeff
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        assetDAOImpl = new AssetDAOImpl();
        assetTypeDAOImpl = new AssetTypeDAOImpl();
        uiUtilities = new UIUtilities();
        attachEvents();
        initializeFieldValues();
        systemDescriptionTextArea.setWrapText(true);
    }

    /**
     * Attaches events to elements in the scene.
     *
     * @author Jeff
     */
    public void attachEvents() {
        systemTypeChoiceBox.valueProperty().addListener((obs, oldval, newval) -> {
            if(newval != null)
                selectedAssetType = newval;
        });
        saveBtn.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                saveAsset(assembleAsset());
            }
        });
        // Change scenes to Systems.fxml
        systemMenuBtn.setOnMouseClicked(mouseEvent -> uiUtilities.changeScene(mouseEvent, "/Systems"));
        // Change scenes to Systems.fxml
        cancelBtn.setOnMouseClicked(mouseEvent -> uiUtilities.changeScene(mouseEvent, "/Systems"));
    }

    /**
     * Initializes the default and possible values for all fields that can accept user input. For example,
     * it establishes the possible dropdown values for the system type selection.
     */
    public void initializeFieldValues() {
        // Establishes the asset types available for selection in the dropdown
        assetTypeNamesList = FXCollections.observableArrayList(assetTypeDAOImpl.getAssetTypeList());
        systemTypeChoiceBox.setItems(assetTypeNamesList);
        systemTypeChoiceBox.setValue(systemTypeChoiceBox.getItems().get(0));
        systemTypeChoiceBox.setConverter(new StringConverter<AssetType>() {
            @Override
            public String toString(AssetType assetType) {
                return assetType.getName();
            }

            @Override
            public AssetType fromString(String s) {
                return systemTypeChoiceBox.getItems().stream().filter(ap ->
                        ap.getName().equals(s)).findFirst().orElse(null);
            }
        });
    }

    public Asset assembleAsset() {
        Asset newAsset = new Asset();
        newAsset.setName(systemNameInput.getText());
        newAsset.setAssetTypeID(selectedAssetType.getId());
        newAsset.setDescription(systemDescriptionTextArea.getText());
        newAsset.setSerialNo(serialNumberInput.getText());
        newAsset.setManufacturer(manufacturerInput.getText());
        newAsset.setCategory(categoryInput.getText());
        newAsset.setSite(siteInput.getText());
        newAsset.setLocation(locationInput.getText());
        return newAsset;
    }

    /**
     * Creates an ObservableList of asset type names.
     *
     * @param assetTypeList
     * @return
     */
    public ObservableList<String> getAssetTypeNameList(ObservableList<AssetType> assetTypeList) {
        ObservableList<String> assetTypeNames = FXCollections.observableArrayList();
        for (AssetType assetType:assetTypeList) {
            assetTypeNames.add(assetType.getName());
        }
        return assetTypeNames;
    }

    public void saveAsset(Asset newAsset) {
        //call assetDAOImpl method.
    }
}
