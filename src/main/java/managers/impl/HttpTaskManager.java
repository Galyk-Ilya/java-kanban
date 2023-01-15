package managers.impl;

import com.google.gson.Gson;
import enums.TaskType;
import exception.ManagerLoadException;
import server.KVTaskClient;
import task.CommonTask;
import task.EpicTask;
import task.Subtask;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

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
    public void updateTask(CommonTask task) {
        super.updateTask(task);
        if (task instanceof Subtask) {
            int epicId = ((Subtask) task).getEpicId();
            clientPut(String.valueOf(epicId), gson.toJson(taskList.get(epicId)));
        }
        clientPut(String.valueOf(task.getId()), gson.toJson(task));
    }

    public CommonTask createATask(String nameTask, String description, TaskType type, int duration, LocalDateTime startTime) {
        CommonTask newTask = createTaskAndPutToTaskList(nameTask, description, type, duration, startTime);
        if (newTask != null) {
            clientPut(String.valueOf(newTask.getId()), gson.toJson(newTask));
        }
        return newTask;
    }

    @Override
    public EpicTask createEpicTask(String nameTask, String description, TaskType type, int duration, LocalDateTime startTime) {
        EpicTask newTask = createEpicAndPutToTaskList(nameTask, description, type, duration, startTime);
        if (newTask != null) {
            clientPut(String.valueOf(newTask.getId()), gson.toJson(newTask));
        }
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
        return newTask;
    }

    @Override
    public CommonTask getTask(int id) {
        String json = clientLoad(String.valueOf(id));
        if (json != null && !json.isBlank()) {
            CommonTask task = gson.fromJson(json,CommonTask.class);
            addToHistory(task);
            return task;
        } else {
            return null;
        }
    }

    @Override
    public EpicTask getEpic(int id) {
        String json = clientLoad(String.valueOf(id));
        if (json != null && !json.isBlank()) {
   //         addToHistory(id);
            return gson.fromJson(json, EpicTask.class);
        }
        return null;
    }

    @Override
    public Subtask getSubtask(int id) {
        String json = clientLoad(String.valueOf(id));
        if (json != null && !json.isBlank()) {
      //      addToHistory(id);
            return gson.fromJson(json, Subtask.class);
        } else {
            return null;
        }
    }

    @Override
    public Map<Integer, CommonTask> getTaskList() {
        return super.getTaskList();
    }

    @Override
    public void clearTaskList() {
        for (CommonTask task : taskList.values()) {
            clientPut(String.valueOf(task.getId()), gson.toJson(new CommonTask("DELETE", "DELETE")));
        }
        prioritizedTasks.clear();
        taskList.clear();
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
        if (epic.getSubtasksList().equals(subtaskList))
            return epic.getSubtasksList();
        else return null;
    }

    @Override
    public List<CommonTask> getHistory() {
        return super.getHistory();
    }

    @Override
    public TreeSet<CommonTask> getPrioritizedTasks() {
        return super.getPrioritizedTasks();
    }

    @Override
    public void save() {
        //Логика FileBackedTasksManager очень сильно в методе save() зависима с классом File
        //Мне удобнее было бы наследоваться от InMemoryTaskManager, но это не соответствует ТЗ
        //p.s. Поэтому пришлось таким образом заруинить метод тут
        //Буду раз комментариям как можно было бы обойти эту ситуацию)

        //Также буду раз по коду комментариям, использование бесполезных или слишком сложных способов
        //достижения цели, неправильных модификаторов доступа и т.д.
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

}
//переписать методы крейт и гет исключить супер
