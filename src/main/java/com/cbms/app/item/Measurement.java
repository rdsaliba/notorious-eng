/*
  This class represents all the measurements read and recorded by the asset attributes. It contains
  the time cycle at which the measurement was taken as well as the value attached to it.

  @author Jeremie Chouteau
  @version 2.0
  @last_edit 12/24/2020
 */
package com.cbms.app.item;

public class Measurement {
    private int time;
    private double value;

    public Measurement() {}

    public Measurement(int time, double value) {
        this.time = time;
        this.value = value;
    }

    public double getValue() {
        return value;
    }

    public int getTime() {
        return time;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public void setTime(int time) {
        this.time = time;
    }

    @Override
    public String toString() {
        return "Measurement{" +
                "time=" + time +
                ", measurement value='" + value + "}";
    }
}
