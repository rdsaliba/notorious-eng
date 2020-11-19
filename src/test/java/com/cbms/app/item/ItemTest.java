package com.cbms.app.item;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class ItemTest {
    private Item item;

    @Before
    public void setup(){
        item = new Item(123,"usertag");
    }

    @Test
    public void defaultCons(){
        Item temp = new Item();
        assertNotNull(temp);
    }

    @Test
    public void getId() {
        assertEquals("Current ID should be 123", 123, item.getId());
    }

    @Test
    public void setId() {
        item.setId(555);
        assertEquals("ID after change should be 555", 555, item.getId());

    }

    @Test
    public void getUserTag() {
        assertEquals("Current usertag should be 'usertag'", "usertag", item.getUserTag());
    }

    @Test
    public void setUserTag() {
        item.setUserTag("newUserTag");
        assertEquals("Usertag after change should be 'newUserTag'","newUserTag",item.getUserTag());
    }
}