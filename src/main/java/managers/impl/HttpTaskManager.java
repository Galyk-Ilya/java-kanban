package managers.impl;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import enums.TaskType;
import exception.ManagerLoadException;
import server.KVTaskClient;
import task.*;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;

public class HttpTaskManager extends FileBackedTasksManager {
    public KVTaskClient taskClient;
    Gson gson;

    public HttpTaskManager(String url) {
        super(url);
        gson = new Gson();
        try {
            taskClient = new KVTaskClient(url);
        } catch (InterruptedException | IOException e) {
            throw new ManagerLoadException("Ошибка запуска");
        }
    }

    @Override
    public Map<Integer, CommonTask> getTaskList() {
        String stringTaskList = clientLoad("Tasks");
        if (stringTaskList == null) {return null;}
        return parseJsonFromMap(stringTaskList);
    }

    @Override
    public List<CommonTask> getHistory() {
        String stringHistory = clientLoad("History");
        if (stringHistory == null) {return null;}
        return parseJsonFromList(stringHistory);
    }

    @Override
    public TreeSet<CommonTask> getPrioritizedTasks() {
        String stringPrioritizedTasks = clientLoad("PrioritizedTasks");
        if (stringPrioritizedTasks == null) {return null;}
        return parseJsonFromSet(stringPrioritizedTasks);
    }

    @Override
    public void updateTask(CommonTask task) {
        super.updateTask(task);
        if (task instanceof Subtask) {
            int epicId = ((Subtask) task).getEpicId();
            clientPut(String.valueOf(epicId), gson.toJson(taskList.get(epicId)));
        }
        clientPut(String.valueOf(task.getId()), gson.toJson(task));
        save();
    }

    @Override
    public CommonTask createATask(String nameTask, String description, TaskType type, int duration, LocalDateTime startTime) {
        CommonTask newTask = createTaskAndPutToTaskList(nameTask, description, type, duration, startTime);
        if (newTask != null) {
            clientPut(String.valueOf(newTask.getId()), gson.toJson(newTask));
        }
        save();
        return newTask;
    }

    @Override
    public EpicTask createEpicTask(String nameTask, String description, TaskType type, int duration, LocalDateTime startTime) {
        EpicTask newTask = createEpicAndPutToTaskList(nameTask, description, type, duration, startTime);
        if (newTask != null) {
            clientPut(String.valueOf(newTask.getId()), gson.toJson(newTask));
        }
        save();
        return newTask;
    }

    @Override
    public Subtask createASubtask(String nameTask, String description, TaskType type, int epicId, int duration, LocalDateTime startTime) {
        Subtask newTask = createSubtaskAndPutToTaskList(nameTask, description, type, epicId, duration, startTime);
        if (newTask != null) {
            clientPut(String.valueOf(newTask.getId()), gson.toJson(newTask));
            EpicTask epicTask = gson.fromJson(clientLoad(String.valueOf(newTask.getEpicId())), EpicTask.class);
            epicTask.getSubtasksList().add(newTask);
            clientPut(String.valueOf(epicTask.getId()), gson.toJson(epicTask));
        }
        save();
        return newTask;
    }

    @Override
    public CommonTask getTask(int id) {
        String json = clientLoad(String.valueOf(id));
        if (json != null && !json.isBlank()) {
            CommonTask task = gson.fromJson(json, CommonTask.class);
            addToHistory(task);
            save();
            return task;
        } else {
            return null;
        }
    }

    @Override
    public EpicTask getEpic(int id) {
        String json = clientLoad(String.valueOf(id));
        if (json != null && !json.isBlank()) {
            EpicTask task = gson.fromJson(json, EpicTask.class);
            addToHistory(task);
            save();
            return task;
        } else {
            return null;
        }
    }

    @Override
    public Subtask getSubtask(int id) {
        String json = clientLoad(String.valueOf(id));
        if (json != null && !json.isBlank()) {
            Subtask task = gson.fromJson(json, Subtask.class);
            addToHistory(task);
            save();
            return task;
        } else {
            return null;
        }
    }

    @Override
    public void clearTaskList() {
        for (CommonTask task : taskList.values()) {
            clientPut(String.valueOf(task.getId()), gson.toJson(new CommonTask("DELETE", "DELETE")));
        }
        prioritizedTasks.clear();
        getDefaultHistory.clear();
        taskList.clear();
        clientPut("History", "DELETE");
        clientPut("Tasks", "DELETE");
        clientPut("PrioritizedTasks", "DELETE");
    }

    @Override
    public void deleteById(int id) {
        super.deleteById(id);
        clientPut(String.valueOf(id), gson.toJson(new CommonTask("DELETE", "DELETE")));
    }

    @Override
    public List<Subtask> getListSubtasks(EpicTask task) {
        List<Subtask> subtaskList = super.getListSubtasks(task);
        EpicTask epic = gson.fromJson(clientLoad(gson.toJson(subtaskList)), EpicTask.class);
        if (task != null && epic.getSubtasksList().equals(subtaskList))
            return epic.getSubtasksList();
        else return null;
    }

    @Override
    public void save() {
        if (getDefaultHistory.getHistory() !=null)
        clientPut("History", gson.toJson(getDefaultHistory.getHistory()));

        clientPut("Tasks", gson.toJson(taskList));
        clientPut("PrioritizedTasks", gson.toJson(prioritizedTasks));
        //надеюсь правильно понял мысль
    }

    private void clientPut(String id, String body) {
        try {
            taskClient.put(id, body);
        } catch (IOException | InterruptedException ignored) {
            throw new ManagerLoadException("Ошибка обработки запроса");
        }
    }

    private String clientLoad(String key) {
        try {
            return taskClient.load(key);
        } catch (IOException | InterruptedException ignored) {
            throw new ManagerLoadException("Ошибка обработки запроса");
        }
    }
    public List<CommonTask> parseJsonFromList(String xjson) {
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

    public TreeSet<CommonTask> parseJsonFromSet(String xjson) {
        TreeSet<CommonTask> list = new TreeSet<>(Comparator.comparing(CommonTask::getStartTime));
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

    public Map<Integer, CommonTask> parseJsonFromMap(String xjson) {
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