package task;

import pattern.TaskType;

public class Subtask extends CommonTask {
    private Integer epicId;

    public Subtask(String name, String description) {
        super(name, description);
         type = TaskType.SUBTASK;
    }

    public Integer getEpicId() {
        return epicId;
    }
    public void setEpicId(Integer epicId) {
        this.epicId = epicId;
    }
    @Override
    public String toString() {
        return "{Subtask = [name = '" + name + "', description = '" + description + "', status = '" + status + "'.]}";
    }
}
