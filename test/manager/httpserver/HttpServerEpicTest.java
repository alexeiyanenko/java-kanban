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

public class HttpServerEpicTest {

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
    public void testAddEpic() throws IOException, InterruptedException {
        Epic epic = new Epic(1, "Epic", "Epic Description", TaskStatus.NEW);
        String epicJson = gson.toJson(epic);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(epicJson)).build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());

        List<Epic> epicsFromManager = manager.getEpics();
        assertNotNull(epicsFromManager, "Эпики не возвращаются");
        assertEquals(1, epicsFromManager.size(), "Некорректное количество эпиков");
        assertEquals("Epic", epicsFromManager.get(0).getName(), "Некорректное имя эпика");
    }

    @Test
    public void testGetEpics() throws IOException, InterruptedException {
        Epic epic = new Epic(1, "Epic", "Epic Description", TaskStatus.NEW);
        manager.addEpic(epic);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        Epic[] epics = gson.fromJson(response.body(), Epic[].class);
        assertEquals(1, epics.length, "Некорректное количество эпиков");
        assertEquals("Epic", epics[0].getName(), "Некорректное имя эпика");
    }

    @Test
    public void testGetEpicById() throws IOException, InterruptedException {
        Epic epic = new Epic(1, "Epic", "Epic Description", TaskStatus.NEW);
        manager.addEpic(epic);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics/1");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        Epic epicFromResponse = gson.fromJson(response.body(), Epic.class);
        assertEquals(1, epicFromResponse.getId(), "Некорректный ID эпика");
        assertEquals("Epic", epicFromResponse.getName(), "Некорректное имя эпика");
    }

    @Test
    public void testGetSubtasksOfEpic() throws IOException, InterruptedException {
        Epic epic = new Epic(1, "Epic", "Epic Description", TaskStatus.NEW);
        manager.addEpic(epic);

        Subtask subtask1 = new Subtask(1, 2, "Subtask 1", "Description 1", TaskStatus.NEW, 5);
        manager.addSubtask(subtask1);

        Subtask subtask2 = new Subtask(1, 3, "Subtask 2", "Description 2", TaskStatus.NEW, 5);
        manager.addSubtask(subtask2);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics/1/subtasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        Subtask[] subtasks = gson.fromJson(response.body(), Subtask[].class);
        assertEquals(2, subtasks.length, "Некорректное количество подзадач эпика");
        assertEquals("Subtask 1", subtasks[0].getName(), "Некорректное имя первой подзадачи эпика");
        assertEquals("Subtask 2", subtasks[1].getName(), "Некорректное имя второй подзадачи эпика");
    }

    @Test
    public void testDeleteEpic() throws IOException, InterruptedException {
        Epic epic = new Epic(1, "Epic", "Epic Description", TaskStatus.NEW);
        manager.addEpic(epic);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics/1");
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        List<Epic> epicsFromManager = manager.getEpics();
        assertEquals(0, epicsFromManager.size(), "Эпик не удален");
    }

    @Test
    public void testGetNonexistentEpicById() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics/999");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, response.statusCode(), "Ожидаемый код ошибки не вернулся при запросе несуществующего эпика");
    }

    @Test
    public void testDeleteNonexistentEpic() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics/999");
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, response.statusCode(), "Ожидаемый код ошибки не вернулся при удалении несуществующего эпика");
    }

    @Test
    public void testGetSubtasksOfNonexistentEpic() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics/999/subtasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, response.statusCode(), "Ожидаемый код ошибки не вернулся при запросе подзадач несуществующего эпика");
    }
}

