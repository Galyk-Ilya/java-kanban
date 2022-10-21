package task;

public class CommonTask {
    protected String name;
    protected String description;
    protected String status;
    protected Integer id;

    public CommonTask(String name, String description) {
        this.name = name;
        this.description = description;
        status = "NEW";
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) { this.status = status; }

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

    @Override
    public String toString() {
        return "{name = '" + name + "', description = '" + description + "', status = '" + status + "'.}";
    }

    public void setId(Integer id) {
        this.id = id;
    }
    public Integer getId() {
        return id;
    }
}