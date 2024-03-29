package task;

import enums.StatusType;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

import static enums.TaskType.SUBTASK;

public class Subtask extends CommonTask {
    private int epicId;

    public Subtask(String name, String description) {
        super(name, description);
    }

    public Subtask(int id, String name, StatusType status, String description,
                   int duration, LocalDateTime startTime) {
        super(id, name, status, description, duration, startTime);
    }
    public Subtask(int id, String name, StatusType status, String description, int epicId,
                   int duration, LocalDateTime startTime) {
        super(id, name, status, description, duration, startTime);
        this.epicId = epicId;
    }

    @Override
    public String toString() {
        return "Subtask{" +
                "name='" + name + "'" +
                ", description='" + description + "'" +
                ", id=" + id + "'" +
                ", status=" + status + "'" +
                ", epicId=" + epicId +
                "', duration = '" + duration + "'" +
                "', startTime = '" + startTime + "'" +
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
                this.calculateEndTime().format(formatter);
    }

    public Integer getEpicId() {
        return epicId;
    }

    public void setEpicId(Integer epicId) {
        this.epicId = epicId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Subtask subtask = (Subtask) o;
        return epicId == subtask.epicId && Objects.equals(id, subtask.getId()) && Objects.equals(name,subtask.getName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}