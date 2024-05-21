package manager;

import tasks.Task;
import tasks.TaskStatus;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {
    public HistoryManager historyManager;

    @BeforeEach
    void setUp() {
        historyManager = Managers.getDefaultHistory();
    }

    @Test
    void canAddAndGet10TasksToHistory() {
        for (int i = 0; i < 10; i++) {
            Task task = new Task("Task " + i, "Description " + i, TaskStatus.NEW);
            historyManager.addToHistory(task);
        }

        List<Task> historyList = historyManager.getHistory();

        // Проверяем, что в истории 10 элементов
        assertEquals(10, historyList.size());

        System.out.println("History:");
        for (Task task : historyList) {
            System.out.println(task);
        }
    }

    @Test
    void canNotAddAndGet11TasksToHistory() {
        for (int i = 0; i < 11; i++) {
            Task task = new Task("Task " + (i+1), "Description " + i, TaskStatus.NEW);
            historyManager.addToHistory(task);
        }

        List<Task> historyList = historyManager.getHistory();

        // Проверяем, что в истории 10 элементов
        assertEquals(10, historyList.size());

        System.out.println("History:");
        for (Task task : historyList) {
            System.out.println(task);
        }
    }
}