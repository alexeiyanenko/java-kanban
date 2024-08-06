package manager;

import tasks.Epic;
import tasks.Subtask;
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
    private final Map<Integer, Subtask> subtasks;
    private final TreeSet<Task> prioritizedTasks = new TreeSet<>(Comparator.comparing(Task::getStartTime));
    private final HistoryManager historyManager;
    private static int generatorId = 0;

    public InMemoryTaskManager() {
        tasks = new HashMap<>();
        epics = new HashMap<>();
        subtasks = new HashMap<>();
        historyManager = Managers.getDefaultHistory();
    }

    public InMemoryTaskManager(HistoryManager historyManager) {
        tasks = new HashMap<>();
        epics = new HashMap<>();
        subtasks = new HashMap<>();
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
    public void addSubtask(Subtask subtask) {
        if (isTimeOverlap(subtask)) {
            throw new IllegalArgumentException("Subtask time overlaps with an existing task");
        }

        while (subtask.getId() == 0) {
            final int id = ++generatorId;
            subtask.setId(id);

            if (tasks.containsKey(subtask.getId())) {
                subtask.setId(0);
            }
        }
        int id = subtask.getId();
        subtasks.put(subtask.getId(), subtask);
        if (subtask.getStartTime() != null) {
            prioritizedTasks.add(subtask);
        }

        int epicId = subtask.getEpicId();
        epics.get(epicId).getSubtaskOfEpicIDs().add(id);

        updateEpicStatus(subtask.getEpicId());
        updateEpicTime(subtask.getEpicId());
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
    public void updateSubtask(Subtask subtask) {
        final Task savedSubtask = subtasks.get(subtask.getId());
        if (savedSubtask == null) {
            return;
        }
        subtasks.put(subtask.getId(), subtask);
        updateEpicStatus(subtask.getEpicId());
        prioritizedTasks.remove(savedSubtask);
        if (subtask.getStartTime() != null) {
            prioritizedTasks.add(subtask);
        }

    }

    //Проверка на пересечение времени
    public boolean isTimeOverlap(Task newTask) {
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
        allTasks.addAll(subtasks.values());
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
    public List<Subtask> getSubtasks() {
        return new ArrayList<>(subtasks.values());
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
        subtasks.clear();
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
    public void removeAllSubtasks() {
        subtasks.values().stream()
                .forEach(subtask -> {
                    historyManager.remove(subtask.getId());
                    prioritizedTasks.remove(subtask);
                });
        subtasks.clear();

        epics.values().stream()
                .forEach(epic -> {
                    epic.getSubtaskOfEpicIDs().clear();
                    updateEpicStatus(epic.getId());
                    updateEpicTime(epic.getId());
                });
    }

    @Override
    public void removeAllEpics() {
        epics.values().stream()
                .forEach(epic -> historyManager.remove(epic.getId()));
        epics.clear();

        subtasks.values().stream()
                .forEach(subtask -> {
                    historyManager.remove(subtask.getId());
                    prioritizedTasks.remove(subtask);
                });
        subtasks.clear();
    }

    //Получение по идентификатору
    @Override
    public Task getTaskById(int taskId) {
        Task task = tasks.get(taskId);
        historyManager.add(task);
        return task;
    }

    @Override
    public Subtask getSubtaskById(int subtaskId) {
        Subtask subtask = subtasks.get(subtaskId);
        historyManager.add(subtask);
        return subtask;
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
    public void removeSubtask(int id) {
        if (subtasks.containsKey(id)) {
            int epicId = subtasks.get(id).getEpicId();
            subtasks.remove(id);
            historyManager.remove(id);
            prioritizedTasks.remove(getSubtaskById(id));

            epics.get(epicId).getSubtaskOfEpicIDs().remove((Integer)id);
            updateEpicStatus(epicId);
            updateEpicTime(epicId);
        }
    }

    @Override
    public void removeEpic(int id) {
        if (epics.containsKey(id)) {
            epics.get(id).getSubtaskOfEpicIDs().stream()
                    .forEach(subtaskId -> {
                        subtasks.remove(subtaskId);
                        historyManager.remove(subtaskId);
                    });

            epics.remove(id);
            historyManager.remove(id);
        }
    }

    //Получение списка всех подзадач определённого эпика
    @Override
    public List<Subtask> getSubtasksOfEpic(Epic epic) {
        List<Subtask> subtasksOfEpic = new ArrayList<>();
        epic.getSubtaskOfEpicIDs().stream()
                .map(subtasks::get)
                .forEach(subtasksOfEpic::add);
        return subtasksOfEpic;
    }

    @Override
    public void updateEpicStatus(int epicId) {
        Epic epic = epics.get(epicId);
        ArrayList<Integer> subtaskIDs = epic.getSubtaskOfEpicIDs();
        if (subtaskIDs.isEmpty()) {
            epic.setStatus(TaskStatus.NEW);
            return;
        }
        TaskStatus status = null;
        for (int id : subtaskIDs) {
            final Subtask subtask = subtasks.get(id);
            if (status == null) {
                status = subtask.getStatus();
                continue;
            }
            if (status.equals(subtask.getStatus())
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

        for (Subtask subtask : getSubtasksOfEpic(epic)) {
            totalDuration = totalDuration.plus(subtask.getDuration());
            if (startTime == null || subtask.getStartTime().isBefore(startTime)) {
                startTime = subtask.getStartTime();
            }
            if (endTime == null || subtask.getEndTime().isAfter(endTime)) {
                endTime = subtask.getEndTime();
            }
        }

        epic.setDuration(totalDuration);
        epic.setStartTime(startTime);
        epic.setEndTime(endTime);
    }
}
