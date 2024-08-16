package tasks;

import manager.TaskManager;
import manager.InMemoryTaskManager;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

class EpicTest {

    public Epic epic;
    public Subtask subtask1;
    public Subtask subtask2;
    public Subtask subtask3;
    public TaskManager manager;

    @BeforeEach
    void createEpicAndSubtasks() {
        manager = new InMemoryTaskManager();
        epic = new Epic("Epic", "Description of epic", TaskStatus.NEW);
        manager.addEpic(epic);
        int epicId = epic.getId();
        subtask1 = new Subtask(epicId, "Subtask 1", "Description of subtask1", TaskStatus.NEW);
        subtask2 = new Subtask(epicId, "Subtask 2", "Description of subtask2", TaskStatus.NEW);
        subtask3 = new Subtask(epicId, "Subtask 3", "Description of subtask3", TaskStatus.NEW);
        manager.addSubtask(subtask1);
        manager.addSubtask(subtask2);
        manager.addSubtask(subtask3);
    }

    @AfterEach
    void removeEpicAndSubtasks() {
        manager.removeAllEpics();
    }

    @Test
    void shouldReturnNewEpicStatusWithoutSubtasks() {
        manager.removeAllSubtasks();
        assertEquals(TaskStatus.NEW, epic.getStatus(), "Статус пустого эпика не NEW");
    }

    @Test
    void epicCannotBeAddedAsSubtaskToItself() {
        manager.addTask(epic);
    }

    @Test
    void shouldReturnNewEpicStatusWithAllSubtasksNewStatus() {
        manager.updateEpicStatus(epic.getId());
        assertEquals(TaskStatus.NEW, epic.getStatus(), "Статус эпика c новыми сабтасками не NEW");
    }

    @Test
    void shouldReturnProgressEpicStatusWithSubtasksProgressStatus() {
        subtask1.setStatus(TaskStatus.IN_PROGRESS);
        subtask2.setStatus(TaskStatus.IN_PROGRESS);

        manager.updateSubtask(subtask1);
        manager.updateSubtask(subtask2);

        assertEquals(TaskStatus.IN_PROGRESS, epic.getStatus(), "Статус эпика с сабтасками со статусом IN_PROGRESS не IN_PROGRESS");
    }

    @Test
    void shouldReturnProgressEpicStatusWithSubtasksNewAndDoneStatuses() {
        subtask1.setStatus(TaskStatus.DONE);
        subtask2.setStatus(TaskStatus.DONE);
        subtask3.setStatus(TaskStatus.NEW);

        manager.updateSubtask(subtask1);
        manager.updateSubtask(subtask2);
        manager.updateSubtask(subtask3);

        assertEquals(TaskStatus.IN_PROGRESS, epic.getStatus(), "Статус эпика с сабтасками со статусами NEW и DONE не IN_PROGRESS");
    }

    @Test
    void shouldReturnDoneEpicStatusWithAllSubtasksDoneStatus() {
        subtask1.setStatus(TaskStatus.DONE);
        subtask2.setStatus(TaskStatus.DONE);
        subtask3.setStatus(TaskStatus.DONE);

        manager.updateSubtask(subtask1);
        manager.updateSubtask(subtask2);
        manager.updateSubtask(subtask3);

        assertEquals(TaskStatus.DONE, epic.getStatus(), "Статус эпика со всеми сабтасками в статусе DONE не DONE");
    }
}