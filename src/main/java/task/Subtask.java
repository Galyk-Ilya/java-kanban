package task;

import java.time.format.DateTimeFormatter;

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
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
        String formatDateTime = startTime.format(formatter);
        return id + ", " +
                SUBTASK + ", " +
                name + ", " +
                status + ", " +
                description + ", " +
                epicId + ", " +
                duration + ", " +
                formatDateTime + ", " +
                this.getEndTime().format(formatter);
    }

    public Integer getEpicId() {
        return epicId;
    }

    public void setEpicId(Integer epicId) {
        this.epicId = epicId;
    }
}