package manager.httpserver;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpServer;
import manager.HistoryManager;
import manager.Managers;
import manager.TaskManager;
import manager.json.DurationAdapter;
import manager.json.LocalDateTimeAdapter;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.time.Duration;
import java.time.LocalDateTime;

public class HttpTaskServer {
    private static final int PORT = 8080;
    private final HttpServer server;
    private final TaskManager manager;
    private final HistoryManager historyManager;

    public HttpTaskServer() throws IOException {
        this.server = HttpServer.create(new InetSocketAddress(PORT), 0);
        this.historyManager = Managers.getDefaultHistory();
        this.manager = Managers.getDefault(historyManager);
        createContext();
    }

    public HttpTaskServer(TaskManager taskManager, HistoryManager historyManager) throws IOException {
        this.server = HttpServer.create(new InetSocketAddress(PORT), 0);
        this.manager = taskManager;
        this.historyManager = historyManager;
        createContext();
    }

    private void createContext() {
        server.createContext("/tasks", new TaskHandler(manager));
        server.createContext("/subtasks", new SubtaskHandler(manager));
        server.createContext("/epics", new EpicHandler(manager));
        server.createContext("/history", new HistoryHandler(historyManager));
        server.createContext("/prioritized", new PrioritizedTaskHandler(manager));
    }

    public void start() {
        server.start();
        System.out.println("Server started on port " + PORT);
    }

    public void stop() {
        server.stop(0);
        System.out.println("Server stopped.");
    }

    public static Gson getGson() {
        return new GsonBuilder()
                .registerTypeAdapter(Duration.class, new DurationAdapter())
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .create();
    }

    public static void main(String[] args) throws IOException {
        HttpTaskServer server = new HttpTaskServer();
        server.start();
    }
}

