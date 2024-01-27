package tasks;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class SubTaskTest {
    @Test
    void taskSubclassesWithEqualIdsShouldBeEqual() {
        // Создаем два экземпляра SubTask с одинаковыми id
        SubTask subTask1 = new SubTask(1, 1, "SubTask 1", "SubDescription 1", TaskStatus.NEW);
        SubTask subTask2 = new SubTask(1, 1, "SubTask 1", "SubDescription 1", TaskStatus.NEW);

        // Проверяем, что экземпляры равны друг другу
        assertEquals(subTask1, subTask2, "Экземпляры SubTask с одинаковыми id не равны");
    }
}