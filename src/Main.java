import java.util.List;

public class Main {
    public static void main(String[] args) {
        // Создаем задачи
        Task task1 = new Task("Task 1", "Description 1");
        Task task2 = new Task("Task 2", "Description 2");

        // Создаем подзадачи
        SubTask subTask1 = new SubTask("SubTask 1", "SubDescription 1");
        SubTask subTask2 = new SubTask("SubTask 2", "SubDescription 2");

        // Создаем эпики с подзадачами
        Epic epic1 = new Epic("Epic 1", "Epic Description 1", List.of(subTask1, subTask2));
        Epic epic2 = new Epic("Epic 2", "Epic Description 2", List.of(subTask1));

        // Создаем менеджера и сохраняем задачи
        Manager manager = new Manager();
        manager.saveTask(epic1);
        manager.saveTask(epic2);
        manager.saveTask(task1);
        manager.saveTask(task2);
        manager.saveTask(subTask1);
        manager.saveTask(subTask2);

        // Выводим списки задач
        System.out.println("Task lists:");
        System.out.println(manager.getEpics());
        System.out.println(manager.getTasks());
        System.out.println(manager.getSubTasks());

        //Изменяем статусы
        task1.setStatus(TaskStatus.IN_PROGRESS.toString());
        subTask1.setStatus(TaskStatus.DONE.toString());
        subTask2.setStatus(TaskStatus.IN_PROGRESS.toString());
        manager.updateEpicStatus(epic1);
        manager.updateEpicStatus(epic2);

        // Выводим обновленные списки
        System.out.println("\nLists after status update:");
        System.out.println(manager.getEpics());
        System.out.println(manager.getTasks());
        System.out.println(manager.getSubTasks());

        // Удаляем одну задачу и один эпик
        manager.removeTaskById(task2.getId());
        manager.removeTaskById(epic1.getId());

        // Выводим списки после удаления
        System.out.println("\nLists after removal:");
        System.out.println(manager.getEpics());
        System.out.println(manager.getTasks());
        System.out.println(manager.getSubTasks());
    }
}
