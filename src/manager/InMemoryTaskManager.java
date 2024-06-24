package manager;

import tasks.Epic;
import tasks.SubTask;
import tasks.Task;
import tasks.TaskStatus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryTaskManager implements TaskManager {
    private final Map<Integer, Task> tasks;
    private final Map<Integer, Epic> epics;
    private final Map<Integer, SubTask> subTasks;
    public final HistoryManager historyManager = Managers.getDefaultHistory();
    private static int generatorId = 0;

    public InMemoryTaskManager() {
        tasks = new HashMap<>();
        epics = new HashMap<>();
        subTasks = new HashMap<>();
    }

    //Добавление новых задач
    @Override
    public void addTask(Task task) {
        while (task.getId() == 0) {
            final int id = ++generatorId;
            task.setId(id);

            if (tasks.containsKey(task.getId())) {
                task.setId(0);
            }
        }

        tasks.put(task.getId(), task);
    }

    @Override
    public void addEpic(Epic epic) {
        while (epic.getId() == 0) {
            final int id = ++generatorId;
            epic.setId(id);

            if (tasks.containsKey(epic.getId())) {
                epic.setId(0);
            }
        }

        epics.put(epic.getId(), epic);
    }

    @Override
    public void addSubTask(SubTask subTask) {
        while (subTask.getId() == 0) {
            final int id = ++generatorId;
            subTask.setId(id);

            if (tasks.containsKey(subTask.getId())) {
                subTask.setId(0);
            }
        }
        int id = subTask.getId();
        subTasks.put(subTask.getId(), subTask);

        int epicId = subTask.getEpicId();
        epics.get(epicId).getSubTaskOfEpicIDs().add(id);

        updateEpicStatus(subTask.getEpicId());
    }

    //Обновление существующих задач
    @Override
    public void updateTask(Task task) {
        final Task savedTask = tasks.get(task.getId());
        if (savedTask == null) {
            return;
        }
        tasks.put(task.getId(), task);
    }

    @Override
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

    @Override
    public void updateSubTask(SubTask subTask) {
        final Task savedSubTask = subTasks.get(subTask.getId());
        if (savedSubTask == null) {
            return;
        }
        subTasks.put(subTask.getId(), subTask);
        updateEpicStatus(subTask.getEpicId());
    }

    //Получение списков задач
    @Override
    public List<Task> getAllTasks() {
        List<Task> allTasks = new ArrayList<>();
        allTasks.addAll(tasks.values());
        allTasks.addAll(epics.values());
        allTasks.addAll(subTasks.values());
        return allTasks;
    }

    @Override
    public List<Task> getTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public List<Epic> getEpics() {
        return new ArrayList<>(epics.values());
    }
    @Override
    public List<SubTask> getSubTasks() {
        return new ArrayList<>(subTasks.values());
    }

    //Удаление задач
    @Override
    public void removeAll() {
        tasks.clear();
        subTasks.clear();
        epics.clear();
    }

    @Override
    public void removeAllTasks() {
        tasks.clear();
    }

    @Override
    public void removeAllSubTasks() {
        subTasks.clear();

        for (Epic epic : epics.values()) {
            epic.getSubTaskOfEpicIDs().clear();
            updateEpicStatus(epic.getId());
        }
    }

    @Override
    public void removeAllEpics() {
        epics.clear();
        subTasks.clear();
    }

    //Получение по идентификатору
    @Override
    public Task getTaskById(int taskId) {
        Task task = tasks.get(taskId);
        historyManager.add(task);
        return task;
    }

    @Override
    public SubTask getSubTaskById(int subTaskId) {
        SubTask subTask = subTasks.get(subTaskId);
        historyManager.add(subTask);
        return subTask;
    }

    @Override
    public Epic getEpicById(int epicId) {
        Epic epic = epics.get(epicId);
        historyManager.add(epic);
        return epic;
    }

    @Override
    public void printHistory(){
        System.out.println("History:");
        for (Task task : historyManager.getHistory()) {
            System.out.println(task);
        }
    }


    //Удаление по идентификатору
    @Override
    public void removeTask(int id) {
        if (tasks.containsKey(id)) {
            tasks.remove(id);
        }
    }

    @Override
    public void removeSubTask(int id) {
        if (subTasks.containsKey(id)) {
            int epicId = subTasks.get(id).getEpicId();
            subTasks.remove(id);

            epics.get(epicId).getSubTaskOfEpicIDs().remove((Integer)id);
            updateEpicStatus(epicId);
        }
    }

    @Override
    public void removeEpic(int id) {
        if (epics.containsKey(id)) {
            for (int subTaskId : epics.get(id).getSubTaskOfEpicIDs()) {
                subTasks.remove(subTaskId);
            }
            epics.remove(id);
        }
    }

    //Получение списка всех подзадач определённого эпика
    @Override
    public List<SubTask> getSubTasksOfEpic(Epic epic) {
        List<SubTask> subTasksOfEpic = new ArrayList<>();

        for(int id : epic.getSubTaskOfEpicIDs()) {
            subTasksOfEpic.add(subTasks.get(id));
        }

        return subTasksOfEpic;
    }

    @Override
    public void updateEpicStatus(int epicId) {
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
