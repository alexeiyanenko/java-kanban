package manager;

import tasks.Epic;
import tasks.SubTask;
import tasks.Task;
import tasks.TaskStatus;

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

        updateEpicStatus(subTask.getEpicId());
    }

    //Обновление существующих задач
    public void updateTask(Task task) {
        final Task savedTask = tasks.get(task.getId());
        if (savedTask == null) {
            return;
        }
        tasks.put(task.getId(), task);
    }

    public void updateEpic(Epic epic) {
        final Epic savedEpic = epics.get(epic.getId());
        if (savedEpic == null) {
            return;
        }
        int id = epic.getId();
        String newName = epic.getName();
        String newDescription = epic.getDescription();
        epics.get(id).setName(newName);
        epics.get(id).setDescription(newDescription);
    }

    public void updateSubTask(SubTask subTask) {
        final Task savedSubTask = tasks.get(subTask.getId());
        if (savedSubTask == null) {
            return;
        }
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
            updateEpicStatus(epic.getId());
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
    public void removeTask(int id) {
        if (tasks.containsKey(id)) {
            tasks.remove(id);
        }
    }

    public void removeSubTask(int id) {
        if (subTasks.containsKey(id)) {
            int epicId = subTasks.get(id).getEpicId();
            subTasks.remove(id);

            epics.get(epicId).getSubTaskOfEpicIDs().remove((Integer)id);
            updateEpicStatus(epicId);
        }
    }

    public void removeEpic(int id) {
        if (epics.containsKey(id)) {
            for (int subTaskId : epics.get(id).getSubTaskOfEpicIDs()) {
                subTasks.remove(subTaskId);
            }
            epics.remove(id);
        }
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
}
