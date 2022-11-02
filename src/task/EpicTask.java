package task;

import pattern.TaskType;

import java.util.ArrayList;

public class EpicTask extends CommonTask {
    public EpicTask(String name, String description) {
        super(name, description);
        type = TaskType.EPIC_TASK;
    }

    private final ArrayList<Subtask> subtasksList = new ArrayList<>();

    public ArrayList<Subtask> getSubtasksList() {
        return subtasksList;
    }

    @Override
    public String toString() {
        return "{EpicTask = [name = '" + name + "', description = '" + description + "', status = '" + status + "'.]}";
    }
}
