package com.cbms.app;

public class Engine {

    private double linearRUL;
    private double lstmRUL;
    private String name;
    private String type;
    private String serialNumber;
    private String manufacturer;
    private String location;
    private Sensor[] sensors;

    public Engine() {

    }

    public Engine(double linearRUL, double lstmRUL, String name, String type, String serialNumber, String manufacturer, String location, Sensor[] sensors) {
        this.linearRUL = linearRUL;
        this.lstmRUL = lstmRUL;
        this.name = name;
        this.type = type;
        this.serialNumber = serialNumber;
        this.manufacturer = manufacturer;
        this.location = location;
        this.sensors = sensors;
    }

    public double getLinearRUL() {
        return linearRUL;
    }

    public void setLinearRUL(double linearRUL) {
        this.linearRUL = linearRUL;
    }

    public double getLstmRUL() {
        return lstmRUL;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setLstmRUL(double lstmRUL) {
        this.lstmRUL = lstmRUL;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Sensor[] getSensors() {
        return sensors;
    }

    public void setSensors(Sensor[] sensors) {
        this.sensors = sensors;
    }

    @Override
    public String toString() {
        return "System{" +
                "linearRUL=" + linearRUL +
                ", lstmRUL=" + lstmRUL +
                ", name='" + name + '\'' +
                '}';
    }
}
