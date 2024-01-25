import manager.Manager;
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



        // Создаем менеджера и сохраняем задачи
        Manager manager = new Manager();
        manager.addTask(task1);
        manager.addTask(task2);
        manager.addEpic(epic1);
        manager.addEpic(epic2);
        manager.addSubTask(subTask1);
        manager.addSubTask(subTask2);
        manager.addSubTask(subTask3);

        // Выводим списки задач
        System.out.println("Task lists:");
        System.out.println(manager.getEpics());
        System.out.println(manager.getSubTasks());
        System.out.println(manager.getTasks());


        //Изменяем статусы
        task1.setStatus(TaskStatus.IN_PROGRESS);
        subTask1.setStatus(TaskStatus.DONE);
        subTask2.setStatus(TaskStatus.IN_PROGRESS);
        subTask3.setStatus(TaskStatus.IN_PROGRESS);

        manager.updateTask(task1);
        manager.updateSubTask(subTask1);
        manager.updateSubTask(subTask2);
        manager.updateSubTask(subTask3);

        // Выводим обновленные списки
        System.out.println("\nLists after status update:");
        System.out.println(manager.getEpics());
        System.out.println(manager.getSubTasks());
        System.out.println(manager.getTasks());


        // Удаляем одну задачу и один эпик
        manager.removeTask(task2.getId());
        manager.removeEpic(epic1.getId());

        // Выводим списки после удаления
        System.out.println("\nLists after removal:");
        System.out.println(manager.getEpics());
        System.out.println(manager.getSubTasks());
        System.out.println(manager.getTasks());
    }
}
