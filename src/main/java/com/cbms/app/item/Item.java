/**
 * All assets to be create must be registered as Items
 *
 * @author      Roy Saliba
 * @version     1.0
 * @last_edit   11/07/2020
 */
package com.cbms.app.item;

public class Item {

    public int id;
    public String userTag;

    public Item(int id, String userTag) {
        this.id = id;
        this.userTag = userTag;
    }

    public Item() {

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUserTag() {
        return userTag;
    }

    public void setUserTag(String userTag) {
        this.userTag = userTag;
    }
}