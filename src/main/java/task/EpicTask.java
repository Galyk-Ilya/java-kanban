package task;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static enums.TaskType.EPIC_TASK;

public class EpicTask extends CommonTask {
    LocalDateTime endTime;

    public EpicTask(String name, String description) {
        super(name, description);
    }

    private final List<Subtask> subtasksList = new ArrayList<>();

    @Override
    public String toString() {
        return "{EpicTask" +
                " [name = '" + name +
                ", description='" + description + "'" +
                "', id = '" + id +
                "', status = '" + status + "'" +
//                "', subtasksList='" + subtasksList +
                "'.]}";
    }

    @Override
    public String toStringToWriteToFile() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
        String formatDateTime = startTime.format(formatter);
        return id + ", " +
                EPIC_TASK + ", " +
                name + ", " +
                status + ", " +
                description + ", " +
                "-" + ", " +
                duration + ", " +
                formatDateTime + ", " +
                this.getEndTime().format(formatter);
    }

    public List<Subtask> getSubtasksList() {
        return subtasksList;
    }

    @Override
    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }
}