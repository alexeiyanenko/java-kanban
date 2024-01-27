package tasks;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TaskTest {
    @Test
    void taskInstancesWithEqualIdsShouldBeEqual() {
        // Создаем два экземпляра Task с одинаковыми id
        Task task1 = new Task(1, "Task 1", "Description 1", TaskStatus.NEW);
        Task task2 = new Task(1, "Task 1", "Description 1", TaskStatus.NEW);

        // Проверяем, что экземпляры равны друг другу
        assertEquals(task1, task2, "Экземпляры Task с одинаковыми id не равны");
    }
}