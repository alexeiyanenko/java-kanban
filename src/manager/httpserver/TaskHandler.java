package manager.httpserver;

import com.sun.net.httpserver.HttpExchange;
import com.google.gson.Gson;
import manager.exceptions.NotFoundException;
import manager.TaskManager;
import tasks.Task;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class TaskHandler extends BaseHttpHandler {
    private final TaskManager manager;
    private final Gson gson = HttpTaskServer.getGson();

    public TaskHandler(TaskManager manager) {
        this.manager = manager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();

        try {
            switch (method) {
                case "GET":
                    if (path.equals("/tasks")) {
                        sendTasks(exchange);
                    } else if (path.matches("/tasks/\\d+")) {
                        sendTaskById(exchange, extractIdFromPath(path));
                    } else {
                        sendNotFound(exchange);
                    }
                    break;
                case "POST":
                    handlePost(exchange);
                    break;
                case "DELETE":
                    if (path.matches("/tasks/\\d+")) {
                        removeTaskById(exchange, extractIdFromPath(path));
                    } else {
                        sendNotFound(exchange);
                    }
                    break;
                default:
                    sendNotFound(exchange);
            }
        } catch (NotFoundException e) {
            sendNotFound(exchange);
        } catch (Exception e) {
            e.printStackTrace();
            sendInternalError(exchange);
        }
    }

    private void sendTasks(HttpExchange exchange) throws IOException {
        List<Task> tasks = manager.getTasks();
        String response = gson.toJson(tasks);
        sendText(exchange, response);
    }

    private void sendTaskById(HttpExchange exchange, int id) throws IOException {
        Task task = manager.getTaskById(id);
        if (task == null) {
            throw new NotFoundException("Subtask not found with ID: " + id);
        }
        String response = gson.toJson(task);
        sendText(exchange, response);
    }

    private void handlePost(HttpExchange exchange) throws IOException {
        InputStreamReader reader = new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8);
        Task task = gson.fromJson(reader, Task.class);

        // Проверка на пересечение по времени
        if (manager.isTimeOverlap(task)) {
            sendHasInteractions(exchange);
            return;
        }

        if (manager.getTaskById(task.getId()) != null) {
            manager.updateTask(task);
        } else {
            manager.addTask(task);
        }

        exchange.sendResponseHeaders(201, -1);
        exchange.close();
    }

    private void removeTaskById(HttpExchange exchange, int id) throws IOException {
        if (manager.getTaskById(id) == null) {
            throw new NotFoundException("Subtask not found with ID: " + id);
        }
        manager.removeTask(id);
        exchange.sendResponseHeaders(200, -1);
        exchange.close();
    }

    private int extractIdFromPath(String path) {
        String[] segments = path.split("/");
        return Integer.parseInt(segments[segments.length - 1]);
    }

    private void sendInternalError(HttpExchange exchange) throws IOException {
        exchange.sendResponseHeaders(500, -1);
        exchange.close();
    }
}
