package manager;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;
import tasks.TaskStatus;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FileBackedTaskManagerTest {
    private File tempFile;
    private FileBackedTaskManager manager;

    @BeforeEach
    void setUp() throws IOException {
        tempFile = File.createTempFile("kanban", ".csv");
        manager = new FileBackedTaskManager(tempFile);
    }

    @AfterEach
    void tearDown() {
        tempFile.delete();
    }

    @Test
    void saveAndLoadEmptyFile() {
        manager.save();
        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(tempFile);

        assertTrue(loadedManager.getAllTasks().isEmpty(), "Task list should be empty");
        assertTrue(loadedManager.getEpics().isEmpty(), "Epic list should be empty");
        assertTrue(loadedManager.getSubtasks().isEmpty(), "Subtask list should be empty");
    }

    @Test
    void saveAndLoadMultipleTasks() {
        Task task1 = new Task(1, "Task1", "Description1", TaskStatus.NEW);
        Task task2 = new Task(2, "Task2", "Description2", TaskStatus.IN_PROGRESS);
        Epic epic = new Epic(3, "Epic1", "Description3", TaskStatus.NEW);
        Subtask subtask = new Subtask(epic.getId(), 4, "Subtask1", "Description4", TaskStatus.DONE);

        manager.addTask(task1);
        manager.addTask(task2);
        manager.addEpic(epic);
        manager.addSubtask(subtask);

        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(tempFile);

        assertEquals(2, loadedManager.getTasks().size(), "Task list size should be 2");
        assertEquals(1, loadedManager.getEpics().size(), "Epic list size should be 1");
        assertEquals(1, loadedManager.getSubtasks().size(), "Subtask list size should be 1");

        assertEquals(task1, loadedManager.getTaskById(task1.getId()), "Loaded task1 should match original");
        assertEquals(task2, loadedManager.getTaskById(task2.getId()), "Loaded task2 should match original");
        assertEquals(epic, loadedManager.getEpicById(epic.getId()), "Loaded epic should match original");
        assertEquals(subtask, loadedManager.getSubtaskById(subtask.getId()), "Loaded subtask should match original");
    }

    @Test
    void saveAndLoadSingleTask() {
        Task task = new Task(1, "Task1", "Description1", TaskStatus.NEW);
        manager.addTask(task);

        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(tempFile);

        assertEquals(1, loadedManager.getAllTasks().size(), "Task list size should be 1");
        assertEquals(task, loadedManager.getTaskById(task.getId()), "Loaded task should match original");
    }

    @Test
    void saveAndLoadSingleEpic() {
        Epic epic = new Epic(1, "Epic1", "Description1", TaskStatus.NEW);
        manager.addEpic(epic);

        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(tempFile);

        assertEquals(1, loadedManager.getEpics().size(), "Epic list size should be 1");
        assertEquals(epic, loadedManager.getEpicById(epic.getId()), "Loaded epic should match original");
    }

    @Test
    void saveAndLoadSingleSubtask() {
        Epic epic = new Epic(1, "Epic1", "Description1", TaskStatus.NEW);
        manager.addEpic(epic);
        Subtask subtask = new Subtask(epic.getId(), 2, "Subtask1", "Description2", TaskStatus.DONE);
        manager.addSubtask(subtask);

        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(tempFile);

        assertEquals(1, loadedManager.getEpics().size(), "Epic list size should be 1");
        assertEquals(1, loadedManager.getSubtasks().size(), "Subtask list size should be 1");
        assertEquals(subtask, loadedManager.getSubtaskById(subtask.getId()), "Loaded subtask should match original");
    }

    @Test
    void saveAndLoadSingleSubtaskWithDurationAndStartTime() {
        Epic epic = new Epic(1, "Epic1", "Description1", TaskStatus.NEW);
        manager.addEpic(epic);
        Subtask subtask = new Subtask(epic.getId(), 2, "Subtask1", "Description2", TaskStatus.DONE, 10);
        subtask.setStartTime(2024, 9, 1, 9, 0);
        manager.addSubtask(subtask);

        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(tempFile);

        assertEquals(1, loadedManager.getEpics().size(), "Epic list size should be 1");
        assertEquals(1, loadedManager.getSubtasks().size(), "Subtask list size should be 1");
        assertEquals(subtask, loadedManager.getSubtaskById(subtask.getId()), "Loaded subtask should match original");
    }
}

