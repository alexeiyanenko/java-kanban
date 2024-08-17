package manager.httpserver;

import com.sun.net.httpserver.HttpExchange;
import com.google.gson.Gson;
import manager.TaskManager;
import tasks.Task;
import java.io.IOException;
import java.util.List;

public class PrioritizedTaskHandler extends BaseHttpHandler {
    private final TaskManager manager;
    private final Gson gson = HttpTaskServer.getGson();

    public PrioritizedTaskHandler(TaskManager manager) {
        this.manager = manager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();

        try {
            if (method.equals("GET") && path.equals("/prioritized")) {
                sendPrioritizedTasks(exchange);
            } else {
                sendNotFound(exchange);
            }
        } catch (Exception e) {
            e.printStackTrace();
            sendInternalError(exchange);
        }
    }

    private void sendPrioritizedTasks(HttpExchange exchange) throws IOException {
        List<Task> prioritizedTasks = manager.getPrioritizedTasks();
        String response = gson.toJson(prioritizedTasks);
        sendText(exchange, response);
    }

    private void sendInternalError(HttpExchange exchange) throws IOException {
        exchange.sendResponseHeaders(500, -1);
        exchange.close();
    }
}

