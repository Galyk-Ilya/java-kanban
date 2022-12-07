package task;

import pattern.StatusType;
import pattern.TaskType;

import static pattern.StatusType.NEW;

public class CommonTask {
    protected String name;
    protected String description;
    protected Integer id;
    protected StatusType status;
    protected boolean taskReviewed;
    protected TaskType type = TaskType.COMMONTASK;

    public CommonTask(String name, String description) {
        this.name = name;
        this.description = description;
        status = NEW;
        taskReviewed = false;
    }

    public TaskType getType() {
        return type;
    }

    public StatusType getStatus() {
        return status;
    }

    public void setStatus(StatusType status) {
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public void setType(TaskType type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "{" + this.getType() + " [name = '" + name + "', id = '" + id + "', status = '" + status + "'.]}";
    }

    public String toStringToWriteToFile() {
        return id + ", " +
                this.getType() + ", " +
                name + ", " +
                status + ", " +
                 description + ", " +
                "-";
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }
}