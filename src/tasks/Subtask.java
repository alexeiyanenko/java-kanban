package tasks;

import java.util.Objects;

public class Subtask extends Task {
    private int epicId;

    //Конструктор без id, duration и startTime
    public Subtask(int epicId, String name, String description, TaskStatus status) {
        super(name, description, status);
        this.epicId = epicId;
    }

    //Конструктор без id и startTime, но с duration
    public Subtask(int epicId, String name, String description, TaskStatus status, long minutes) {
        super(name, description, status, minutes);
        this.epicId = epicId;
    }

    //Конструктор c id, но без duration и startTime
    public Subtask(int epicId, int id, String name, String description, TaskStatus status) {
        super(id, name, description, status);
        this.epicId = epicId;
    }

    //Конструктор c id и duration, но без startTime
    public Subtask(int epicId, int id, String name, String description, TaskStatus status, long minutes) {
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
        return "Subtask{" +
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
        Subtask subtask = (Subtask) o;
        return epicId == subtask.epicId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), epicId);
    }
}
