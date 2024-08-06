package manager;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.nio.file.Files;
import tasks.Task;

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
        manager.addTask(task);
        manager.addEpic(epic);
        manager.addSubtask(subtask);

        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(tempFile);

        assertEquals(1, loadedManager.getTasks().size(), "Task list size should be 1");
        assertEquals(1, loadedManager.getEpics().size(), "Epic list size should be 1");
        assertEquals(1, loadedManager.getSubtasks().size(), "Subtask list size should be 1");

        assertEquals(task, loadedManager.getTaskById(task.getId()), "Loaded task should match original");
        assertEquals(epic, loadedManager.getEpicById(epic.getId()), "Loaded epic should match original");
        assertEquals(subtask, loadedManager.getSubtaskById(subtask.getId()), "Loaded subtask should match original");
    }

    @Test
    void saveAndLoadSingleTask() {
        manager.addTask(task);

        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(tempFile);

        assertEquals(1, loadedManager.getAllTasks().size(), "Task list size should be 1");
        assertEquals(task, loadedManager.getTaskById(task.getId()), "Loaded task should match original");
    }

    @Test
    void saveAndLoadSingleTaskWithDurationAndStartTime() {
        task.setStartTime(2024, 9, 1, 9, 0);
        task.setDuration(30);
        manager.addTask(task);

        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(tempFile);

        assertEquals(1, loadedManager.getTasks().size(), "Task list size should be 1");
        assertEquals(task, loadedManager.getTaskById(task.getId()), "Loaded task should match original");
    }

    @Test
    void saveAndLoadSingleEpic() {
        manager.addEpic(epic);

        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(tempFile);

        assertEquals(1, loadedManager.getEpics().size(), "Epic list size should be 1");
        assertEquals(epic, loadedManager.getEpicById(epic.getId()), "Loaded epic should match original");
    }

    @Test
    void saveAndLoadSingleSubtask() {
        manager.addEpic(epic);
        manager.addSubtask(subtask);

        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(tempFile);

        assertEquals(1, loadedManager.getEpics().size(), "Epic list size should be 1");
        assertEquals(1, loadedManager.getSubtasks().size(), "Subtask list size should be 1");
        assertEquals(subtask, loadedManager.getSubtaskById(subtask.getId()), "Loaded subtask should match original");
    }

    @Test
    public void testSaveDoesNotThrowException() throws IOException {
        manager.addTask(task);

        assertDoesNotThrow(() -> {
            manager.save();
        }, "Expected ManagerSaveException not to be thrown, but it was");

        List<String> lines = Files.readAllLines(tempFile.toPath());
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
        try (BufferedWriter writer = Files.newBufferedWriter(tempFile.toPath())) {
            writer.write("id,type,name,status,description,duration,start time,epic\n");
            writer.write("1,TASK,Test Task,NEW,Test description,null,null\n");
        }

        assertDoesNotThrow(() -> {
            FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(tempFile);
            List<Task> tasks = loadedManager.getTasks();
            assertEquals(1, tasks.size());
            assertEquals("Test Task", tasks.get(0).getName());
        }, "Expected ManagerSaveException not to be thrown, but it was");
    }

    @Test
    public void testLoadFromFileThrowsException() throws IOException {
        try (BufferedWriter writer = Files.newBufferedWriter(tempFile.toPath())) {
            writer.write("id,type,name,status,description,duration,start time,epic\n");
            writer.write("1,WRONGTYPE,Test Task,NEW,Test description,null,null\n");
        }

        assertThrows(ManagerSaveException.class, () -> {
            FileBackedTaskManager.loadFromFile(tempFile);
        }, "Expected ManagerSaveException to be thrown, but it was not");
    }
}

