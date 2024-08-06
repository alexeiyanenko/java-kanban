package manager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;
import tasks.TaskStatus;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest {

    private TaskManager manager;
    private Task task;
    private Epic epic;
    private Subtask subtask;

    @BeforeEach
    void setUp() {
        manager = new InMemoryTaskManager();

        // Общие объекты для тестов
        task = new Task("Sample Task", "Description", TaskStatus.NEW);
        manager.addTask(task);
        epic = new Epic("Epic", "Epic Description", TaskStatus.IN_PROGRESS);
        manager.addEpic(epic);
        subtask = new Subtask(epic.getId(), "Subtask", "Subtask Description", TaskStatus.DONE);
        manager.addSubtask(subtask);
    }

    @Test
    void canAddAndFindTaskById() {
        Task foundTask = manager.getTaskById(task.getId());
        assertNotNull(foundTask, "Задача не найдена");
        assertEquals(task, foundTask, "Найденная задача не соответствует ожидаемой");
    }

    @Test
    void canAddAndFindEpicById() {
        Epic foundEpic = manager.getEpicById(epic.getId());
        assertNotNull(foundEpic, "Эпик не найден");
        assertEquals(epic, foundEpic, "Найденный эпик не соответствует ожидаемому");
    }

    @Test
    void canAddAndFindSubtaskById() {
        Subtask foundSubtask = manager.getSubtaskById(subtask.getId());
        assertNotNull(foundSubtask, "Подзадача не найдена");
        assertEquals(subtask, foundSubtask, "Найденная подзадача не соответствует ожидаемой");
    }

    @Test
    void canAddAndFindTasksWithExplicitAndGeneratedIds() {
        // Добавляем задачу с явно указанным id
        Task explicitIdTask = new Task(1, "Explicit ID Task", "Description", TaskStatus.NEW);
        manager.addTask(explicitIdTask);

        // Добавляем задачу со сгенерированным id
        Task generatedIdTask = new Task("Generated ID Task", "Description", TaskStatus.IN_PROGRESS);
        manager.addTask(generatedIdTask);

        // Получение задач
        Task foundExplicitIdTask = manager.getTaskById(explicitIdTask.getId());
        Task foundGeneratedIdTask = manager.getTaskById(generatedIdTask.getId());

        // Проверка полученных задач
        assertNotNull(foundExplicitIdTask, "Задача с явно указанным id не была найдена");
        assertNotNull(foundGeneratedIdTask, "Задача сгенерированным id не была найдена");

        assertEquals(explicitIdTask, foundExplicitIdTask, "Найденная задача с явно указанным id не соответствует ожидаемой");
        assertEquals(generatedIdTask, foundGeneratedIdTask, "Найденная задача сгенерированным id не соответствует ожидаемой");
    }

    @Test
    public void whenSubtaskRemoved_thenAlsoRemovedFromEpic() {
        manager.removeSubtask(subtask.getId());
        assertTrue(manager.getSubtasksOfEpic(epic).isEmpty());
    }

    @Test
    void subtasksShouldNotContainOldIdsAfterDeletion() {
        manager.removeSubtask(subtask.getId()); // Удаление подзадачи
        Epic updatedEpic = manager.getEpicById(epic.getId()); // Получение обновленного эпика
        assertFalse(updatedEpic.getSubtaskOfEpicIDs().contains(subtask.getId()), "Deleted subtask ID should not be in the epic");
    }
}
