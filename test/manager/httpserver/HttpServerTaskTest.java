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
import java.util.List;
import tasks.Task;
import tasks.TaskStatus;

import static org.junit.jupiter.api.Assertions.*;

public class HttpServerTaskTest {

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
    public void testAddTask() throws IOException, InterruptedException {
        Task task = new Task(1, "Task", "Description", TaskStatus.NEW, 5);
        String taskJson = gson.toJson(task);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());

        List<Task> tasksFromManager = manager.getTasks();
        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
        assertEquals("Task", tasksFromManager.get(0).getName(), "Некорректное имя задачи");
    }

    @Test
    public void testGetTasks() throws IOException, InterruptedException {
        Task task = new Task(1, "Task", "Description", TaskStatus.NEW, 5);
        manager.addTask(task);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        Task[] tasks = gson.fromJson(response.body(), Task[].class);
        assertEquals(1, tasks.length, "Некорректное количество задач");
        assertEquals("Task", tasks[0].getName(), "Некорректное имя задачи");
    }

    @Test
    public void testUpdateTask() throws IOException, InterruptedException {
        Task task = new Task(1, "Task", "Description", TaskStatus.NEW, 5);
        manager.addTask(task);

        Task updatedTask = new Task(1, "Updated Task", "Updated Description", TaskStatus.IN_PROGRESS, 10);
        String updatedTaskJson = gson.toJson(updatedTask);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(updatedTaskJson)).build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());

        Task taskFromManager = manager.getTaskById(1);
        assertNotNull(taskFromManager, "Задача не возвращается");
        assertEquals("Updated Task", taskFromManager.getName(), "Имя задачи не обновлено");
        assertEquals("Updated Description", taskFromManager.getDescription(), "Описание задачи не обновлено");
        assertEquals(TaskStatus.IN_PROGRESS, taskFromManager.getStatus(), "Статус задачи не обновлен");
    }

    @Test
    public void testDeleteTask() throws IOException, InterruptedException {
        Task task = new Task(1, "Task", "Description", TaskStatus.NEW, 5);
        manager.addTask(task);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/1");
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        List<Task> tasksFromManager = manager.getTasks();
        assertEquals(0, tasksFromManager.size(), "Задача не удалена");
    }

    @Test
    public void testGetTaskById() throws IOException, InterruptedException {
        Task task = new Task(1, "Task", "Description", TaskStatus.NEW, 5);
        manager.addTask(task);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/1");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        Task taskFromResponse = gson.fromJson(response.body(), Task.class);
        assertEquals(1, taskFromResponse.getId(), "Некорректный ID задачи");
        assertEquals("Task", taskFromResponse.getName(), "Некорректное имя задачи");
    }

    @Test
    public void testAddTaskWithTimeConflict() throws IOException, InterruptedException {
        Task task1 = new Task(1, "Task 1", "Description 1", TaskStatus.NEW, 60);
        task1.setStartTime(2024, 8, 18, 10, 0);
        manager.addTask(task1);

        Task task2 = new Task(2, "Task 2", "Description 2", TaskStatus.NEW, 60);
        task2.setStartTime(2024, 8, 18, 10, 30);
        String taskJson = gson.toJson(task2);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(406, response.statusCode(), "Ожидаемый код ошибки 406 не вернулся при добавлении задачи с конфликтом по времени");
    }

    @Test
    public void testDeleteNonexistentTask() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/999");
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, response.statusCode(), "Ожидаемый код ошибки не вернулся при удалении несуществующей задачи");
    }

    @Test
    public void testGetNonexistentTaskById() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/999");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, response.statusCode(), "Ожидаемый код ошибки не вернулся при запросе несуществующей задачи");
    }
}
