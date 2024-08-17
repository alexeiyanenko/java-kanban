package manager.httpserver;

import manager.HistoryManager;
import manager.Managers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.io.IOException;
import com.google.gson.Gson;
import tasks.Task;
import tasks.TaskStatus;

import static org.junit.jupiter.api.Assertions.*;

public class HttpServerHistoryTest {

    private HistoryManager historyManager;
    private HttpTaskServer server;
    private final Gson gson = HttpTaskServer.getGson();

    @BeforeEach
    public void setUp() throws IOException {
        historyManager = Managers.getDefaultHistory();
        server = new HttpTaskServer(null, historyManager);
        server.start();
    }

    @AfterEach
    public void tearDown() {
        server.stop();
    }

    @Test
    public void testGetHistory() throws IOException, InterruptedException {
        Task task1 = new Task(1, "Task 1", "Description 1", TaskStatus.NEW, 60);
        Task task2 = new Task(2, "Task 2", "Description 2", TaskStatus.IN_PROGRESS, 60);

        historyManager.add(task1);
        historyManager.add(task2);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/history");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(), "Не удалось получить историю задач");

        Task[] historyTasks = gson.fromJson(response.body(), Task[].class);
        assertEquals(2, historyTasks.length, "Некорректное количество задач в истории");
        assertEquals("Task 1", historyTasks[0].getName(), "Первая задача в истории не совпадает");
        assertEquals("Task 2", historyTasks[1].getName(), "Вторая задача в истории не совпадает");
    }

    @Test
    public void testGetEmptyHistory() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/history");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(), "Не удалось получить историю задач");

        Task[] historyTasks = gson.fromJson(response.body(), Task[].class);
        assertEquals(0, historyTasks.length, "Ожидался пустой список задач в истории");
    }
}
