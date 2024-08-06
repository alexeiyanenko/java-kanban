package tasks;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class SubtaskTest {
    @Test
    void taskSubclassesWithEqualIdsShouldBeEqual() {
        // Создаем два экземпляра Subtask с одинаковыми id
        Subtask subtask1 = new Subtask(1, 1, "Subtask 1", "SubDescription 1", TaskStatus.NEW);
        Subtask subtask2 = new Subtask(1, 1, "Subtask 1", "SubDescription 1", TaskStatus.NEW);

        // Проверяем, что экземпляры равны друг другу
        assertEquals(subtask1, subtask2, "Экземпляры Subtask с одинаковыми id не равны");
    }
}