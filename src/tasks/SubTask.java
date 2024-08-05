package tasks;

import java.util.Objects;
import java.time.Duration;
import java.time.LocalDateTime;

public class SubTask extends Task {
    private int epicId;

    //Конструктор без id, duration и startTime
    public SubTask(int epicId, String name, String description, TaskStatus status) {
        super(name, description, status);
        this.epicId = epicId;
    }

    //Конструктор без id и startTime, но с duration
    public SubTask(int epicId, String name, String description, TaskStatus status, long minutes) {
        super(name, description, status, minutes);
        this.epicId = epicId;
    }

    //Конструктор c id, но без duration и startTime
    public SubTask(int epicId, int id, String name, String description, TaskStatus status) {
        super(id, name, description, status);
        this.epicId = epicId;
    }

    //Конструктор c id и duration, но без startTime
    public SubTask(int epicId, int id, String name, String description, TaskStatus status, long minutes) {
        super(id, name, description, status, minutes);
        this.epicId = epicId;
    }

    @Override
    public TaskType getType() {
        return TaskType.SUBTASK;
    }

    public int getEpicId() {
        return epicId;
    }

    public void setEpicId(int epicId) {
        this.epicId = epicId;
    }

    @Override
    public String toString() {
        return "SubTask{" +
                "epicId=" + epicId +
                ", id=" + id +
                ", status=" + status +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        SubTask subTask = (SubTask) o;
        return epicId == subTask.epicId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), epicId);
    }
}
