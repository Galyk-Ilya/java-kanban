package task;

import pattern.TaskType;

import java.util.ArrayList;
import java.util.List;

public class EpicTask extends CommonTask {

    public EpicTask(String name, String description) {
        super(name, description);
        type = TaskType.EPIC_TASK;
    }

//    @Override
//    public String toString() {
//        return "EpicTask{" +
//                "subtasksList=" + subtasksList +
//                ", name='" + name + "'" +
//                ", description='" + description + "'" +
//                ", id=" + id +
//                ", status=" + status +
//                ", taskReviewed=" + taskReviewed +
//                ", type=" + type +
//                '}';
//    }

    private final List<Subtask> subtasksList = new ArrayList<>();

    public List<Subtask> getSubtasksList() {
        return subtasksList;
    }


}