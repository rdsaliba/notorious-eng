package com.cbms;


import javafx.beans.property.SimpleStringProperty;

public class SystemList {
    private SimpleStringProperty name;
    private int associated_assets;
    private double value_ok,value_caution,value_advisory,value_warning,value_failed;

    public SystemList(String name, int associated_assets) {
        this.name = new SimpleStringProperty(name);
        this.associated_assets = associated_assets;
    }

    public SystemList(String name, int associated_assets, double value_ok, double value_caution, double value_advisory, double value_warning, double value_failed) {
        this.name = new SimpleStringProperty(name);
        this.associated_assets = associated_assets;
        this.value_ok = value_ok;
        this.value_caution = value_caution;
        this.value_advisory = value_advisory;
        this.value_warning = value_warning;
        this.value_failed = value_failed;
    }

    public double getValue_ok() {
        return value_ok;
    }

    public void setValue_ok(double value_ok) {
        this.value_ok = value_ok;
    }

    public double getValue_caution() {
        return value_caution;
    }

    public void setValue_caution(double value_caution) {
        this.value_caution = value_caution;
    }

    public double getValue_advisory() {
        return value_advisory;
    }

    public void setValue_advisory(double value_advisory) {
        this.value_advisory = value_advisory;
    }

    public double getValue_warning() {
        return value_warning;
    }

    public void setValue_warning(double value_warning) {
        this.value_warning = value_warning;
    }

    public double getValue_failed() {
        return value_failed;
    }

    public void setValue_failed(double value_failed) {
        this.value_failed = value_failed;
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
