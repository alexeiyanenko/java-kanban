package manager;

import tasks.*;
import java.io.*;
import java.nio.file.Files;
import java.util.List;
import java.time.Duration;
import java.time.LocalDateTime;

public class FileBackedTaskManager extends InMemoryTaskManager {
    private final File file;
    private static final String TITLE = "id, type, name, status, description, duration, start time, epic\n";

    public FileBackedTaskManager(File file) {
        this.file = file;
    }

    @Override
    public void addTask(Task task) {
        super.addTask(task);
        save();
    }

    @Override
    public void addEpic(Epic epic) {
        super.addEpic(epic);
        save();
    }

    @Override
    public void addSubTask(SubTask subtask) {
        super.addSubTask(subtask);
        save();
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }

    @Override
    public void updateSubTask(SubTask subtask) {
        super.updateSubTask(subtask);
        save();
    }

    @Override
    public void removeTask(int id) {
        super.removeTask(id);
        save();
    }

    @Override
    public void removeEpic(int id) {
        super.removeEpic(id);
        save();
    }

    @Override
    public void removeSubTask(int id) {
        super.removeSubTask(id);
        save();
    }

    void save() {
        try (BufferedWriter writer = Files.newBufferedWriter(file.toPath())) {
            writer.write(TITLE);

            // Если этот цикл преобразовать со StreamAPI,
            // то идея требует взять каждый отдельный writer.write() в try-catch,
            // так код выходит более длинным и менее читаемым, чем есть.
            for (Task task : getAllTasks()) {
                writer.write(toString(task) + "\n");
            }
            for (Epic epic : getEpics()) {
                writer.write(toString(epic) + "\n");
                for (SubTask subtask : getSubTasksOfEpic(epic)) {
                    writer.write(toString(subtask) + "\n");
                }
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Error saving tasks", e);
        }
    }

    private String toString(Task task) {
        String duration = task.getDuration() != null ? String.valueOf(task.getDuration().toMinutes()) : "null";
        String startTime = task.getStartTime() != null ? task.getStartTime().toString() : "null";
        if (task.getType() == TaskType.SUBTASK) {
            SubTask subtask = (SubTask) task;
            return String.format("%d,SUBTASK,%s,%s,%s,%s,%s,%d",
                    subtask.getId(), subtask.getName(), subtask.getStatus(),
                    subtask.getDescription(), duration, startTime, subtask.getEpicId());
        } else if (task.getType() == TaskType.EPIC) {
            Epic epic = (Epic) task;
            return String.format("%d,EPIC,%s,%s,%s,%s,%s",
                    epic.getId(), epic.getName(), epic.getStatus(), epic.getDescription(),
                    duration, startTime);
        } else {
            return String.format("%d,TASK,%s,%s,%s,%s,%s",
                    task.getId(), task.getName(), task.getStatus(), task.getDescription(),
                    duration, startTime);
        }
    }

    public static FileBackedTaskManager loadFromFile(File file) {
        FileBackedTaskManager manager = new FileBackedTaskManager(file);
        try {
            List<String> lines = Files.readAllLines(file.toPath());
            /*for (String line : lines.subList(1, lines.size())) { // Skip header
                Task task = fromString(line);
                if (task.getType() == TaskType.EPIC) {
                    manager.addEpic((Epic) task);
                } else if (task.getType() == TaskType.SUBTASK) {
                    manager.addSubTask((SubTask) task);
                } else {
                    manager.addTask(task);
                }
            }*/
            lines.subList(1, lines.size()).stream()
                    .map(FileBackedTaskManager::fromString)
                    .forEach(task -> {
                        if (task.getType() == TaskType.EPIC) {
                            manager.addEpic((Epic) task);
                        } else if (task.getType() == TaskType.SUBTASK) {
                            manager.addSubTask((SubTask) task);
                        } else {
                            manager.addTask(task);
                        }
                    });

        } catch (IOException e) {
            throw new ManagerSaveException("Error loading tasks", e);
        }
        return manager;
    }

    private static Task fromString(String value) {
        String[] fields = value.split(",");
        int id = Integer.parseInt(fields[0]);
        TaskType type = TaskType.valueOf(fields[1].toUpperCase());
        String name = fields[2];
        TaskStatus status = TaskStatus.valueOf(fields[3]);
        String description = fields[4];
        Duration duration = fields[5].equals("null") ? Duration.ZERO : Duration.ofMinutes(Long.parseLong(fields[5]));
        LocalDateTime startTime = fields[6].equals("null") ? null : LocalDateTime.parse(fields[6]);

        switch (type) {
            case TASK:
                Task task = new Task(id, name, description, status);
                task.setDuration(duration);
                task.setStartTime(startTime);
                return task;
            case EPIC:
                Epic epic = new Epic(id, name, description, status);
                epic.setDuration(duration);
                epic.setStartTime(startTime);
                return epic;
            case SUBTASK:
                int epicId = Integer.parseInt(fields[7]);
                SubTask subTask = new SubTask(epicId, id, name, description, status);
                subTask.setDuration(duration);
                subTask.setStartTime(startTime);
                return subTask;
            default:
                throw new IllegalArgumentException("Unknown task type: " + type);
        }
    }
}

