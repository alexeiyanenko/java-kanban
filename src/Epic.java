import java.util.List;

public class Epic extends Task {
    private List<SubTask> subTasksOfEpic;

    public Epic(String name, String description) {
        super(name, description);
    }

    public Epic(String name, String description, List<SubTask> subTasksOfEpic) {
        super(name, description);
        this.subTasksOfEpic = subTasksOfEpic;
    }

    public List<SubTask> getSubTasksOfEpic() {
        return subTasksOfEpic;
    }

    public void setSubTasksOfEpic(List<SubTask> subTasksOfEpic) {
        this.subTasksOfEpic = subTasksOfEpic;
    }
}
