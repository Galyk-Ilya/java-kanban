package server;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import enums.TaskType;
import exception.ManagerIOException;
import managers.Managers;
import managers.TaskManager;
import task.CommonTask;
import task.EpicTask;
import task.Subtask;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;
import java.util.regex.Pattern;

import static server.HttpTaskServer.TasksHandler.Endpoint.*;

public class HttpTaskServer {
    private static final TaskManager manager = Managers.getDefault();
    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
    private static final int PORT = 8080;
    private static final Gson gson = new Gson();
    protected HttpServer httpServer;

    public HttpTaskServer() {
        try {
            httpServer = HttpServer.create();
            httpServer.bind(new InetSocketAddress(PORT), 0);
            httpServer.createContext("/tasks", new TasksHandler());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void start() {
        httpServer.start();
        System.out.println("HTTP-сервер запущен на " + PORT + " порту!");
    }

    public void stop() {
        httpServer.stop(5);
    }

    static class TasksHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) {
            String method = exchange.getRequestMethod();
            String path = exchange.getRequestURI().getPath();
            Endpoint endpoint = getEndpoint(path, method);
            try {
                switch (endpoint) {
                    case GET_TASKS:
                        if (!manager.getTaskList().isEmpty()) {
                        writeResponse(exchange, gson.toJson(manager.getTaskList()), 200);
                        } else {
                            writeResponse(exchange, "Список задач пуст", 200);
                        }
                        break;

                    case GET_TASK: {
                        int id = parsePatchId(path.replaceFirst("/tasks/task/", ""));
                        CommonTask task = manager.getTask(id);
                        if (task != null && !task.getName().equals("DELETE")) {
                            writeResponse(exchange, gson.toJson(task), 200);
                        } else {
                            writeResponse(exchange, "Некорректный идентификатор задачи, или задача №" + id +
                                    " не является Task", 400);
                        }
                        break;
                    }
                    case DELETE_TASKS:
                        manager.clearTaskList();
                        writeResponse(exchange, "Список очищен", 200);
                        break;
                    case DELETE_TASK: {
                        int id = parsePatchId(path.replaceFirst("/tasks/task/", ""));
                        if (id >= 0) {
                            manager.deleteById(id);
                            writeResponse(exchange, "Задача удалена", 200);
                        } else {
                            writeResponse(exchange, "Некорректный идентификатор задачи", 400);
                        }
                        break;
                    }
                    case GET_EPIC: {
                        int id = parsePatchId(path.replaceFirst("/tasks/epic/", ""));
                        EpicTask epicTask = manager.getEpic(id);
                        if (epicTask != null && !epicTask.getName().equals("DELETE")) {
                            writeResponse(exchange, gson.toJson(epicTask), 200);
                        } else {
                            writeResponse(exchange, "Некорректный идентификатор задачи, или задача №" + id +
                                    " не является Epic", 400);
                        }
                        break;
                    }
                    case GET_SUBTASK_LIST: {
                        int id = parsePatchId(path.replaceFirst("/tasks/subtask/epic/", ""));
                        List<Subtask> subtaskList = manager.getEpic(id).getSubtasksList();
                        boolean taskContains = manager.getTaskList().containsKey(id);
                        if (manager.getTaskList().get(id) instanceof EpicTask && subtaskList != null && taskContains) {
                            writeResponse(exchange, gson.toJson(subtaskList), 200);
                        } else {
                            writeResponse(exchange, "Некорректный идентификатор задачи, или задача №" + id +
                                    " не является Epic", 400);
                        }
                        break;
                    }
                    case GET_SUBTASK: {
                        int id = parsePatchId(path.replaceFirst("/tasks/subtask/", ""));
                        Subtask subtask = manager.getSubtask(id);
                        if (subtask != null && !subtask.getName().equals("DELETE")) {
                            writeResponse(exchange, gson.toJson(subtask), 200);
                        } else {
                            writeResponse(exchange, "Некорректный идентификатор задачи, эпика или задача №" + id +
                                    " не является Subtask", 400);
                            break;
                        }
                    }
                    case GET_HISTORY:
                        if (!manager.getHistory().isEmpty()) {
                            writeResponse(exchange, gson.toJson(manager.getHistory()), 200);
                        } else {
                            writeResponse(exchange, "История запросов пуста", 200);
                        }
                        break;
                    case GET_TASKS_PRIORITIZED:
                        if (!manager.getPrioritizedTasks().isEmpty()) {
                        writeResponse(exchange, gson.toJson(manager.getPrioritizedTasks()), 200);
                        } else {
                            writeResponse(exchange, "Список задач пуст", 200);
                        }
                        break;
                    case POST_TASK: {
                        try (InputStream inputStream = exchange.getRequestBody()) {
                            String body = new String(inputStream.readAllBytes(), DEFAULT_CHARSET);
                            if (body.isBlank()) {
                                writeResponse(exchange, "Получен некорректный JSON", 400);
                                break;
                            }
                            switch (taskCreator(body)) {
                                case "added":
                                    writeResponse(exchange, "Задача добавлена", 201);
                                    break;
                                case "updated":
                                    writeResponse(exchange, "Задача обновлена", 201);
                                    break;
                                case "null":
                                    writeResponse(exchange, "Заполните обязательные поля задачи или убедитесь в наличии Epic для Subtask", 400);
                                    break;
                            }
                        } catch (JsonSyntaxException exception) {
                            System.out.println("test2");
                            writeResponse(exchange, "Получен некорректный JSON", 400);
                        }
                    }
                    default:
                        writeResponse(exchange, "Такого эндпоинта не существует или используемый метод не предусмотрен",
                                404);
                }
            } catch (IOException exception) {
                throw new ManagerIOException("Сбой сервера, повторите попытку");
            }
        }

        private Endpoint getEndpoint(String requestPath, String requestMethod) {
            switch (requestMethod) {
                case "GET":
                    if (Pattern.matches("^/tasks/task/\\d+$", requestPath)) {
                        return GET_TASK;
                    } else if (Pattern.matches("^/tasks/subtask/\\d+$", requestPath)) {
                        return GET_SUBTASK;
                    } else if (Pattern.matches("^/tasks/epic/\\d+$", requestPath)) {
                        return GET_EPIC;
                    } else if (Pattern.matches("^/tasks/subtask/epic/\\d+$", requestPath)) {
                        return GET_SUBTASK_LIST;
                    } else if (Pattern.matches("^/tasks/task/", requestPath)) {
                        return GET_TASKS;
                    } else if (Pattern.matches("^/tasks/history/", requestPath)) {
                        return GET_HISTORY;
                    } else if (Pattern.matches("^/tasks/", requestPath)) {
                        return GET_TASKS_PRIORITIZED;
                    }
                    break;
                case "DELETE":
                    if (Pattern.matches("^/tasks/task/\\d+$", requestPath)) {
                        return DELETE_TASK;
                    } else if (Pattern.matches("^/tasks/task/", requestPath)) {
                        return DELETE_TASKS;
                    }
                    break;
                case "POST":
                    if (Pattern.matches("^/tasks/task/", requestPath)) {
                        return POST_TASK;
                    }
                    break;
            }
            return UNKNOWN;
        }

        private int parsePatchId(String path) {
            try {
                return Integer.parseInt(path);
            } catch (NumberFormatException | NullPointerException exception) {
                return -1;
            }
        }

        private String taskCreator(String body) {
            CommonTask task = null;
            if (body.contains("subtasksList")) {
                task = gson.fromJson(body, EpicTask.class);
            } else if (body.contains("epicId")) {
                task = gson.fromJson(body, Subtask.class);
            } else if (!body.contains("epicId") && !body.contains("subtasksList")) {
                task = gson.fromJson(body, CommonTask.class);
            }
            if (task != null && !task.getName().isBlank() && !task.getDescription().isBlank() &&
                    parsePatchId(String.valueOf(task.getDuration().toMinutes())) >= 0) {
                int id = parsePatchId(String.valueOf(task.getId()));
                String name = task.getName();
                String description = task.getDescription();
                int duration = Integer.parseInt(String.valueOf(task.getDuration().toMinutes()));
                LocalDateTime startTime = task.getStartTime();

                if (manager.getTaskList().containsKey(id)) {
                    manager.updateTask(task);
                    return "updated";
                } else {
                    if (task instanceof EpicTask) {
                        EpicTask epicTask = manager.createEpicTask(name, description,
                                TaskType.EPIC_TASK, duration, startTime);
                        epicTask.setStatus(task.getStatus());
                    } else if (task instanceof Subtask) {
                        if (!manager.getTaskList().containsKey(((Subtask) task).getEpicId())) {
                            return "null";
                        }
                        Subtask subtask = manager.createASubtask(name, description, TaskType.SUBTASK,
                                ((Subtask) task).getEpicId(), duration, startTime);
                        subtask.setStatus(task.getStatus());
                    } else {
                        CommonTask commonTask = manager.createATask(name, description,
                                TaskType.COMMONTASK, duration, startTime);
                        commonTask.setStatus(task.getStatus());
                    }
                    return "added";
                }
            } else {
                return "null";
            }
        }

        private void writeResponse(HttpExchange exchange,
                                   String responseString,
                                   int responseCode) throws IOException {
            if (responseString.isBlank()) {
                exchange.sendResponseHeaders(responseCode, 0);
            } else {
                byte[] bytes = responseString.getBytes(DEFAULT_CHARSET);
                exchange.sendResponseHeaders(responseCode, bytes.length);
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(bytes);
                }
            }
            exchange.close();
        }

        enum Endpoint {
            GET_TASKS, GET_TASK,
            POST_TASK/*Добавить логику на разделение - добавления или обновления таски*/,
            DELETE_TASKS, DELETE_TASK,
            GET_EPIC, GET_SUBTASK_LIST,
            GET_SUBTASK,
            GET_HISTORY,
            GET_TASKS_PRIORITIZED, UNKNOWN
        }
    }
}