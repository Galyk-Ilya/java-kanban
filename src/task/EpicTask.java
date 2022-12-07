package task;

import pattern.TaskType;

import java.util.ArrayList;
import java.util.List;

public class EpicTask extends CommonTask {

    public EpicTask(String name, String description) {
        super(name, description);
        type = TaskType.EPIC_TASK;
    }

    private final List<Subtask> subtasksList = new ArrayList<>();

    @Override
    public String toString() {
        return "EpicTask{" +
                "subtasksList=" + subtasksList +
                ", name='" + name + "'" +
                ", description='" + description + "'" +
                ", id=" + id +
                ", status=" + status +
                ", taskReviewed=" + taskReviewed +
                ", type=" + type +
                '}';
    }

    public List<Subtask> getSubtasksList() {
        return subtasksList;
    }
}