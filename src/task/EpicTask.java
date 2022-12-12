package task;

import java.util.ArrayList;
import java.util.List;

import static enums.TaskType.EPIC_TASK;

public class EpicTask extends CommonTask {

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
        return id + ", " +
                EPIC_TASK + ", " +
                name + ", " +
                status + ", " +
                description + ", " +
                "-";
    }

    public List<Subtask> getSubtasksList() {
        return subtasksList;
    }
}