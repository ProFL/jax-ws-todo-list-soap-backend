package projeto_1.task.exceptions;

public class TaskNotFoundException extends Exception {
    public final int id;

    public TaskNotFoundException(int id) {
        super("Task with id: " + id + " was not found");
        this.id = id;
    }
}
