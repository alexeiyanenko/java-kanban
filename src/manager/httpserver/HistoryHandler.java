package manager.httpserver;

import com.sun.net.httpserver.HttpExchange;
import com.google.gson.Gson;
import manager.HistoryManager;
import tasks.Task;

import java.io.IOException;
import java.util.List;

public class HistoryHandler extends BaseHttpHandler {
    private final HistoryManager historyManager;

    private final Gson gson = HttpTaskServer.getGson();

    public HistoryHandler(HistoryManager historyManager) {
        this.historyManager = historyManager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();

        try {
            if (method.equals("GET") && path.equals("/history")) {
                sendHistory(exchange);
            } else {
                sendNotFound(exchange);
            }
        } catch (Exception e) {
            e.printStackTrace();
            sendInternalError(exchange);
        }
    }

    private void sendHistory(HttpExchange exchange) throws IOException {
        List<Task> history = historyManager.getHistory();
        String response = gson.toJson(history);
        sendText(exchange, response);
    }

    private void sendInternalError(HttpExchange exchange) throws IOException {
        exchange.sendResponseHeaders(500, -1);
        exchange.close();
    }
}

