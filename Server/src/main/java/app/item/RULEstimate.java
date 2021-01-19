/*
  This class represents all the RUL estimates calculated by our application for specific assets using
  certain RUL models. It contains the timestamp at which the estimate was made as well as the value of
  the estimate.

  @author Jeremie Chouteau
  @version 2.0
  @last_edit 12/24/2020
 */
package app.item;

import java.util.Date;

public class RULEstimate {
    private Date timestamp;
    private double value;

    public RULEstimate(){}

    public RULEstimate(Date timestamp, double value) {
        this.timestamp = timestamp;
        this.value =value;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public double getValue() {
        return value;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public void setValue(double value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "RULEstimate{" +
                "timestamp=" + timestamp +
                ", value=" + value + '}';
    }
}
