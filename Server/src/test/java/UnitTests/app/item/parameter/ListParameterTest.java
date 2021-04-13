package UnitTests.app.item.parameter;

import app.item.parameter.ListParameter;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ListParameterTest {
    private ListParameter listParameter;
    private List<String> listValues;

    @Before
    public void setUp() {
        listValues = new ArrayList<String>();
        listParameter = new ListParameter("TestListParameter", listValues, "testSelectedValue");
    }

    @After
    public void tearDown() {
        listValues = null;
        listParameter = null;
    }

    @Test
    public void testAddToList() {
        listParameter.addToList("testValue1");
        assertTrue(listParameter.getListValues().contains("testValue1"));
    }

    @Test
    public void testGetAndSetListValues() {
        List<String> temp = new ArrayList<String>();
        temp.add("newValue");
        listParameter.setListValues(temp);
        assertEquals(temp, listParameter.getListValues());
    }

    @Test
    public void testGetAndSetSelectedValue() {
        listParameter.setSelectedValue("newSelectedValue");
        assertEquals("newSelectedValue", listParameter.getSelectedValue());
    }
}