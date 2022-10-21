package task;

import java.util.ArrayList;

public class EpicTask extends CommonTask {
    public EpicTask(String name, String description) {
        super(name, description);
    }

    private ArrayList<Subtask> subtasksList = new ArrayList<>();

    public ArrayList<Subtask> getSubtasksList() {
        return subtasksList;
    }

    public void taskInfo() {
        System.out.println("{EpicTask [name = '" + name + "', description = '" + description
                + "', status = '" + status + "'.]}");
        if (getSubtasksList().size() > 0) {
            for (int i = 0; i < subtasksList.size(); i++) {
                Subtask subtask = subtasksList.get(i);
                System.out.println("{Subtask [name = '" + subtask.name + "', description = '" + subtask.description
                        + "', status = '" + subtask.status + "'.]}");
            }
        }
    }
}
