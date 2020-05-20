package projeto_1.labels.beans;

public class Label {
    private int id;
    private int ownerId;
    private String name;
    private String color;

    public Label() {
        this(-1, -1, null, null);
    }

    public Label(int ownerId, String name, String color) {
        this(-1, ownerId, name, color);
    }

    public Label(int id, int ownerId, String name, String color) {
        this.id = id;
        this.ownerId = ownerId;
        this.name = name;
        this.color = color;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(int ownerId) {
        this.ownerId = ownerId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }
}
