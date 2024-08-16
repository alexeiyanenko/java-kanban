import manager.Managers;
import manager.TaskManager;
import manager.HistoryManager;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;
import tasks.TaskStatus;

public class Main {

    public static void main(String[] args) {
        // Создаем задачи
        Task task1 = new Task("Task 1", "Description 1",TaskStatus.NEW);
        Task task2 = new Task("Task 2", "Description 2",TaskStatus.IN_PROGRESS);

        // Создаем эпики с подзадачами
        Epic epic1 = new Epic("Epic 1", "Epic Description 1",TaskStatus.NEW);
        Epic epic2 = new Epic("Epic 2", "Epic Description 2",TaskStatus.NEW);

        // Создаем подзадачи
        Subtask subtask1 = new Subtask(3, "Subtask 1 in Epic 1", "SubDescription 1", TaskStatus.NEW);
        Subtask subtask2 = new Subtask(3, "Subtask 2 in Epic 1", "SubDescription 2", TaskStatus.NEW);
        Subtask subtask3 = new Subtask(4, "Subtask 1 in Epic 2", "SubDescription 1", TaskStatus.NEW);

        // Создаем менеджеров
        HistoryManager historyManager = Managers.getDefaultHistory();
        TaskManager manager = Managers.getDefault(historyManager);

        //Сохраняем задачи
        manager.addTask(task1);
        manager.addTask(task2);
        manager.addEpic(epic1);
        manager.addEpic(epic2);
        manager.addSubtask(subtask1);
        manager.addSubtask(subtask2);
        manager.addSubtask(subtask3);

        //Обращение к задачам для сохранения в истории
        System.out.println(manager.getEpicById(3));
        System.out.println(manager.getSubtaskById(7));
        System.out.println(manager.getTaskById(1));

        System.out.println("History:");
        historyManager.getHistory().stream()
                .forEach(System.out::println);
    }
}
