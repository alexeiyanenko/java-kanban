package manager.httpserver;

import manager.Managers;
import manager.TaskManager;
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

public class HttpServerPrioritizedTaskTest {

    private TaskManager manager;
    private HttpTaskServer server;
    private final Gson gson = HttpTaskServer.getGson();

    @BeforeEach
    public void setUp() throws IOException {
        manager = Managers.getDefault();
        server = new HttpTaskServer(manager, null);
        server.start();
    }

    @AfterEach
    public void tearDown() {
        server.stop();
    }

    @Test
    public void testGetPrioritizedTasks() throws IOException, InterruptedException {
        Task task1 = new Task(1, "Task 1", "Description 1", TaskStatus.NEW, 60);
        task1.setStartTime(2024, 8, 18, 9, 0);
        manager.addTask(task1);

        Task task2 = new Task(2, "Task 2", "Description 2", TaskStatus.IN_PROGRESS, 60);
        task2.setStartTime(2024, 8, 18, 8, 0);
        manager.addTask(task2);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/prioritized");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(), "Не удалось получить приоритетные задачи");

        Task[] prioritizedTasks = gson.fromJson(response.body(), Task[].class);
        assertEquals(2, prioritizedTasks.length, "Некорректное количество задач в приоритетном списке");
        assertEquals("Task 2", prioritizedTasks[0].getName(), "Задачи отсортированы неверно");
        assertEquals("Task 1", prioritizedTasks[1].getName(), "Задачи отсортированы неверно");
    }

    @Test
    public void testGetPrioritizedTasksWhenEmpty() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/prioritized");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(), "Не удалось получить приоритетные задачи");

        Task[] prioritizedTasks = gson.fromJson(response.body(), Task[].class);
        assertEquals(0, prioritizedTasks.length, "Ожидался пустой список приоритетных задач");
    }
}
