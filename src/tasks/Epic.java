package tasks;

import java.util.ArrayList;
import java.util.Objects;
import java.time.Duration;
import java.time.LocalDateTime;

public class Epic extends Task {
    private LocalDateTime endTime;
    private ArrayList<Integer> subtaskOfEpicIDs;

    public Epic(String name, String description, TaskStatus status) {
        super(name, description, status);
        this.subtaskOfEpicIDs = new ArrayList<>();
    }

    public Epic(int id, String name, String description, TaskStatus status) {
        super(id, name, description, status);
        this.subtaskOfEpicIDs = new ArrayList<>();
    }

    @Override
    public TaskType getType() {
        return TaskType.EPIC;
    }

    public ArrayList<Integer> getSubtaskOfEpicIDs() {
        return subtaskOfEpicIDs;
    }

    public void setSubtaskOfEpicIDs(ArrayList<Integer> subtaskOfEpicIDs) {
        this.subtaskOfEpicIDs = subtaskOfEpicIDs;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = startTime;
    }

    @Override
    public String toString() {
        return "Epic{" +
                "id=" + id +
                ", status=" + status +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", subtaskOfEpicIDs=" + subtaskOfEpicIDs +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Epic epic = (Epic) o;
        return Objects.equals(subtaskOfEpicIDs, epic.subtaskOfEpicIDs);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), subtaskOfEpicIDs);
    }
}
