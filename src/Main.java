import manager.Managers;
import manager.TaskManager;
import tasks.Epic;
import tasks.SubTask;
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
        SubTask subTask1 = new SubTask(3, "SubTask 1 in Epic 1", "SubDescription 1", TaskStatus.NEW);
        SubTask subTask2 = new SubTask(3, "SubTask 2 in Epic 1", "SubDescription 2", TaskStatus.NEW);
        SubTask subTask3 = new SubTask(4, "SubTask 1 in Epic 2", "SubDescription 1", TaskStatus.NEW);

        // Создаем менеджеров
        TaskManager manager = Managers.getDefault();

        //Сохраняем задачи
        manager.addTask(task1);
        manager.addTask(task2);
        manager.addEpic(epic1);
        manager.addEpic(epic2);
        manager.addSubTask(subTask1);
        manager.addSubTask(subTask2);
        manager.addSubTask(subTask3);

        //Обращение к задачам для сохранения в истории
        System.out.println(manager.getEpicById(3));
        System.out.println(manager.getSubTaskById(7));
        System.out.println(manager.getTaskById(1));

        manager.printHistory();
    }
}
