package projeto_1.task.beans;

public class Task {
    private int id;
    private int ownerId;
    private String name;
    private String description;

    public Task() {
        this(-1, -1, null, null);
    }

    public Task(int ownerId, String name, String description) {
        this(-1, ownerId, name, description);
    }

    public Task(int id, int ownerId, String name, String description) {
        this.id = id;
        this.ownerId = ownerId;
        this.name = name;
        this.description = description;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}