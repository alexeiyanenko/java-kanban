package manager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.SubTask;
import tasks.Task;
import tasks.TaskStatus;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest {

    private TaskManager manager;
    private Task task;
    private Epic epic;
    private SubTask subTask;

    @BeforeEach
    void setUp() {
        manager = new InMemoryTaskManager();

        // Общие объекты для тестов
        task = new Task("Sample Task", "Description", TaskStatus.NEW);
        manager.addTask(task);
        epic = new Epic("Epic", "Epic Description", TaskStatus.IN_PROGRESS);
        manager.addEpic(epic);
        subTask = new SubTask(epic.getId(), "SubTask", "SubTask Description", TaskStatus.DONE);
        manager.addSubTask(subTask);
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
    void canAddAndFindSubTaskById() {
        SubTask foundSubTask = manager.getSubTaskById(subTask.getId());
        assertNotNull(foundSubTask, "Подзадача не найдена");
        assertEquals(subTask, foundSubTask, "Найденная подзадача не соответствует ожидаемой");
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
}
