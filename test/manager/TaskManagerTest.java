package manager;

import org.junit.jupiter.api.BeforeEach;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;
import tasks.TaskStatus;

import java.io.File;
import java.io.IOException;

public abstract class TaskManagerTest<T extends TaskManager> {
    protected T manager;
    protected File file;
    protected Task task;
    protected Epic epic;
    protected Subtask subtask;

    @BeforeEach
    void setUp() throws IOException {
        file = new File("data.csv");
        task = new Task(1,"Sample Task", "Description", TaskStatus.NEW);
        epic = new Epic(2,"Epic", "Epic Description", TaskStatus.IN_PROGRESS);
        subtask = new Subtask(epic.getId(), 3, "Subtask", "Subtask Description", TaskStatus.DONE);
    }
}
