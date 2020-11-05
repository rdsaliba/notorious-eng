package Item;

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