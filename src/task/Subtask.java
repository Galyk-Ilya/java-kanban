package task;

import pattern.TaskType;

public class Subtask extends CommonTask {
    private Integer epicId;

    public Subtask(String name, String description) {
        super(name, description);
        type = TaskType.SUBTASK;
    }

    @Override
    public String toString() {
        return "Subtask{" +
                "epicId=" + epicId +
                ", name='" + name + "'" +
                ", description='" + description + "'" +
                ", id=" + id +
                ", status=" + status +
                ", taskReviewed=" + taskReviewed +
                ", type=" + type +
                '}';
    }

    @Override
    public String toStringToWriteToFile() {
        return super.toStringToWriteToFile().substring(0, super.toStringToWriteToFile().length() - 1) + epicId;
    }

    public Integer getEpicId() {
        return epicId;
    }

    public void setEpicId(Integer epicId) {
        this.epicId = epicId;
    }
}