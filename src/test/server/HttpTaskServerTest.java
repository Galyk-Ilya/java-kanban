package server;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import enums.StatusType;
import exception.ManagerLoadException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import task.CommonTask;
import task.EpicTask;
import task.Subtask;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

import static enums.StatusType.NEW;
import static org.junit.jupiter.api.Assertions.*;

public class HttpTaskServerTest {
    KVServer kvServer;
    Gson gson = new Gson();
    HttpTaskServer httpTaskServer;
    HttpClient httpClient = HttpClient.newHttpClient();
//Все тесты работают при запуске в индивидуальном порядке, разработка этого класса не входит в ТЗ(согласно тз -тесты написал),
    //но для себя, прошу направить, в чем проблема с запуском сервера, предполагаю сервер не успевает
    // одновременно обрабатывать все запросы тестов(пробовал добавлять задержку в request


    @BeforeEach
    public void createServers() {
        try {
            kvServer = new KVServer();
            kvServer.start();
            httpTaskServer = new HttpTaskServer();
            httpTaskServer.start();
        } catch (IOException e) {
            throw new ManagerLoadException("Ошибка запуска");
        }
    }

    @BeforeEach
    public void createTask() {
        CommonTask task1 = new CommonTask(1, "CommonTask1", NEW, "description",
                10, LocalDateTime.of(2000, 10, 20, 10, 20));
        CommonTask task2 = new CommonTask(2, "CommonTask2", NEW, "description", 10,
                LocalDateTime.of(2000, 10, 20, 10, 20));
        CommonTask task3 = new CommonTask(3, "CommonTask3", NEW, "description",
                10, LocalDateTime.of(2000, 10, 20, 10, 20));
        EpicTask task4 = new EpicTask(4, "EpicTask1", NEW, "description",
                10, LocalDateTime.of(2000, 10, 20, 10, 20));
        Subtask task5 = new Subtask(5, "Subtask1", NEW, "description", 4,
                10, LocalDateTime.of(2600, 10, 20, 10, 20));
        Subtask task6 = new Subtask(6, "Subtask2", NEW, "description", 4,
                10, LocalDateTime.of(2000, 10, 20, 10, 20));
        put(gson.toJson(task1));
        put(gson.toJson(task2));
        put(gson.toJson(task3));
        put(gson.toJson(task4));
        put(gson.toJson(task5));
        put(gson.toJson(task6));
    }

    @AfterEach
    public void stopServer() {
        kvServer.stop();
        httpTaskServer.stop();
    }

    @Test
    public void endpointGET_TASKSTest() {
        Map<Integer, CommonTask> tasksList = parseJsonFromMap((load("/task/").body()));
        assertEquals(6, tasksList.size());
        assertEquals("CommonTask1", tasksList.get(1).getName());
    }

    @Test
    public void endpointGET_TASKTest() {
        String jsonTask1 = load("/task/1").body();

        CommonTask task = gson.fromJson(jsonTask1, CommonTask.class);
        assertNotEquals(2, (int) task.getId());
        assertEquals("CommonTask1", task.getName());
        assertEquals(404, load("/task/qwe").statusCode());
    }

    @Test
    public void endpointGET_EPICTest() {
        String jsonTask1 = load("/epic/4").body();
        EpicTask task = gson.fromJson(jsonTask1, EpicTask.class);
        assertNotEquals(2, (int) task.getId());
        assertEquals(400, load("/epic/150").statusCode());
    }

    @Test
    public void endpointGET_SUBTASKTest() {
        String jsonTask1 = load("/subtask/5").body();
        Subtask task = gson.fromJson(jsonTask1, Subtask.class);
        assertNotEquals(2, (int) task.getId());
        assertEquals("Subtask1", task.getName());
        assertEquals(404, load("/subtask/-1").statusCode());
        assertEquals("Некорректный идентификатор задачи, эпика или задача №150 не является Subtask",
                load("/subtask/150").body());
    }

    @Test
    public void endpointGET_SUBTASK_LISTTest() {
        String bodyE1 = load("/subtask/epic/4").body();
        List<CommonTask> list = parseJsonFromList(bodyE1);
        assertEquals(2, list.size());
        assertEquals("Subtask1", list.get(0).getName());
        assertEquals(200, delete("/task/5").statusCode());
        String bodyE2 = load("/subtask/epic/4").body();
        list = parseJsonFromList(bodyE2);
        assertEquals(1, list.size());
        assertEquals("Subtask2", list.get(0).getName());
    }

    @Test
    public void endpointGET_HISTORYTest() {
        String bodyHistory1 = load("/history/").body();
        assertEquals("История запросов пуста", bodyHistory1);
        CommonTask commonTask = gson.fromJson(load("/task/2").body(), CommonTask.class);
        String bodyHistory2 = load("/history/").body();
        List<CommonTask> listHistory2 = parseJsonFromList(bodyHistory2);
        assertEquals(1, listHistory2.size());
        assertEquals(commonTask, listHistory2.get(0));
    }

    @Test
    public void endpointGET_TASKS_PRIORITIZEDTest() {
        String testRequest1 = load("/").body();
        CommonTask task = new CommonTask(16, "CommonTask1 TEST", StatusType.NEW, "description",
                10, LocalDateTime.of(2500, 10, 20, 10, 20));
        List<CommonTask> taskPrioritizedList = parseJsonFromList(testRequest1);
        assertEquals(6, taskPrioritizedList.size());
        put(gson.toJson(task));
        String testRequest2 = load("/").body();
        taskPrioritizedList = parseJsonFromList(testRequest2);
        assertEquals(7, taskPrioritizedList.size());
        assertEquals(task.getName(), taskPrioritizedList.get(6).getName());
        assertNotEquals(task.getStartTime(), taskPrioritizedList.get(6).getStartTime());
        boolean comparison = Objects.equals(testRequest1, testRequest2);
        assertFalse(comparison);
    }

