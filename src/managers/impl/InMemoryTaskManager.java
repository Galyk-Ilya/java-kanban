package managers.impl;

import enums.TaskType;
import managers.*;
import task.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static enums.StatusType.*;

public class InMemoryTaskManager implements TaskManager {

    final Map<Integer, CommonTask> taskList = new HashMap<>();
    final HistoryManager getDefaultHistory = Managers.getDefaultHistory();
    private Integer ID = 1;

    @Override
    public Map<Integer, CommonTask> getTaskList() {
        return taskList;
    }

    @Override
    public void getAllTaskList() {
        if (taskList.size() < 1) {
            System.out.println("Список задач пуст");
        } else {
            for (CommonTask tool : taskList.values()) {
                if (tool instanceof EpicTask) {
                    EpicTask epicTask = (EpicTask) tool;
                    getListSubtasks(epicTask);
                    System.out.println();
                } else if (!(tool instanceof Subtask)) {
                    System.out.println(tool);
                }
            }
        }
    }

    @Override
    public void clearTaskList() {
        taskList.clear();
        System.out.println("Все задачи удалены.");
    }

    @Override
    public CommonTask getById(Integer id) {
        if (taskList.containsKey(id)) {
            return taskList.get(id);
        } else {
            System.out.println("Задача отсутстует");
            return null;
        }
    }

    @Override
    public void deleteById(int id) {
        CommonTask task = taskList.get(id);
        if (taskList.containsKey(id)) {
            if (task instanceof Subtask) {
                Subtask subtask = (Subtask) task;
                EpicTask epicTask = (EpicTask) taskList.get(subtask.getEpicId());
                epicTask.getSubtasksList().remove(subtask);
                updateTask(taskList.get(subtask.getEpicId()));
            }
            if (task instanceof EpicTask) {
                EpicTask EpicTasks = (EpicTask) task;
                for (Subtask subtask : EpicTasks.getSubtasksList()) {
                    getDefaultHistory.remove(subtask.getId());
                }
            }
            getDefaultHistory.remove(id);
            System.out.println(taskList.get(id) + " ID №" + id + " удалена.");
            taskList.remove(id);
        } else {
            System.out.println("Задача с таким идентификатором отсутстует");
        }
    }

    @Override
    public List<Subtask> getListSubtasks(EpicTask task) {
        System.out.println(task);
        if (task.getSubtasksList().size() > 0) {
            System.out.println("-------------------------------------------------------------");
            for (Subtask subtask : task.getSubtasksList()) {
                System.out.println(subtask);
            }
            System.out.println("-------------------------------------------------------------");
        }
        return task.getSubtasksList();
    }

    @Override
    public CommonTask createATask(String nameTask, String description, TaskType type) {
        Integer idCommonTask = generateID();
        CommonTask newTask = new CommonTask(nameTask, description);
        newTask.setId(idCommonTask);
        System.out.println("CommonTask создана, ID задачи: " + idCommonTask);
        taskList.put(idCommonTask, newTask);
        return newTask;
    }

    @Override
    public EpicTask createEpicTask(String nameTask, String description, TaskType type) {
        Integer idEpicTask = generateID();
        EpicTask newTask = new EpicTask(nameTask, description);
        newTask.setId(idEpicTask);
        System.out.println("EpicTask создана, ID задачи: " + idEpicTask);
        taskList.put(idEpicTask, newTask);
        return newTask;
    }

    @Override
    public Subtask createASubtask(String nameTask, String description, TaskType type, int epicId) {
        if (!taskList.containsKey(epicId)) {
            System.out.println("EpicTask для данной Subtask не создана");
            return null;
        } else {
            Integer idSubtask = generateID();
            Subtask newTask = new Subtask(nameTask, description);
            newTask.setId(idSubtask);
            newTask.setEpicId(epicId);
            EpicTask epicTask = (EpicTask) taskList.get(epicId);
            epicTask.getSubtasksList().add(newTask);
            taskList.put(idSubtask, newTask);
            System.out.println("Subtask создана, ID задачи: " + idSubtask);
            return newTask;
        }
    }

    @Override
    public void updateTask(CommonTask task) {
        taskList.put(task.getId(), task);
        if (task instanceof EpicTask) {
            updateStatus((EpicTask) task);
        }
        if (task instanceof Subtask) {
            Subtask subtask = (Subtask) task;
            EpicTask epicTask = (EpicTask) taskList.get(subtask.getEpicId());
            updateStatus(epicTask);
        }
    }

    @Override
    public CommonTask getTask(int id) {
        if (taskList.get(id) != null) {
            getDefaultHistory.addLast(taskList.get(id));
            return getById(id);
        }
        return null;
    }

    @Override
    public EpicTask getEpic(int id) {
        if (taskList.get(id) != null) {
            getDefaultHistory.addLast(taskList.get(id));
            return (EpicTask) taskList.get(id);
        }
        return null;
    }

    @Override
    public Subtask getSubtask(int id) {
        if (taskList.get(id) != null) {
            Subtask subtask = (Subtask) taskList.get(id);
            if (taskList.containsKey(subtask.getEpicId())){
                getDefaultHistory.addLast(taskList.get(id));
                return (Subtask) taskList.get(id);
            }
        }
        return null;
    }

    @Override
    public List<CommonTask> getHistory() {
        return getDefaultHistory.getHistory();
    }

    public void updateStatus(EpicTask epicTask) {
        int testStatus = 0;
        for (Subtask taskTest : epicTask.getSubtasksList()) {
            if (taskTest.getStatus().equals(NEW)) {
                testStatus++;
            }
        }
        if (epicTask.getSubtasksList().size() < 1 || testStatus == epicTask.getSubtasksList().size()) {
            epicTask.setStatus(NEW);
        } else if (testStatus == 0) {
            epicTask.setStatus(DONE);
        } else {
            epicTask.setStatus(IN_PROGRESS);
        }
    }

    public Integer generateID() {
        while (taskList.containsKey(ID)) {
            ++ID;
        }
        return ID;
    }
}