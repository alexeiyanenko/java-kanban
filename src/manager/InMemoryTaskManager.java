package manager;

import tasks.Epic;
import tasks.SubTask;
import tasks.Task;
import tasks.TaskStatus;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import java.util.Comparator;

public class InMemoryTaskManager implements TaskManager {
    private final Map<Integer, Task> tasks;
    private final Map<Integer, Epic> epics;
    private final Map<Integer, SubTask> subTasks;
    private final TreeSet<Task> prioritizedTasks = new TreeSet<>(Comparator.comparing(Task::getStartTime));
    private final HistoryManager historyManager;
    private static int generatorId = 0;

    public InMemoryTaskManager() {
        tasks = new HashMap<>();
        epics = new HashMap<>();
        subTasks = new HashMap<>();
        historyManager = Managers.getDefaultHistory();
    }

    public InMemoryTaskManager(HistoryManager historyManager) {
        tasks = new HashMap<>();
        epics = new HashMap<>();
        subTasks = new HashMap<>();
        this.historyManager = historyManager;
    }

    //Добавление новых задач
    @Override
    public void addTask(Task task) {
        if (isTimeOverlap(task)) {
            throw new IllegalArgumentException("Task time overlaps with an existing task");
        }

        while (task.getId() == 0) {
            final int id = ++generatorId;
            task.setId(id);

            if (tasks.containsKey(task.getId())) {
                task.setId(0);
            }
        }

        tasks.put(task.getId(), task);
        if (task.getStartTime() != null) {
            prioritizedTasks.add(task);
        }
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
        if (isTimeOverlap(subTask)) {
            throw new IllegalArgumentException("SubTask time overlaps with an existing task");
        }

        while (subTask.getId() == 0) {
            final int id = ++generatorId;
            subTask.setId(id);

            if (tasks.containsKey(subTask.getId())) {
                subTask.setId(0);
            }
        }
        int id = subTask.getId();
        subTasks.put(subTask.getId(), subTask);
        if (subTask.getStartTime() != null) {
            prioritizedTasks.add(subTask);
        }

        int epicId = subTask.getEpicId();
        epics.get(epicId).getSubTaskOfEpicIDs().add(id);

        updateEpicStatus(subTask.getEpicId());
        updateEpicTime(subTask.getEpicId());
    }

    //Обновление существующих задач
    @Override
    public void updateTask(Task task) {
        final Task savedTask = tasks.get(task.getId());
        if (savedTask == null) {
            return;
        }
        tasks.put(task.getId(), task);
        prioritizedTasks.remove(savedTask);
        if (task.getStartTime() != null) {
            prioritizedTasks.add(task);
        }
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
        prioritizedTasks.remove(savedSubTask);
        if (subTask.getStartTime() != null) {
            prioritizedTasks.add(subTask);
        }

    }

    //Проверка на пересечение времени
    private boolean isTimeOverlap(Task newTask) {
        return prioritizedTasks.stream()
                .anyMatch(task -> {
                    if (task.getId() == newTask.getId()) {
                        return false;
                    }
                    if (task.getStartTime() == null || newTask.getStartTime() == null) {
                        return false;
                    }
                    return task.getStartTime().isBefore(newTask.getEndTime()) &&
                            newTask.getStartTime().isBefore(task.getEndTime());
                });
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

    public List<Task> getPrioritizedTasks() {
        return new ArrayList<>(prioritizedTasks);
    }

    //Удаление задач
    @Override
    public void removeAll() {
        historyManager.clearHistory();
        prioritizedTasks.clear();
        tasks.clear();
        subTasks.clear();
        epics.clear();
    }

    @Override
    public void removeAllTasks() {
        tasks.values().stream()
                .forEach(task -> {
                    historyManager.remove(task.getId());
                    prioritizedTasks.remove(task);
                });
        tasks.clear();
    }

    @Override
    public void removeAllSubTasks() {
        subTasks.values().stream()
                .forEach(subtask -> {
                    historyManager.remove(subtask.getId());
                    prioritizedTasks.remove(subtask);
                });
        subTasks.clear();

        epics.values().stream()
                .forEach(epic -> {
                    epic.getSubTaskOfEpicIDs().clear();
                    updateEpicStatus(epic.getId());
                    updateEpicTime(epic.getId());
                });
    }

    @Override
    public void removeAllEpics() {
        epics.values().stream()
                .forEach(epic -> historyManager.remove(epic.getId()));
        epics.clear();

        subTasks.values().stream()
                .forEach(subtask -> {
                    historyManager.remove(subtask.getId());
                    prioritizedTasks.remove(subtask);
                });
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

    //Удаление по идентификатору
    @Override
    public void removeTask(int id) {
        if (tasks.containsKey(id)) {
            tasks.remove(id);
            historyManager.remove(id);
            prioritizedTasks.remove(getTaskById(id));
        }
    }

    @Override
    public void removeSubTask(int id) {
        if (subTasks.containsKey(id)) {
            int epicId = subTasks.get(id).getEpicId();
            subTasks.remove(id);
            historyManager.remove(id);
            prioritizedTasks.remove(getSubTaskById(id));

            epics.get(epicId).getSubTaskOfEpicIDs().remove((Integer)id);
            updateEpicStatus(epicId);
            updateEpicTime(epicId);
        }
    }

    @Override
    public void removeEpic(int id) {
        if (epics.containsKey(id)) {
            epics.get(id).getSubTaskOfEpicIDs().stream()
                    .forEach(subTaskId -> {
                        subTasks.remove(subTaskId);
                        historyManager.remove(subTaskId);
                    });

            epics.remove(id);
            historyManager.remove(id);
        }
    }

    //Получение списка всех подзадач определённого эпика
    @Override
    public List<SubTask> getSubTasksOfEpic(Epic epic) {
        List<SubTask> subTasksOfEpic = new ArrayList<>();
        epic.getSubTaskOfEpicIDs().stream()
                .map(subTasks::get)
                .forEach(subTasksOfEpic::add);
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

    private void updateEpicTime(int epicId) {
        Epic epic = epics.get(epicId);
        Duration totalDuration = Duration.ZERO;
        LocalDateTime startTime = null;
        LocalDateTime endTime = null;

        for (SubTask subTask : getSubTasksOfEpic(epic)) {
            totalDuration = totalDuration.plus(subTask.getDuration());
            if (startTime == null || subTask.getStartTime().isBefore(startTime)) {
                startTime = subTask.getStartTime();
            }
            if (endTime == null || subTask.getEndTime().isAfter(endTime)) {
                endTime = subTask.getEndTime();
            }
        }

        epic.setDuration(totalDuration);
        epic.setStartTime(startTime);
        epic.setEndTime(endTime);
    }
}
