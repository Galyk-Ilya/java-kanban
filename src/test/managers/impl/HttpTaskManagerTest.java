package managers.impl;

import com.google.gson.Gson;
import enums.StatusType;
import exception.ManagerLoadException;
import managers.Managers;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import server.HttpTaskServer;
import server.KVServer;
import task.CommonTask;
import task.EpicTask;
import task.Subtask;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;

import static enums.TaskType.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class HttpTaskManagerTest extends TaskManagerTest<HttpTaskManager> {

    KVServer kvServer;
    Gson gson = new Gson();
    HttpTaskServer httpTaskServer;
    HttpClient httpClient = HttpClient.newHttpClient();

    @BeforeEach
    public void createServers() {
        try {
            if (kvServer == null) {
                kvServer = new KVServer();
                kvServer.start();
            }
            if (httpTaskServer == null) {
                httpTaskServer = new HttpTaskServer();
                httpTaskServer.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new ManagerLoadException("Ошибка запуска");
        }
        taskManager = (HttpTaskManager) Managers.getDefault();
        taskManager.createATask("CommonTask1", "description", COMMONTASK,
                10, LocalDateTime.of(2000, 10, 20, 10, 20));
        taskManager.createATask("CommonTask2", "description", COMMONTASK,
                10, LocalDateTime.of(2000, 10, 20, 10, 20));
        taskManager.createATask("CommonTask3", "description", COMMONTASK,
                10, LocalDateTime.of(2000, 10, 20, 10, 20));

        EpicTask epicTask1 = taskManager.createEpicTask("EpicTask1", "description", EPIC_TASK,
                10, LocalDateTime.of(2000, 10, 20, 10, 20));

        taskManager.createASubtask("Subtask1", "description", SUBTASK, epicTask1.getId(),
                10, LocalDateTime.of(2600, 10, 20, 10, 20));
        taskManager.createASubtask("Subtask2", "description", SUBTASK, epicTask1.getId(),
                10, LocalDateTime.of(2000, 10, 20, 10, 20));
    }

    @AfterEach
    public void stopServer() {
        kvServer.stop();
        httpTaskServer.stop();
    }

    @Test
    void clearTaskListTest() {
        String body = load("/subtask/5").body();
        assertEquals(gson.fromJson(body, Subtask.class), taskManager.getSubtask(5));
        taskManager.clearTaskList();
        assertEquals(400, load("/subtask/5").statusCode());
        assertEquals("Некорректный идентификатор задачи, или задача №1 не является Task",
                load("/task/1").body());
    }

    @Test
    void deleteByIdTest() {
        assertEquals(200, load("/task/1").statusCode());
        taskManager.deleteById(1);
        assertEquals(load("/task/1").statusCode(), 400);
        assertEquals(load("/task/1").body(),
                "Некорректный идентификатор задачи, или задача №1 не является Task");
    }

    @Test
    void getListSubtasksTest() {
        String body = load("/subtask/epic/4").body();
        System.out.println(body);
        EpicTask epicTask = gson.fromJson(body, EpicTask.class);
        System.out.println(body);
        assertEquals(2, epicTask.getSubtasksList().size());
        taskManager.deleteById(5);
        epicTask = (EpicTask) taskManager.taskList.get(epicTask.getId());
        assertEquals(1, epicTask.getSubtasksList().size());

    }

    @Test
    void getHistoryTest() {
        String managerTest1 = gson.toJson(taskManager.getPrioritizedTasks());
        String httpTest1 = load("/task/").body();
        taskManager.getTask(1);
        String managerTest2 = gson.toJson(taskManager.getPrioritizedTasks());
        String httpTest2 = load("/task/").body();
        assertNotEquals(managerTest1, managerTest2);
        assertNotEquals(httpTest1, httpTest2);
    }

    @Test
    void getTaskListTest() {
        assertEquals(6, taskManager.getTaskList().size());
        Subtask subtask = gson.fromJson(load("/subtask/6").body(), Subtask.class);
        assertEquals(subtask, taskManager.taskList.get(6));
    }

    @Test
    void getPrioritizedTasksTest() {
        String managerTest1 = gson.toJson(taskManager.getPrioritizedTasks());
        String httpTest1 = load("/").body();
        CommonTask task = new CommonTask(1, "CommonTask1 TEST", StatusType.NEW, "description",
                10, LocalDateTime.of(1, 10, 20, 10, 20));
        put(gson.toJson(task));
        String managerTest2 = gson.toJson(taskManager.getPrioritizedTasks());
        String httpTest2 = load("/").body();
        assertNotEquals(managerTest1, managerTest2);
        assertNotEquals(httpTest1, httpTest2);
    }

    @Test
    void createATaskTest() {
        CommonTask commonTaskUpdate = new CommonTask(1, "CommonTask1 TEST", StatusType.NEW, "description",
                10, LocalDateTime.of(2000, 10, 20, 10, 20));
        put(gson.toJson(commonTaskUpdate));
        assertEquals(gson.fromJson(load("/task/1").body(), CommonTask.class).getName(), "CommonTask1 TEST");
    }

    @Test
    void createEpicTaskTest() {
        taskManager.clearTaskList();
        EpicTask epicTask1 = taskManager.createEpicTask("EpicTask1", "description", SUBTASK,
                10, LocalDateTime.of(2000, 10, 20, 10, 20));
        put(gson.toJson(epicTask1));
    }

    @Test
    void createASubtaskTest() {
        Subtask subtask1 = taskManager.createASubtask("Subtask1", "description", SUBTASK, 10000000,
                10, LocalDateTime.of(2300, 10, 20, 10, 20));
        put(gson.toJson(subtask1));
        assertEquals("Некорректный идентификатор задачи, эпика или задача №7 не является Subtask",
                load("/subtask/7").body());
    }

    @Test
    void getTaskTest() {
        String jsonTask1 = load("/task/1").body();
        CommonTask task = gson.fromJson(jsonTask1, CommonTask.class);
        assertNotEquals(2, (int) task.getId());
        assertEquals(task.getId(), taskManager.taskList.get(1).getId());
        assertEquals(task.getName(), "CommonTask1");
        assertEquals(task.getDuration(), taskManager.taskList.get(1).getDuration());
        assertEquals(load("/task/qwe").statusCode(), 404);
    }

    @Test
    void getEpicTest() {
        String jsonTask1 = load("/epic/4").body();
        EpicTask task = gson.fromJson(jsonTask1, EpicTask.class);
        assertNotEquals(2, (int) task.getId());
        assertEquals(task.getId(), taskManager.taskList.get(4).getId());
        assertEquals(task.getName(), "EpicTask1");
        assertEquals(task.getDuration(), taskManager.taskList.get(4).getDuration());
        assertEquals(load("/epic/150").statusCode(), 400);
    }

    @Test
    void getSubtaskTest() {
        String jsonTask1 = load("/subtask/5").body();
        Subtask task = gson.fromJson(jsonTask1, Subtask.class);
        assertNotEquals(2, (int) task.getId());
        assertEquals(task.getId(), taskManager.taskList.get(5).getId());
        assertEquals(task.getName(), "Subtask1");
        assertEquals(task.getDuration(), taskManager.taskList.get(1).getDuration());
        assertEquals(load("/subtask/-1").statusCode(), 404);
        assertEquals(load("/subtask/150").body(),
                "Некорректный идентификатор задачи, эпика или задача №150 не является Subtask");
    }

    private void put(String json) {
        URI url = URI.create("http://localhost:8080/tasks/task/");
        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(body).build();
        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (InterruptedException | IOException e) {
            e.printStackTrace();
            throw new ManagerLoadException("Ошибка операции");
        }
    }

    private HttpResponse<String> load(String query) {

        URI url = URI.create("http://localhost:8080/tasks" + query);
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        try {
            return httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (InterruptedException | IOException e) {
            e.printStackTrace();
            throw new ManagerLoadException("Ошибка операции");
        }
    }
}
