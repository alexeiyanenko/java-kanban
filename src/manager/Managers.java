package manager;

public class Managers {
    private Managers(){};
    private static final HistoryManager historyManager = new InMemoryHistoryManager();

    public static TaskManager getDefault() {
        return new InMemoryTaskManager();
    }

    public static HistoryManager getDefaultHistory() {
        return historyManager;
    }
}
