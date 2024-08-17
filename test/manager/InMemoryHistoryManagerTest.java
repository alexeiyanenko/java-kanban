package manager;

import tasks.Task;
import tasks.Subtask;
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
    private Subtask subtask;
    private Epic epic;
    private Task task2;
    private Task task3;

    @BeforeEach
    void setUp() {
        historyManager = Managers.getDefaultHistory();
        manager = Managers.getDefault(historyManager);

        task = new Task("Task", "Description", TaskStatus.NEW);
        manager.addTask(task);

        epic = new Epic("Epic", "Epic Description", TaskStatus.NEW);
        manager.addEpic(epic);  // Сначала добавляем Epic
        subtask = new Subtask(epic.getId(), "Subtask", "Subtask Description", TaskStatus.NEW);
        manager.addSubtask(subtask);  // Теперь добавляем Subtask, когда Epic уже существует

        task2 = new Task("Task2", "Description2", TaskStatus.NEW);
        manager.addTask(task2);

        task3 = new Task("Task3", "Description3", TaskStatus.NEW);
        manager.addTask(task3);
    }

    @AfterEach
    void tearDown() { manager.removeAll(); }

    @Test
    public void removingTaskFromBeginningOfHistory() {
        historyManager.add(task);
        historyManager.add(task2);
        historyManager.add(task3);

        historyManager.remove(task.getId());

        List<Task> history = historyManager.getHistory();
        assertEquals(2, history.size());
        assertEquals(task2, history.get(0));
        assertEquals(task3, history.get(1));
    }

    @Test
    public void removingTaskFromMiddleOfHistory() {
        historyManager.add(task);
        historyManager.add(task2);
        historyManager.add(task3);

        historyManager.remove(task2.getId());

        List<Task> history = historyManager.getHistory();
        assertEquals(2, history.size());
        assertEquals(task, history.get(0));
        assertEquals(task3, history.get(1));
    }

    @Test
    public void removingTaskFromEndOfHistory() {
        historyManager.add(task);
        historyManager.add(task2);
        historyManager.add(task3);

        historyManager.remove(task3.getId());

        List<Task> history = historyManager.getHistory();
        assertEquals(2, history.size());
        assertEquals(task, history.get(0));
        assertEquals(task2, history.get(1));
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
        manager.getSubtaskById(subtask.getId());
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
        manager.getSubtaskById(subtask.getId());
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
        manager.getSubtaskById(subtask.getId());
        manager.getTaskById(task2.getId());

        manager.removeAll(); // Удаление всех задач

        // Проверяем, что история пуста
        assertTrue(historyManager.getHistory().isEmpty(), "History should be empty after removing all tasks");
    }

    @Test
    void taskShouldBeRemovedFromHistoryAfterDeleting() {
        manager.getTaskById(task.getId()); // Просмотр задачи для добавления в историю
        manager.removeTask(task.getId()); // Удаляем задачу

        // Проверяем, что история пуста
        assertTrue(historyManager.getHistory().isEmpty(), "History should be empty after removing the task");
    }

    @Test
    void epicShouldBeRemovedFromHistoryAfterDeleting() {
        manager.getEpicById(epic.getId());  // Просмотр эпика для добавления в историю
        manager.removeEpic(epic.getId());   // Удаляем эпик

        // Проверяем, что история пуста
        assertTrue(historyManager.getHistory().isEmpty(), "History should be empty after removing the epic");
    }

    @Test
    void subtaskShouldBeRemovedFromHistoryAfterDeleting() {
        manager.getSubtaskById(subtask.getId());  // Просмотр подзадачи для добавления в историю
        manager.removeSubtask(subtask.getId());   // Удаляем подзадачи

        // Проверяем, что история пуста
        assertTrue(historyManager.getHistory().isEmpty(), "History should be empty after removing the subtask");
    }

    @Test
    void subtasksShouldBeDeletedFromHistoryAfterRemovingEpic() {
        manager.getSubtaskById(subtask.getId());  // Просмотр подзадачи для добавления в историю
        manager.removeEpic(epic.getId());   // Удаляем эпик

        // Проверяем, что история пуста
        assertTrue(historyManager.getHistory().isEmpty(), "History should be empty after removing the epic");
    }

    @Test
    public void taskShouldBeRemovedFromAllStructures() {
        historyManager.add(task);
        historyManager.add(task2);
        manager.removeTask(task.getId());

        List<Task> history = historyManager.getHistory();
        assertFalse(history.contains(task), "Task should be removed from history");
        assertTrue(history.contains(task2), "Other tasks should remain in history");
    }

    @Test
    public void epicAndSubtasksShouldBeRemovedFromAllStructures() {
        historyManager.add(epic);
        historyManager.add(subtask);
        manager.removeEpic(epic.getId());

        List<Task> history = historyManager.getHistory();
        assertFalse(history.contains(epic), "Epic should be removed from history");
        assertFalse(history.contains(subtask), "Subtasks of the epic should be removed from history");
    }
}