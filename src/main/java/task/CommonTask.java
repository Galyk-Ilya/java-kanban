package task;

import enums.StatusType;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static enums.StatusType.NEW;
import static enums.TaskType.COMMONTASK;

public class CommonTask {
    protected String name;
    protected String description;
    protected Integer id;
    protected StatusType status;
    protected Duration duration;
    protected LocalDateTime startTime;

    public CommonTask(String name, String description) {
        this.name = name;
        this.description = description;
        status = NEW;
    }
    public CommonTask(int id, String name, StatusType status, String description,
                      int duration, LocalDateTime startTime){
        this.id = id;
        this.name = name;
        this.status = status;
        this.description = description;
        this.duration = Duration.ofMinutes(duration);
        this.startTime = startTime;
    }

    @Override
    public String toString() {
        return "{CommonTask" +
                " [name = '" + name +
                ", description='" + description + "'" +
                "', id = '" + id +
                "', status = '" + status +
                ", duration='" + duration + "'" +
                ", startTime='" + startTime + "'" +
                "'.]}";
    }

    public String toStringToWriteToFile() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
        String formatDateTime = startTime.format(formatter);
        return id + ", " +
                COMMONTASK + ", " +
                name + ", " +
                status + ", " +
                description + ", " +
                "-" + ", " +
                duration + ", " +
                formatDateTime + ", " +
                this.calculateEndTime().format(formatter);
    }

    public LocalDateTime calculateEndTime() {
        return startTime.plusMinutes(duration.toMinutes());
    }


    public StatusType getStatus() {
        return status;
    }

    public void setStatus(StatusType status) {
        this.status = status;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;

    }

    public Duration getDuration() {
        return duration;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

}
