import java.util.Objects;

public class Task {
    static int uniqId = 1;

    private int id;
    private String status = TaskStatus.NEW.toString();

    private String type;
    protected String name;
    protected String description;

    //Конструктор без обработки времени
    public Task(String name, String description) {
        if(this instanceof SubTask) this.type = "SubTask";
        else if (this instanceof Epic) this.type = "Epic";
        else this.type = "Task";
        this.name = name;
        this.description = description;
        id = uniqId;
        uniqId++;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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

    public String getStatus() {
        return status;
    }

    public Integer getId() {
        return id;
    }

    public void setStatus(String status) {
        if (status.equals(TaskStatus.DONE.toString())
                || status.equals(TaskStatus.NEW.toString())
                || status.equals(TaskStatus.IN_PROGRESS.toString())) {
            this.status = status;
        }
    }

    @Override
    public String toString() {
        StringBuilder taskInfo = new StringBuilder();
        taskInfo.append(status).append(" ").append(type).append(": ").append(name).append(" (id = ").append(id).append(")").append(" (").append(description)
                .append(") ").append("\n");
        return taskInfo.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return id == task.id && Objects.equals(type, task.type)
                && Objects.equals(status, task.status) && Objects.equals(name, task.name)
                && Objects.equals(description, task.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, status, type, name, description) + 31;
    }
}
