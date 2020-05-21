package projeto_1.task.exceptions;

public class AlreadyLabeledException extends Exception {
    public final int taskId;
    public final int labelId;

    public AlreadyLabeledException(int taskId, int labelId) {
        super("This task is already labeled with this label");
        this.taskId = taskId;
        this.labelId = labelId;
    }
}
