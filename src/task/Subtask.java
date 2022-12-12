package task;

import static enums.TaskType.SUBTASK;

public class Subtask extends CommonTask {
    private Integer epicId;

    public Subtask(String name, String description) {
        super(name, description);
    }

    @Override
    public String toString() {
        return "Subtask{" +
                "name='" + name + "'" +
                ", description='" + description + "'" +
                ", id=" + id + "'" +
                ", status=" + status + "'" +
                ", epicId=" + epicId +
                '}';
    }

    @Override
    public String toStringToWriteToFile() {
        return id + ", " +
                SUBTASK + ", " +
                name + ", " +
                status + ", " +
                description + ", " +
                epicId;
    }

    public Integer getEpicId() {
        return epicId;
    }

    public void setEpicId(Integer epicId) {
        this.epicId = epicId;
    }
}