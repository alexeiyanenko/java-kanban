package manager;

import tasks.Task;

import java.util.ArrayList;

public class InMemoryHistoryManager implements HistoryManager{
    private final ArrayList<Task> historyList = new ArrayList<>();

    @Override
    public void addToHistory(Task task) {
        if (task == null) {
            return;
        }

        historyList.add(task);

        if (historyList.size() > 10) {
            historyList.remove(0);
        }
    }

    @Override
    public ArrayList<Task> getHistory() {
        return historyList;
    }

    @Override
    public String toString() {
        return "History:\n" + getHistory();
    }
}
