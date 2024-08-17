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
import tasks.Epic;
import tasks.Subtask;
import tasks.TaskStatus;

import static org.junit.jupiter.api.Assertions.*;

public class HttpServerSubtaskTest {

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
    public void testAddSubtask() throws IOException, InterruptedException {
        Epic epic = new Epic(1, "Epic", "Epic Description", TaskStatus.NEW);
        manager.addEpic(epic);

        Subtask subtask = new Subtask(1, 2, "Subtask", "Description", TaskStatus.NEW, 5);
        String subtaskJson = gson.toJson(subtask);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(subtaskJson)).build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());

        List<Subtask> subtasksFromManager = manager.getSubtasks();
        assertNotNull(subtasksFromManager, "Подзадачи не возвращаются");
        assertEquals(1, subtasksFromManager.size(), "Некорректное количество подзадач");
        assertEquals("Subtask", subtasksFromManager.get(0).getName(), "Некорректное имя подзадачи");
    }

    @Test
    public void testGetSubtasks() throws IOException, InterruptedException {
        Epic epic = new Epic(1, "Epic", "Epic Description", TaskStatus.NEW);
        manager.addEpic(epic);

        Subtask subtask = new Subtask(1, 2, "Subtask", "Description", TaskStatus.NEW, 5);
        manager.addSubtask(subtask);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        Subtask[] subtasks = gson.fromJson(response.body(), Subtask[].class);
        assertEquals(1, subtasks.length, "Некорректное количество подзадач");
        assertEquals("Subtask", subtasks[0].getName(), "Некорректное имя подзадачи");
    }

    @Test
    public void testUpdateSubtask() throws IOException, InterruptedException {
        Epic epic = new Epic(1, "Epic", "Epic Description", TaskStatus.NEW);
        manager.addEpic(epic);

        Subtask subtask = new Subtask(1, 2, "Subtask", "Description", TaskStatus.NEW, 5);
        manager.addSubtask(subtask);

        Subtask updatedSubtask = new Subtask(1, 2, "Updated Subtask", "Updated Description", TaskStatus.IN_PROGRESS, 10);
        String updatedSubtaskJson = gson.toJson(updatedSubtask);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(updatedSubtaskJson)).build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());

        Subtask subtaskFromManager = manager.getSubtaskById(2);
        assertNotNull(subtaskFromManager, "Подзадача не возвращается");
        assertEquals("Updated Subtask", subtaskFromManager.getName(), "Имя подзадачи не обновлено");
        assertEquals("Updated Description", subtaskFromManager.getDescription(), "Описание подзадачи не обновлено");
        assertEquals(TaskStatus.IN_PROGRESS, subtaskFromManager.getStatus(), "Статус подзадачи не обновлен");
    }

    @Test
    public void testDeleteSubtask() throws IOException, InterruptedException {
        Epic epic = new Epic(1, "Epic", "Epic Description", TaskStatus.NEW);
        manager.addEpic(epic);

        Subtask subtask = new Subtask(1, 2, "Subtask", "Description", TaskStatus.NEW, 5);
        manager.addSubtask(subtask);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks/2");
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        List<Subtask> subtasksFromManager = manager.getSubtasks();
        assertEquals(0, subtasksFromManager.size(), "Подзадача не удалена");
    }

    @Test
    public void testGetSubtaskById() throws IOException, InterruptedException {
        Epic epic = new Epic(1, "Epic", "Epic Description", TaskStatus.NEW);
        manager.addEpic(epic);

        Subtask subtask = new Subtask(1, 2, "Subtask", "Description", TaskStatus.NEW, 5);
        manager.addSubtask(subtask);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks/2");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        Subtask subtaskFromResponse = gson.fromJson(response.body(), Subtask.class);
        assertEquals(2, subtaskFromResponse.getId(), "Некорректный ID подзадачи");
        assertEquals("Subtask", subtaskFromResponse.getName(), "Некорректное имя подзадачи");
    }

    @Test
    public void testAddSubtaskWithTimeConflict() throws IOException, InterruptedException {
        Epic epic = new Epic(1, "Epic", "Epic Description", TaskStatus.NEW);
        manager.addEpic(epic);

        Subtask subtask1 = new Subtask(1, 2, "Subtask 1", "Description 1", TaskStatus.NEW, 60);
        subtask1.setStartTime(2024, 8, 18, 10, 0);
        manager.addSubtask(subtask1);

        Subtask subtask2 = new Subtask(1, 3, "Subtask 2", "Description 2", TaskStatus.NEW, 60);
        subtask2.setStartTime(2024, 8, 18, 10, 30);
        String subtaskJson = gson.toJson(subtask2);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(subtaskJson)).build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(406, response.statusCode(), "Ожидаемый код ошибки 406 не вернулся при добавлении подзадачи с конфликтом по времени");
    }

    @Test
    public void testAddSubtaskWithoutEpic() throws IOException, InterruptedException {
        Subtask subtask = new Subtask(10, 1, "Subtask", "Description", TaskStatus.NEW, 5);
        String subtaskJson = gson.toJson(subtask);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(subtaskJson)).build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(400, response.statusCode(), "Ожидаемый код ошибки не вернулся при добавлении подзадачи без эпика");
    }

    @Test
    public void testDeleteNonexistentSubtask() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks/999");
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, response.statusCode(), "Ожидаемый код ошибки не вернулся при удалении несуществующей подзадачи");
    }

    @Test
    public void testGetNonexistentSubtaskById() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks/999");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, response.statusCode(), "Ожидаемый код ошибки не вернулся при запросе несуществующей подзадачи");
    }
}