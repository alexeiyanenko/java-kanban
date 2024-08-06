package manager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;
import tasks.TaskStatus;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {
    private TaskManager manager;

    @BeforeEach
    void SetUp() throws IOException {
        super.setUp();
        manager = Managers.getDefault();
        manager.addTask(task);
        manager.addEpic(epic);
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
    void canAddAndFindTasksWithGeneratedIds() {
        Task taskWithoutID = new Task("Task Without ID", "Description", TaskStatus.IN_PROGRESS);
        manager.addTask(taskWithoutID);

        Task foundTaskWithoutID = manager.getTaskById(taskWithoutID.getId());

        assertNotNull(foundTaskWithoutID, "Задача со сгенерированным id не была найдена");
        assertEquals(taskWithoutID, foundTaskWithoutID, "Найденная задача со сгенерированным id не соответствует ожидаемой");
    }

    @Test
    public void testUpdateTask() {
        task.setStatus(TaskStatus.DONE);
        task.setName("Updated Task");
        task.setDescription("Updated description");

        manager.updateTask(task);

        Task updatedTask = manager.getTaskById(task.getId());

        assertEquals(TaskStatus.DONE, updatedTask.getStatus());
        assertEquals("Updated Task", updatedTask.getName());
        assertEquals("Updated description", updatedTask.getDescription());
    }

    @Test
    public void testUpdateEpic() {
        epic.setName("Updated Epic");
        epic.setDescription("Updated description");

        manager.updateEpic(epic);

        Task updatedEpic = manager.getEpicById(epic.getId());

        assertEquals("Updated Epic", updatedEpic.getName());
        assertEquals("Updated description", updatedEpic.getDescription());
    }

    @Test
    public void testUpdateSubtask() {
        subtask.setStatus(TaskStatus.DONE);
        subtask.setName("Updated Subtask");
        subtask.setDescription("Updated description");

        manager.updateTask(task);

        Task updatedSubtask = manager.getSubtaskById(subtask.getId());

        assertEquals(TaskStatus.DONE, updatedSubtask.getStatus());
        assertEquals("Updated Subtask", updatedSubtask.getName());
        assertEquals("Updated description", updatedSubtask.getDescription());
    }

    @Test
    public void when30MinutesBetweenTasks_TasksShouldNotOverlap() { //Промежуток между временем выполнения задач - 30 минут
        task.setStartTime(2024, 8, 6, 9, 0);
        task.setDuration(30);
        subtask.setStartTime(2024, 8, 6, 10, 0);
        subtask.setDuration(30);

        manager.updateTask(task);
        manager.updateTask(subtask);

        assertFalse(manager.isTimeOverlap(task));
        assertFalse(manager.isTimeOverlap(subtask));
    }

    @Test
    public void when0MinutesBetweenTasks_TasksShouldNotOverlap() { //Промежуток между временем выполнения задач - 0 минут
        task.setStartTime(2024, 8, 6, 9, 0);
        task.setDuration(60);
        subtask.setStartTime(2024, 8, 6, 10, 0);
        subtask.setDuration(30);

        manager.updateTask(task);
        manager.updateTask(subtask);

        assertFalse(manager.isTimeOverlap(subtask));
    }

    @Test
    public void testOverlap() { //Накладка времени выполнения задач - 10 минут
        task.setStartTime(2024, 8, 6, 9, 0);
        task.setDuration(70);
        subtask.setStartTime(2024, 8, 6, 10, 0);
        subtask.setDuration(30);

        manager.updateTask(task);
        manager.updateTask(subtask);

        assertTrue(manager.isTimeOverlap(subtask));
    }

    @Test
    public void whenNoStartTimeAndDuration_TasksShouldNotOverlap() {
        task.setStartTime(null);
        subtask.setStartTime(null);

        manager.updateTask(task);
        manager.updateTask(subtask);

        assertFalse(manager.isTimeOverlap(subtask));
    }

    @Test
    public void whenNoStartTime_ThereAreDurations_TasksShouldNotOverlap() {
        task.setStartTime(null);
        subtask.setStartTime(null);

        task.setDuration(20);
        subtask.setDuration(20);

        manager.updateTask(task);
        manager.updateTask(subtask);

        assertFalse(manager.isTimeOverlap(subtask));
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

    @Test
    public void testSubtaskHasEpic() {
        Subtask tempSubtask = manager.getSubtaskById(subtask.getId());
        assertEquals(epic.getId(), tempSubtask.getEpicId());

        Epic tempEpic = manager.getEpicById(epic.getId());
        assertTrue(manager.getSubtasksOfEpic(tempEpic).contains(subtask));
        assertTrue(tempEpic.getSubtaskOfEpicIDs().contains(subtask.getId()));
    }
}
