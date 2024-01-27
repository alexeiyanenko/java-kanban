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

    /*
    По заданию, кроме прочих проверок, нужно было сделать 2 таких теста:
    1) проверьте, что объект Epic нельзя добавить в самого себя в виде подзадачи;
    2) проверьте, что объект Subtask нельзя сделать своим же эпиком;

    Не совсем понимаю, как в принципе сделать такие операции.
    Тест 1. Если я применю метод manager.addSubTask() с объектом класса Epic в качестве параметра, то ничего не выйдет.
    Если я, в теории, сделаю один общий метод для добавления всех типов задач и неправильно распишу if проверкой типа...
    Или если я оставлю этот же общий метод и перегружу его для разных типов задач, и объект класса Epic каким-то образом
    попадет не в ту реализацию, то в любом случае у объекта типа Epic просто нет поля, где можно указать id "родительской задачи".

    Тест 2. Во втором тесте история, как я вижу, будет похожая. Если я сделаю тот же общий метод для добавления всех типов задач
    и забуду проверить объект на принадлежность к классу Subtask, то объект класса Subtask не полетит в реализацию для Epic.
    Ближе будет реализация метода для класса Task. Ну и в классе Subtask нет поля со списком для дочерних задач.

    Полагаю, что такие тесты просто не запустятся и приведут к ошибке. Но я сделал доп. тесты на проверку смены статусов.
    С помощью них успел найти ошибку в логике прикрепления сабтасок к эпикам, исправил.

    Есть еще такое задание по тестам: убедитесь, что задачи, добавляемые в HistoryManager,
    сохраняют предыдущую версию задачи и её данных.
    Я, если честно, просто не понял формулировку. Не понимаю, о какой предыдущей версии идет речь.
    Этот тест, понтное дело, должен быть в другом файле, не здесь. Но, чтобы не разбрасывать комментарии по коду, пишу тут :)
    */

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