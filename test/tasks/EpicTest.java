package tasks;

import manager.TaskManager;
import manager.InMemoryTaskManager;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

class EpicTest {

    public Epic epic;
    public SubTask subTask1;
    public SubTask subTask2;
    public SubTask subTask3;
    public TaskManager manager;

    @BeforeEach
    void createEpicAndSubTasks() {
        manager = new InMemoryTaskManager();
        epic = new Epic("Epic", "Description of epic", TaskStatus.NEW);
        manager.addEpic(epic);
        int epicId = epic.getId();
        subTask1 = new SubTask(epicId, "Subtask 1", "Description of subtask1", TaskStatus.NEW);
        subTask2 = new SubTask(epicId, "Subtask 2", "Description of subtask2", TaskStatus.NEW);
        subTask3 = new SubTask(epicId, "Subtask 3", "Description of subtask3", TaskStatus.NEW);
        manager.addSubTask(subTask1);
        manager.addSubTask(subTask2);
        manager.addSubTask(subTask3);
    }

    @AfterEach
    void removeEpicAndSubTasks() {
        manager.removeAllEpics();
    }

    @Test
    void shouldReturnNewEpicStatusWithoutSubtasks() {
        manager.removeAllSubTasks();
        assertEquals(TaskStatus.NEW, epic.getStatus(), "Статус пустого эпика не NEW");
    }

    @Test
    void epicCannotBeAddedAsSubTaskToItself() {
        manager.addTask(epic);
    }

    @Test
    void shouldReturnNewEpicStatusWithAllSubtasksNewStatus() {
        manager.updateEpicStatus(epic.getId());
        assertEquals(TaskStatus.NEW, epic.getStatus(), "Статус эпика c новыми сабтасками не NEW");
    }

    @Test
    void shouldReturnProgressEpicStatusWithSubtasksProgressStatus() {
        subTask1.setStatus(TaskStatus.IN_PROGRESS);
        subTask2.setStatus(TaskStatus.IN_PROGRESS);

        manager.updateSubTask(subTask1);
        manager.updateSubTask(subTask2);

        assertEquals(TaskStatus.IN_PROGRESS, epic.getStatus(), "Статус эпика с сабтасками со статусом IN_PROGRESS не IN_PROGRESS");
    }

    @Test
    void shouldReturnIn_ProgressEpicStatusWithSubtasksNewAndDoneStatuses() {
        subTask1.setStatus(TaskStatus.DONE);
        subTask2.setStatus(TaskStatus.DONE);
        subTask3.setStatus(TaskStatus.NEW);

        manager.updateSubTask(subTask1);
        manager.updateSubTask(subTask2);
        manager.updateSubTask(subTask3);

        assertEquals(TaskStatus.IN_PROGRESS, epic.getStatus(), "Статус эпика с сабтасками со статусами NEW и DONE не IN_PROGRESS");
    }

    @Test
    void shouldReturnDoneEpicStatusWithAllSubtasksDoneStatus() {
        subTask1.setStatus(TaskStatus.DONE);
        subTask2.setStatus(TaskStatus.DONE);
        subTask3.setStatus(TaskStatus.DONE);

        manager.updateSubTask(subTask1);
        manager.updateSubTask(subTask2);
        manager.updateSubTask(subTask3);

        assertEquals(TaskStatus.DONE, epic.getStatus(), "Статус эпика со всеми сабтасками в статусе DONE не DONE");
    }
}