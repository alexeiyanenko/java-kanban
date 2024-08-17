package manager.httpserver;

import com.sun.net.httpserver.HttpExchange;
import com.google.gson.Gson;
import manager.exceptions.NotFoundException;
import manager.TaskManager;
import tasks.Subtask;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class SubtaskHandler extends BaseHttpHandler {
    private final TaskManager manager;
    private final Gson gson = HttpTaskServer.getGson();

    public SubtaskHandler(TaskManager manager) {
        this.manager = manager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();

        try {
            switch (method) {
                case "GET":
                    if (path.equals("/subtasks")) {
                        sendSubtasks(exchange);
                    } else if (path.matches("/subtasks/\\d+")) {
                        sendSubtaskById(exchange, extractIdFromPath(path));
                    } else {
                        sendNotFound(exchange);
                    }
                    break;
                case "POST":
                    handlePost(exchange);
                    break;
                case "DELETE":
                    if (path.matches("/subtasks/\\d+")) {
                        removeSubtaskById(exchange, extractIdFromPath(path));
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

    private void sendSubtasks(HttpExchange exchange) throws IOException {
        List<Subtask> subtasks = manager.getSubtasks();
        String response = gson.toJson(subtasks);
        sendText(exchange, response);
    }

    private void sendSubtaskById(HttpExchange exchange, int id) throws IOException {
        Subtask subtask = manager.getSubtaskById(id);
        if (subtask == null) {
            throw new NotFoundException("Subtask not found with ID: " + id);
        }
        String response = gson.toJson(subtask);
        sendText(exchange, response);
    }

    private void handlePost(HttpExchange exchange) throws IOException {
        InputStreamReader reader = new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8);
        Subtask subtask = gson.fromJson(reader, Subtask.class);

        // Проверка существования Epic
        if (manager.getEpicById(subtask.getEpicId()) == null) {
            exchange.sendResponseHeaders(400, -1);
            exchange.close();
            return;
        }

        // Проверка на пересечение по времени
        if (manager.isTimeOverlap(subtask)) {
            sendHasInteractions(exchange);
            return;
        }

        if (manager.getSubtaskById(subtask.getId()) != null) {
            manager.updateSubtask(subtask);
        } else {
            manager.addSubtask(subtask);
        }

        exchange.sendResponseHeaders(201, -1);
        exchange.close();
    }

    private void removeSubtaskById(HttpExchange exchange, int id) throws IOException {
        if (manager.getSubtaskById(id) == null) {
            throw new NotFoundException("Subtask not found with ID: " + id);
        }
        manager.removeSubtask(id);
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