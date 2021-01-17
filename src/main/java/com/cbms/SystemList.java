package com.cbms;


import javafx.beans.property.SimpleStringProperty;

public class SystemList {
    private SimpleStringProperty name;
    private int associated_assets;

    public SystemList(String name, int associated_assets) {
        this.name = new SimpleStringProperty(name);
        this.associated_assets = associated_assets;
    }

    public String getName() {
        return name.get();
    }

    public SimpleStringProperty nameProperty() {
        return name;
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public int getAssociated_assets() {
        return associated_assets;
    }

    public void setAssociated_assets(int associated_assets) {
        this.associated_assets = associated_assets;
    }
}
