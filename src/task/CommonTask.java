package task;

import pattern.StatusType;

import static pattern.StatusType.NEW;

public class CommonTask {
    protected String name;
    protected String description;
    protected Integer id;
    protected StatusType status;
    protected boolean taskReviewed;


    public CommonTask(String name, String description) {
        this.name = name;
        this.description = description;
        status = NEW;
        taskReviewed = false;
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

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean getTaskReviewed() {
        return taskReviewed;
    }

    public void setTaskReviewed(boolean taskReviewed) {
        this.taskReviewed = taskReviewed;
    }


    @Override
    public String toString() {
        return "{CommonTask = [name = '" + name + "', description = '" + description + "', status = '" + status + "'.]}";
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }
}