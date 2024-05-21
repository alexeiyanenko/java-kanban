package manager;

import tasks.Task;

import java.util.List;

public interface HistoryManager {
    public void addToHistory(Task task);
    public List<Task> getHistory();
}
