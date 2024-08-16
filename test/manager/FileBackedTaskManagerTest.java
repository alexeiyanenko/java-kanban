package manager;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.nio.file.Files;
import tasks.Task;
import manager.FileBackedTaskManager;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class FileBackedTaskManagerTest extends TaskManagerTest<FileBackedTaskManager> {

    @BeforeEach
    void setUp() throws IOException {
        super.setUp();
        manager = new FileBackedTaskManager(file);
    }

    @AfterEach
    void tearDown() {
        file.delete();
    }

    @Test
    public void LoadTaskFromFile() {
        manager.addTask(task);
        FileBackedTaskManager manager2 = FileBackedTaskManager.loadFromFile(file);
        System.out.println("Проверка загрузки задач: " + manager.getAllTasks().equals(manager2.getAllTasks()));
    }

    @Test
    public void LoadEpicFromFile() {
        manager.addEpic(epic);
        FileBackedTaskManager manager2 = FileBackedTaskManager.loadFromFile(file);
        System.out.println("Проверка загрузки эпиков: " + manager.getAllTasks().equals(manager2.getAllTasks()));
    }

    @Test
    public void LoadSubtaskFromFile() {
        manager.addEpic(epic);
        manager.addSubtask(subtask);
        FileBackedTaskManager manager2 = FileBackedTaskManager.loadFromFile(file);
        System.out.println("Проверка загрузки подзадач: " + manager.getAllTasks().equals(manager2.getAllTasks()));
    }

    @Test
    public void LoadMultipleTasks() {
        manager.addTask(task);
        manager.addEpic(epic);
        manager.addSubtask(subtask);

        List<Task> tasks = manager.getAllTasks();

        FileBackedTaskManager manager2 = FileBackedTaskManager.loadFromFile(file);
        List<Task> tasks2 = manager2.getAllTasks();

        System.out.println("ID последней задачи: " + tasks.get(2).getId() + ", " + tasks.get(2).getId().equals(tasks2.get(2).getId()));
    }

    @Test
    void saveAndLoadEmptyFile() {
        manager.save();
        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(file);

        assertTrue(loadedManager.getAllTasks().isEmpty(), "Task list should be empty");
        assertTrue(loadedManager.getEpics().isEmpty(), "Epic list should be empty");
        assertTrue(loadedManager.getSubtasks().isEmpty(), "Subtask list should be empty");
    }

    @Test
    void saveAndLoadSingleTaskWithDurationAndStartTime() {
        task.setStartTime(2024, 9, 1, 9, 0);
        task.setDuration(30);
        manager.addTask(task);

        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(file);

        assertEquals(1, loadedManager.getTasks().size(), "Task list size should be 1");
        assertEquals(task, loadedManager.getTaskById(task.getId()), "Loaded task should match original");
    }

    @Test
    public void testSaveDoesNotThrowException() throws IOException {
        manager.addTask(task);

        assertDoesNotThrow(() -> {
            manager.save();
        }, "Expected ManagerSaveException not to be thrown, but it was");

        List<String> lines = Files.readAllLines(file.toPath());
        assertEquals(2, lines.size());
    }

    @Test
    public void testSaveThrowsIOException() throws IOException {
        // Каталог, который будет использоваться вместо файла
        File tempDir = Files.createTempDirectory("testDir").toFile();
        FileBackedTaskManager manager = new FileBackedTaskManager(tempDir);
        tempDir.deleteOnExit();

        assertThrows(ManagerSaveException.class, () -> {
            manager.save();
        }, "Expected ManagerSaveException to be thrown, but it was not");
    }

    @Test
    public void testLoadFromFileDoesNotThrowException() throws IOException {
        try (BufferedWriter writer = Files.newBufferedWriter(file.toPath())) {
            writer.write("id,type,name,status,description,duration,start time,epic\n");
            writer.write("1,TASK,Test Task,NEW,Test description,null,null\n");
        }

        assertDoesNotThrow(() -> {
            FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(file);
            List<Task> tasks = loadedManager.getTasks();
            assertEquals(1, tasks.size());
            assertEquals("Test Task", tasks.get(0).getName());
        }, "Expected ManagerSaveException not to be thrown, but it was");
    }

    @Test
    public void testLoadFromFileThrowsException() throws IOException {
        try (BufferedWriter writer = Files.newBufferedWriter(file.toPath())) {
            writer.write("id,type,name,status,description,duration,start time,epic\n");
            writer.write("1,WRONGTYPE,Test Task,NEW,Test description,null,null\n");
        }

        assertThrows(ManagerSaveException.class, () -> {
            FileBackedTaskManager.loadFromFile(file);
        }, "Expected ManagerSaveException to be thrown, but it was not");
    }
}

