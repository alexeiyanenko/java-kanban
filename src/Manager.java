import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Manager {
    private Map<Integer, Task> tasksById;
    private Map<Integer, SubTask> subTasksById;
    private Map<Integer, Epic> epicsById;

    public Manager() {
        tasksById = new HashMap<>();
        subTasksById = new HashMap<>();
        epicsById = new HashMap<>();
    }

    //Возможность хранить задачи всех типов
    public void saveTask(Task task) {
        if (task != null) {
            if (task instanceof SubTask) {
                SubTask subTask = (SubTask) task;
                subTasksById.put(subTask.getId(), subTask);
            } else if (task instanceof Epic) {
                Epic epic = (Epic) task;
                epicsById.put(epic.getId(), epic);
                updateEpicStatus(epic); // Обновляем статус эпика
            } else if (task instanceof Task) {
                tasksById.put(task.getId(), (Task) task);
            }
        }
    }

    //Обновление существующей задачи
    public void updateTask(Task updatedTask) {
        if (updatedTask != null) {
            if (updatedTask instanceof Task && tasksById.containsKey(updatedTask.getId())) {
                tasksById.put(updatedTask.getId(), (Task) updatedTask);
            }
            if (updatedTask instanceof SubTask && subTasksById.containsKey(updatedTask.getId())) {
                SubTask subTask = (SubTask) updatedTask;
                subTasksById.put(subTask.getId(), subTask);
            }
            if (updatedTask instanceof Epic && epicsById.containsKey(updatedTask.getId())) {
                Epic epic = (Epic) updatedTask;
                epicsById.put(epic.getId(), epic);
                updateEpicStatus(epic); // Обновляем статус эпика
            }
        }
    }


    //Получение списка всех задач
    public List<Task> getAllTasks() {
        List<Task> allTasks = new ArrayList<>();
        allTasks.addAll(tasksById.values());
        allTasks.addAll(subTasksById.values());
        allTasks.addAll(epicsById.values());
        return allTasks;
    }

    public List<Task> getTasks() {
        List<Task> tasks = new ArrayList<>();
        tasks.addAll(tasksById.values());
        return tasks;
    }

    public List<SubTask> getSubTasks() {
        List<SubTask> subTasks = new ArrayList<>();
        subTasks.addAll(subTasksById.values());
        return subTasks;
    }

    public List<Epic> getEpics() {
        List<Epic> epics = new ArrayList<>();
        epics.addAll(epicsById.values());
        return epics;
    }

    //Удаление всех задач
    public void removeAllTasks() {
        tasksById.clear();
        subTasksById.clear();
        epicsById.clear();
    }

    public void removeTasks() {
        tasksById.clear();
    }

    public void removeSubTasks() {
        subTasksById.clear();
    }

    public void removeEpics() {
        epicsById.clear();
    }

    //Получение по идентификатору
    public Task getTaskById(int taskId) {
        return tasksById.get(taskId);
    }

    public SubTask getSubTaskById(int subTaskId) {
        return subTasksById.get(subTaskId);
    }

    public Epic getEpicById(int epicId) {
        return epicsById.get(epicId);
    }


    //Удаление по идентификатору
    public void removeTaskById(int taskId) {
        tasksById.remove(taskId);
        subTasksById.remove(taskId);
        epicsById.remove(taskId);
    }

    //Получение списка всех подзадач определённого эпика
    public List<SubTask> getSubTasksOfEpic(Epic epic) {
        List<SubTask> subTasksOfEpic = new ArrayList<>();
        subTasksOfEpic = epic.getSubTasksOfEpic();
        return subTasksOfEpic;
    }

    // Метод для управления статусами эпиков
    public void updateEpicStatus(Epic epic) {
        if (epic != null) {
            List<SubTask> subTasksOfEpic = getSubTasksOfEpic(epic);

            if (subTasksOfEpic.isEmpty() || allSubTasksNew(subTasksOfEpic)) {
                epic.setStatus(TaskStatus.NEW.toString());
            } else if (allSubTasksDone(subTasksOfEpic)) {
                epic.setStatus(TaskStatus.DONE.toString());
            } else {
                epic.setStatus(TaskStatus.IN_PROGRESS.toString());
            }
        }
    }

    // Вспомогательный метод для проверки статусов подзадач в эпике
    private boolean allSubTasksNew(List<SubTask> subTasks) {
        for (SubTask subTask : subTasks) {
            if (!subTask.getStatus().equals(TaskStatus.NEW.toString())) {
                return false;
            }
        }
        return true;
    }
    private boolean allSubTasksDone(List<SubTask> subTasks) {
        for (SubTask subTask : subTasks) {
            if (!subTask.getStatus().equals(TaskStatus.DONE.toString())) {
                return false;
            }
        }
        return true;
    }
}
