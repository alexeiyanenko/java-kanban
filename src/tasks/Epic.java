package tasks;

import java.util.ArrayList;
import java.util.Objects;

public class Epic extends Task {
    private ArrayList<Integer> subTaskOfEpicIDs;

    public Epic(int id, String name, String description, TaskStatus status) {
        super(id, name, description, status);
        this.subTaskOfEpicIDs = new ArrayList<>();
    }

    public Epic(String name, String description, TaskStatus status) {
        super(name, description, status);
        this.subTaskOfEpicIDs = new ArrayList<>();
    }

    public ArrayList<Integer> getSubTaskOfEpicIDs() {
        return subTaskOfEpicIDs;
    }

    public void setSubTaskOfEpicIDs(ArrayList<Integer> subTaskOfEpicIDs) {
        this.subTaskOfEpicIDs = subTaskOfEpicIDs;
    }

    @Override
    public String toString() {
        return "Epic{" +
                "id=" + id +
                ", status=" + status +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", subTaskOfEpicIDs=" + subTaskOfEpicIDs +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Epic epic = (Epic) o;
        return Objects.equals(subTaskOfEpicIDs, epic.subTaskOfEpicIDs);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), subTaskOfEpicIDs);
    }
}
