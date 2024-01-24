import Tasks.Epic;
import Tasks.SubTask;
import Tasks.Task;
import Tasks.TaskStatus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Manager {
    private Map<Integer, Task> tasks;
    private Map<Integer, Epic> epics;
    private Map<Integer, SubTask> subTasks;
    private static int generatorId = 0;

    public Manager() {
        tasks = new HashMap<>();
        epics = new HashMap<>();
        subTasks = new HashMap<>();
    }

    //Добавление новых задач
    public void addTask(Task task) {
        if (task.getId() == 0) {
            final int id = ++generatorId;
            task.setId(id);
        }
        tasks.put(task.getId(), task);
    }

    public void addEpic(Epic epic) {
        if (epic.getId() == 0) {
            final int id = ++generatorId;
            epic.setId(id);
        }
        epics.put(epic.getId(), epic);
    }

    public void addSubTask(SubTask subTask) {
        if (subTask.getId() == 0) {
            final int id = ++generatorId;
            subTask.setId(id);
        }
        int id = subTask.getId();
        subTasks.put(id, subTask);

        int epicId = subTask.getEpicId();
        epics.get(epicId).getSubTaskOfEpicIDs().add(id);
    }

    //Обновление существующих задач
    public void updateTask(Task task) {
        tasks.put(task.getId(), task);
    }

    public void updateEpic(Epic epic) {
        int id = epic.getId();
        String newName = epic.getName();
        String newDescription = epic.getDescription();
        epics.get(id).setName(newName);
        epics.get(id).setDescription(newDescription);
    }

    public void updateSubTask(SubTask subTask) {
        subTasks.put(subTask.getId(), subTask);
        updateEpicStatus(subTask.getEpicId());
    }

    //Получение списков задач
    public List<Task> getAllTasks() {
        List<Task> allTasks = new ArrayList<>();
        allTasks.addAll(tasks.values());
        allTasks.addAll(epics.values());
        allTasks.addAll(subTasks.values());
        return allTasks;
    }

    public List<Task> getTasks() {
        return new ArrayList<>(tasks.values());
    }

    public List<Epic> getEpics() {
        return new ArrayList<>(epics.values());
    }
    public List<SubTask> getSubTasks() {
        return new ArrayList<>(subTasks.values());
    }

    //Удаление задач
    public void removeAll() {
        tasks.clear();
        subTasks.clear();
        epics.clear();
    }

    public void removeAllTasks() {
        tasks.clear();
    }

    public void removeAllSubTasks() {
        subTasks.clear();

        for (Epic epic : epics.values()) {
            epic.getSubTaskOfEpicIDs().clear();
        }
    }

    public void removeAllEpics() {
        epics.clear();
        removeAllSubTasks();
    }

    //Получение по идентификатору
    public Task getTaskById(int taskId) {
        return tasks.get(taskId);
    }

    public SubTask getSubTaskById(int subTaskId) {
        return subTasks.get(subTaskId);
    }

    public Epic getEpicById(int epicId) {
        return epics.get(epicId);
    }


    //Удаление по идентификатору
    public boolean removeTask(int id) {
        if (tasks.containsKey(id)) {
            tasks.remove(id);
            return true;
        }

        return false;
    }

    public boolean removeSubTask(int id) {
        if (subTasks.containsKey(id)) {
            subTasks.remove(id);
            return true;
        }

        return false;
    }

    public boolean removeEpic(int id) {
        if (epics.containsKey(id)) {
            epics.remove(id);
            return true;
        }

        return false;
    }

    //Получение списка всех подзадач определённого эпика
    public List<SubTask> getSubTasksOfEpic(Epic epic) {
        List<SubTask> subTasksOfEpic = new ArrayList<>();

        for(int id : epic.getSubTaskOfEpicIDs()) {
            subTasksOfEpic.add(subTasks.get(id));
        }

        return subTasksOfEpic;
    }

    // Метод для управления статусами эпиков
    private void updateEpicStatus(int epicId) {
        Epic epic = epics.get(epicId);
        ArrayList<Integer> subTaskIDs = epic.getSubTaskOfEpicIDs();
        if (subTaskIDs.isEmpty()) {
            epic.setStatus(TaskStatus.NEW);
            return;
        }
        TaskStatus status = null;
        for (int id : subTaskIDs) {
            final SubTask subTask = subTasks.get(id);
            if (status == null) {
                status = subTask.getStatus();
                continue;
            }
            if (status.equals(subTask.getStatus())
                    && !status.equals(TaskStatus.IN_PROGRESS)) {
                continue;
            }
            epic.setStatus(TaskStatus.IN_PROGRESS);
            return;
        }
        epic.setStatus(status);
    }


    /*public void updateEpicStatus(int id) {
        Tasks.Epic epic = epics.get(id);
        List<Tasks.SubTask> subTasksOfEpic = getSubTasksOfEpic(epic);

        if (subTasksOfEpic.isEmpty() || allSubTasksNew(subTasksOfEpic)) {
            epic.setStatus(Tasks.TaskStatus.NEW);
        } else if (allSubTasksDone(subTasksOfEpic)) {
            epic.setStatus(Tasks.TaskStatus.DONE);
        } else {
            epic.setStatus(Tasks.TaskStatus.IN_PROGRESS);
        }
    }

    // Вспомогательные методы для проверки статусов подзадач в эпике
    private boolean allSubTasksNew(List<Tasks.SubTask> subTasks) {
        for (Tasks.SubTask subTask : subTasks) {
            if (!subTask.getStatus().equals(Tasks.TaskStatus.NEW)) {
                return false;
            }
        }
        return true;
    }
    private boolean allSubTasksDone(List<Tasks.SubTask> subTasks) {
        for (Tasks.SubTask subTask : subTasks) {
            if (!subTask.getStatus().equals(Tasks.TaskStatus.DONE)) {
                return false;
            }
        }
        return true;
    }*/
}
