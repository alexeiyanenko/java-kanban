package manager;

import tasks.Task;
import tasks.SubTask;
import tasks.Epic;
import tasks.TaskStatus;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {
    public HistoryManager historyManager;
    public TaskManager manager;
    private Task task;
    private SubTask subtask;
    private Epic epic;
    private Task task2;

    @BeforeEach
    void setUp() {
        manager = Managers.getDefault();
        historyManager = Managers.getDefaultHistory();

        task = new Task("Task", "Description", TaskStatus.NEW);
        manager.addTask(task);

        epic = new Epic("Epic", "Epic Description", TaskStatus.NEW);
        manager.addEpic(epic);  // Сначала добавляем Epic
        subtask = new SubTask(epic.getId(), "Subtask", "Subtask Description", TaskStatus.NEW);
        manager.addSubTask(subtask);  // Теперь добавляем SubTask, когда Epic уже существует

        task2 = new Task("Task2", "Description2", TaskStatus.NEW);
        manager.addTask(task2);
    }

    @AfterEach
    void tearDown() {
        manager.removeAll();
    }

    @Test
    public void whenTaskUpdated_thenNoImpactOnIdInHistory() {
        historyManager.add(task);
        task.setDescription("Updated Description");
        manager.updateTask(task);
        assertEquals("Updated Description", historyManager.getHistory().get(0).getDescription());
    }

    @Test
    void tasksInHistoryAreInOrderOfViewing() {
        //Просмотр задач для добавления в историю
        manager.getTaskById(task.getId());
        manager.getSubTaskById(subtask.getId());
        manager.getTaskById(task2.getId());

        // Проверка порядка в истории
        List<Task> history = historyManager.getHistory();
        assertEquals(3, history.size(), "History should contain 3 tasks");
        assertSame(task, history.get(0), "First task - 'task'");
        assertSame(subtask, history.get(1), "Second task - 'subtask'");
        assertSame(task2, history.get(2), "Last task - 'task2'");
    }

    @Test
    void whenViewTaskSecondTime_TaskIsAddedToHistory_OldRecordIsDeletedFromHistory() {
        //Просмотр задач для добавления в историю
        manager.getTaskById(task.getId());  // Просмотр задачи для добавления в историю
        manager.getSubTaskById(subtask.getId());
        manager.getTaskById(task.getId());  // Просмотр обычной задачи во второй раз

        // Проверка порядка в истории
        List<Task> history = historyManager.getHistory();
        assertEquals(2, history.size(), "History should contain 2 tasks");
        assertSame(subtask, history.get(0), "First task - 'subtask'");
        assertSame(task, history.get(1), "Second task - 'task'");
    }

    @Test
    void clearingHistoryAfterDeletingAllTasks() {
        //Просмотр задач для добавления в историю
        manager.getTaskById(task.getId());
        manager.getSubTaskById(subtask.getId());
        manager.getTaskById(task2.getId());

        manager.removeAll(); // Удаление всех задач

        // Проверяем, что история пуста
        assertTrue(historyManager.getHistory().isEmpty(), "History should be empty after removing all tasks");
    }

    @Test
    void removingTaskFromHistory() {
        manager.getTaskById(task.getId()); // Просмотр задачи для добавления в историю
        manager.removeTask(task.getId()); // Удаляем задачу

        // Проверяем, что история пуста
        assertTrue(historyManager.getHistory().isEmpty(), "History should be empty after removing the task");
    }

    @Test
    void removingEpicFromHistory() {
        manager.getTaskById(epic.getId());  // Просмотр эпика для добавления в историю
        manager.removeEpic(epic.getId());   // Удаляем эпик

        // Проверяем, что история пуста
        assertTrue(historyManager.getHistory().isEmpty(), "History should be empty after removing the epic");
    }

    @Test
    void removingSubtaskFromHistory() {
        manager.getTaskById(subtask.getId());  // Просмотр подзадачи для добавления в историю
        manager.removeSubTask(subtask.getId());   // Удаляем подзадачи

        // Проверяем, что история пуста
        assertTrue(historyManager.getHistory().isEmpty(), "History should be empty after removing the subtask");
    }

    @Test
    void removingSubtaskFromHistoryAfterDeletingEpic() {
        manager.getTaskById(subtask.getId());  // Просмотр подзадачи для добавления в историю
        manager.removeEpic(epic.getId());   // Удаляем эпик

        // Проверяем, что история пуста
        assertTrue(historyManager.getHistory().isEmpty(), "History should be empty after removing the epic");
    }
}