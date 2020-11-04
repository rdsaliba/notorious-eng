package com.cbms.app;

import java.util.Arrays;

public class Sensor {
    private double currentMeasurement;
    private double[] measurements;
    private double[] lastFiveMeasurements;
    private String location;
    private String type;

    public Sensor(double currentMeasurement, double[] measurements, double[] lastFiveMeasurements, String location, String type) {
        this.currentMeasurement = currentMeasurement;
        this.measurements = measurements;
        this.lastFiveMeasurements = lastFiveMeasurements;
        this.location = location;
        this.type = type;
    }

    public double getCurrentMeasurement() {
        return currentMeasurement;
    }

    public void setCurrentMeasurement(double currentMeasurement) {
        this.currentMeasurement = currentMeasurement;
    }

    public double[] getMeasurements() {
        return measurements;
    }

    public void setMeasurements(double[] measurements) {
        this.measurements = measurements;
    }

    public double[] getLastFiveMeasurements() {
        return lastFiveMeasurements;
    }

    public void setLastFiveMeasurements(double[] lastFiveMeasurements) {
        this.lastFiveMeasurements = lastFiveMeasurements;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "Sensor{" +
                "currentMeasurement=" + currentMeasurement +
                ", measurements=" + Arrays.toString(measurements) +
                ", lastFiveMeasurements=" + Arrays.toString(lastFiveMeasurements) +
                ", location='" + location + '\'' +
                ", type='" + type + '\'' +
                '}';
    }
}
