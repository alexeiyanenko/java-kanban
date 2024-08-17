package manager.httpserver;

import com.sun.net.httpserver.HttpExchange;
import com.google.gson.Gson;
import manager.exceptions.NotFoundException;
import manager.TaskManager;
import tasks.Epic;
import tasks.Subtask;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class EpicHandler extends BaseHttpHandler {
    private final TaskManager manager;
    private final Gson gson = HttpTaskServer.getGson();

    public EpicHandler(TaskManager manager) {
        this.manager = manager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();

        try {
            switch (method) {
                case "GET":
                    if (path.equals("/epics")) {
                        sendEpics(exchange);
                    } else if (path.matches("/epics/\\d+")) {
                        sendEpicById(exchange, extractIdFromPath(path));
                    } else if (path.matches("/epics/\\d+/subtasks")) {
                        sendEpicSubtasks(exchange, extractIdFromPath(path));
                    } else {
                        sendNotFound(exchange);
                    }
                    break;
                case "POST":
                    handlePost(exchange);
                    break;
                case "DELETE":
                    if (path.matches("/epics/\\d+")) {
                        removeEpicById(exchange, extractIdFromPath(path));
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

    private void sendEpics(HttpExchange exchange) throws IOException {
        List<Epic> epics = manager.getEpics();
        String response = gson.toJson(epics);
        sendText(exchange, response);
    }

    private void sendEpicById(HttpExchange exchange, int id) throws IOException {
        Epic epic = manager.getEpicById(id);
        if (epic == null) {
            throw new NotFoundException("Epic not found with ID: " + id);
        }
        String response = gson.toJson(epic);
        sendText(exchange, response);
    }

    private void sendEpicSubtasks(HttpExchange exchange, int epicId) throws IOException {
        if (manager.getEpicById(epicId) == null) {
            throw new NotFoundException("Epic not found with ID: " + epicId);
        }
        List<Subtask> subtasks = manager.getSubtasksOfEpic(epicId);
        if (subtasks.isEmpty()) {
            throw new NotFoundException("Epic with ID " + epicId + "doesn't have subtasks");
        }
        String response = gson.toJson(subtasks);
        sendText(exchange, response);
    }

    private void handlePost(HttpExchange exchange) throws IOException {
        InputStreamReader reader = new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8);
        Epic epic = gson.fromJson(reader, Epic.class);

        if (manager.getEpicById(epic.getId()) != null) {
            manager.updateEpic(epic);
        } else {
            manager.addEpic(epic);
        }

        exchange.sendResponseHeaders(201, -1);
        exchange.close();
    }

    private void removeEpicById(HttpExchange exchange, int id) throws IOException {
        if (manager.getEpicById(id) == null) {
            throw new NotFoundException("Epic not found with ID: " + id);
        }
        manager.removeEpic(id);
        exchange.sendResponseHeaders(200, -1);
        exchange.close();
    }

    private int extractIdFromPath(String path) {
        String[] segments = path.split("/");
        if (segments.length > 1 && segments[segments.length - 1].equals("subtasks")) {
            return Integer.parseInt(segments[segments.length - 2]);
        } else {
            return Integer.parseInt(segments[segments.length - 1]);
        }
    }

    private void sendInternalError(HttpExchange exchange) throws IOException {
        exchange.sendResponseHeaders(500, -1);
        exchange.close();
    }
}

