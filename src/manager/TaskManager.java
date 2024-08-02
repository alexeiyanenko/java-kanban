package manager;

import tasks.Epic;
import tasks.SubTask;
import tasks.Task;

import java.util.List;

public interface TaskManager {
    //Добавление новых задач
    void addTask(Task task);
    void addEpic(Epic epic);
    void addSubTask(SubTask subTask);

    //Обновление существующих задач
    void updateTask(Task task);
    void updateEpic(Epic epic);
    void updateSubTask(SubTask subTask);

    //Получение списков задач
    List<Task> getAllTasks();
    List<Task> getTasks();
    List<Epic> getEpics();
    List<SubTask> getSubTasks();

    //Удаление задач
    void removeAll();
    void removeAllTasks();
    void removeAllSubTasks();
    void removeAllEpics();

    //Получение по идентификатору
    Task getTaskById(int taskId);
    SubTask getSubTaskById(int subTaskId);
    Epic getEpicById(int epicId);
    void printHistory();

    //Удаление по идентификатору
    void removeTask(int id);
    void removeSubTask(int id);
    void removeEpic(int id);

    //Получение списка всех подзадач определённого эпика
    List<SubTask> getSubTasksOfEpic(Epic epic);

    // Метод для управления статусами эпиков
    public void updateEpicStatus(int epicId);
}
