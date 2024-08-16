package tasks;

import java.util.Objects;
import java.time.Duration;
import java.time.LocalDateTime;

public class Task {
    protected int id;
    protected TaskStatus status;
    protected String name;
    protected String description;
    protected Duration duration;
    protected LocalDateTime startTime;

    //Конструктор без id, duration и startTime
    public Task(String name, String description, TaskStatus status) {
        this.id = 0;
        this.name = name;
        this.description = description;
        this.status = status;
        this.duration = Duration.ZERO;
        this.startTime = null;
    }

    //Конструктор без id и startTime, но с duration
    public Task(String name, String description, TaskStatus status, long minutes) {
        this.id = 0;
        this.name = name;
        this.description = description;
        this.status = status;
        this.duration = Duration.ofMinutes(minutes);
        this.startTime = null;
    }

    //Конструктор c id, но без duration и startTime
    public Task(int id, String name, String description, TaskStatus status) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.status = status;
        this.duration = Duration.ZERO;
        this.startTime = null;
    }

    //Конструктор c id и duration, но без startTime
    public Task(int id, String name, String description, TaskStatus status, long minutes) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.status = status;
        this.duration = Duration.ofMinutes(minutes);
        this.startTime = null;
    }

    public TaskType getType() {
        return TaskType.TASK;
    }

    public Integer getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus status) {
        if (status.equals(TaskStatus.DONE)
                || status.equals(TaskStatus.NEW)
                || status.equals(TaskStatus.IN_PROGRESS)) {
            this.status = status;
        }
    }

    public Duration getDuration() {
        return duration;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public void setDuration(long minutes) {
        this.duration = Duration.ofMinutes(minutes);
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public void setStartTime(int y, int m, int d, int hh, int mm) {
        this.startTime = LocalDateTime.of(y, m, d, hh, mm);
    }

    public LocalDateTime getEndTime() {
        return (startTime != null) ? startTime.plus(duration) : null;
    }

    @Override
    public String toString() {
        return "Task{" +
                "id=" + id +
                ", status=" + status +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return id == task.id && status == task.status && Objects.equals(name, task.name) && Objects.equals(description, task.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, status, name, description);
    }
}