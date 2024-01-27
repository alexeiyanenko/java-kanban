package manager;

import tasks.Task;
import tasks.TaskStatus;

import org.junit.jupiter.api.Test;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ManagersTest {
    @Test
    void getDefaultReturnsInitializedTaskManager() {
        TaskManager manager = Managers.getDefault();

        Task task = new Task("Task 1", "Description 1", TaskStatus.NEW);
        manager.addTask(task);

        // Получаем список всех задач через TaskManager
        List<Task> testTaskList = manager.getTasks();

        // Проверяем, что задача была успешно добавлена и вернулась в списке задач
        assertTrue(testTaskList.contains(task), "Задача не была возвращена в список задач");
    }

}