package manager;

import tasks.Task;

import java.util.ArrayList;

public interface HistoryManager {
    public void addToHistory(Task task);
    public ArrayList<Task> getHistory();
}
