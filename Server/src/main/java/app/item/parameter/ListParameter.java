package app.item.parameter;

import java.util.ArrayList;

public class ListParameter extends parameter {
    private ArrayList<String> listValues;
    private String selectedValue;


    public ListParameter(String paramName) {
        super(paramName);
    }

    public ListParameter(int paramID, String paramName) {
        super(paramID, paramName);
    }

    public ArrayList<String> getListValues() {
        return listValues;
    }

    public void setListValues(ArrayList<String> listValues) {
        this.listValues = listValues;
    }

    public String getSelectedValue() {
        return selectedValue;
    }

    public void setSelectedValue(String selectedValue) {
        this.selectedValue = selectedValue;
    }
}
