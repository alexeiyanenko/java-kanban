package manager;

import tasks.Task;
import tasks.TaskStatus;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {
    public HistoryManager historyManager;
    public TaskManager manager;

    @BeforeEach
    void setUp() {
        manager = Managers.getDefault();
        historyManager = Managers.getDefaultHistory();
    }

    @Test
    void canAddAndGet100TasksToHistory() {
        for (int i = 0; i < 100; i++) {
            Task task = new Task("Task " + i, "Description " + i, TaskStatus.NEW);
            manager.addTask(task);
            historyManager.add(task);
        }

        List<Task> historyList = historyManager.getHistory();

        // Проверяем, что в истории 10 элементов
        assertEquals(100, historyList.size());

        System.out.println("History:");
        for (Task task : historyList) {
            System.out.println(task);
        }
    }

    @Test
    public void whenTaskUpdated_thenNoImpactOnIdInHistory() {
        Task task = new Task("Task ", "Description ", TaskStatus.NEW);
        manager.addTask(task);
        historyManager.add(task);
        task.setDescription("Updated Description");
        manager.updateTask(task);
        assertEquals("Updated Description", historyManager.getHistory().get(0).getDescription());
    }

}