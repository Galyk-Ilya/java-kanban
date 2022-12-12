package task;

import enums.StatusType;

import static enums.StatusType.NEW;
import static enums.TaskType.COMMONTASK;

public class CommonTask {
    protected String name;
    protected String description;
    protected Integer id;
    protected StatusType status;

    public CommonTask(String name, String description) {
        this.name = name;
        this.description = description;
        status = NEW;
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

    @Override
    public String toString() {
        return "{CommonTask" +
                " [name = '" + name +
                ", description='" + description + "'" +
                "', id = '" + id +
                "', status = '" + status +
                "'.]}";
    }

    public String toStringToWriteToFile() {
        return id + ", " +
                COMMONTASK + ", " +
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