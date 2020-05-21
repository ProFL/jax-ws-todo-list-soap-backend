package projeto_1.labels_tasks.beans;

public class LabelsTasks {
    private int taskId;
    private int labelId;

    public LabelsTasks() {
        this.taskId = -1;
        this.labelId = -1;
    }

    public LabelsTasks(int taskId, int labelId) {
        this.taskId = taskId;
        this.labelId = labelId;
    }

    public int getTaskId() {
        return taskId;
    }

    public void setTaskId(int taskId) {
        this.taskId = taskId;
    }

    public int getLabelId() {
        return labelId;
    }

    public void setLabelId(int labelId) {
        this.labelId = labelId;
    }
}