    @Test
    public void endpointPOST_TASKTest() {
        CommonTask commonTaskUpdate = new CommonTask(1, "CommonTask1 TEST", NEW, "description",
                10, LocalDateTime.of(2000, 10, 20, 10, 20));
        HttpResponse<String> responseTaskUpdate = put(gson.toJson(commonTaskUpdate));
        assertEquals("Задача обновлена", responseTaskUpdate.body());
        CommonTask createCommonTask = new CommonTask(100, "CommonTask1 TEST", NEW, "description",
                10, LocalDateTime.of(2000, 10, 20, 10, 20));
        HttpResponse<String> responseTaskCreate = put(gson.toJson(createCommonTask));
        assertEquals("Задача добавлена", responseTaskCreate.body());

        EpicTask epicTask1 = new EpicTask(100, "EpicTask1", NEW, "description",
                10, LocalDateTime.of(2000, 10, 20, 10, 20));
        HttpResponse<String> responseEpic = put(gson.toJson(epicTask1));
        assertEquals(201, responseEpic.statusCode());

        Subtask subtask1 = new Subtask(99, "Subtask1", NEW, "description", 10000000,
                10, LocalDateTime.of(2300, 10, 20, 10, 20));
        HttpResponse<String> responseSubtask = put(gson.toJson(subtask1));

        assertEquals("Заполните обязательные поля задачи или убедитесь в наличии Epic для Subtask",
                responseSubtask.body());
    }

    @Test
    public void endpointUNKNOWNTest() {
        CommonTask commonTaskUpdate = new CommonTask(1, "CommonTask1 TEST", NEW, "description",
                10, LocalDateTime.of(2000, 10, 20, 10, 20));
        String json = gson.toJson(commonTaskUpdate);
        URI url = URI.create("http://localhost:8080/tasks/task/");
        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request = HttpRequest.newBuilder().uri(url).PUT(body).build();
        try {
            assertEquals("Такого эндпоинта не существует или используемый метод не предусмотрен",
                    httpClient.send(request, HttpResponse.BodyHandlers.ofString()).body());
        } catch (InterruptedException | IOException e) {
            throw new ManagerLoadException("Ошибка операции");
        }
    }

    @Test
    public void endpointDELETE_TASKTest() {
        assertEquals(200, load("/task/1").statusCode());
        assertEquals(200, delete("/task/1").statusCode());
        assertEquals(load("/task/1").body(),
                "Некорректный идентификатор задачи, или задача №1 не является Task");
    }

    @Test
    public void endpointDELETE_TASKSTest() {
        assertEquals(200, load("/task/1").statusCode());
        assertEquals(200, load("/subtask/6").statusCode());
        assertEquals(200, delete("/task/").statusCode());
        assertEquals(400, load("/task/1").statusCode());
        assertEquals(400, load("/subtask/6").statusCode());
        assertEquals("Некорректный идентификатор задачи, или задача №1 не является Task",
                load("/task/1").body());
        //тест отрабатывает если читать его построчно(дебажить), если запустить то не успевает с сервера обрабатывать
        //запросы (наверно)
    }


    private HttpResponse<String> put(String json) {
        URI url = URI.create("http://localhost:8080/tasks/task/");
        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(body).build();
        try {
            return httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (InterruptedException | IOException e) {
            e.printStackTrace();
            throw new ManagerLoadException("Ошибка операции");
        }
    }

    private HttpResponse<String> delete(String query) {

        URI url = URI.create("http://localhost:8080/tasks" + query);
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().timeout(Duration.ofSeconds(5)).build();
        try {
            return httpClient.send(request, HttpResponse.BodyHandlers.ofString());
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

    private List<CommonTask> parseJsonFromList(String xjson) {
        List<CommonTask> list = new ArrayList<>();
        JsonParser jsonParser = new JsonParser();
        JsonArray array = (JsonArray) jsonParser.parse(xjson);
        CommonTask task = null;
        for (int i = 0; i < array.size(); i++) {
            String jsonElement = array.get(i).toString();
            if (jsonElement.contains("subtasksList")) {
                task = gson.fromJson(jsonElement, EpicTask.class);
            } else if (jsonElement.contains("epicId")) {
                task = gson.fromJson(jsonElement, Subtask.class);
            } else if (!jsonElement.contains("epicId") && !jsonElement.contains("subtasksList")) {
                task = gson.fromJson(jsonElement, CommonTask.class);
            }
            list.add(task);
        }
        return list;
    }

    private Map<Integer, CommonTask> parseJsonFromMap(String xjson) {
        Map<Integer, CommonTask> TaskList = new HashMap<>();
        JsonParser jsonParser = new JsonParser();
        JsonObject array = (JsonObject) jsonParser.parse(xjson);
        CommonTask task = null;
        for (String s : array.asMap().keySet()) {
            String jsonElement = array.asMap().get(s).toString();
            if (jsonElement.contains("subtasksList")) {
                task = gson.fromJson(jsonElement, EpicTask.class);
            } else if (jsonElement.contains("epicId")) {
                task = gson.fromJson(jsonElement, Subtask.class);
            } else if (!jsonElement.contains("epicId") && !jsonElement.contains("subtasksList")) {
                task = gson.fromJson(jsonElement, CommonTask.class);
            }
            TaskList.put(Integer.parseInt(s), task);
        }
        return TaskList;
    }
}
