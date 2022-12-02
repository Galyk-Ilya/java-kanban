package memoryManagers;

import pattern.*;
import task.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static pattern.StatusType.*;

public class InMemoryTaskManager implements TaskManager {

    private static final Map<Integer, CommonTask> taskList = new HashMap<>();
    private static final HistoryManager getDefaultHistory = Managers.getDefaultHistory();
    private static Integer ID = 0;

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
                if (tool.getType() == TaskType.EPIC_TASK) {
                    EpicTask test = (EpicTask) tool;
                    getListSubtasks(test);
                    System.out.println();
                } else if (tool.getType() == TaskType.TASK) {
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
    public CommonTask getByID(Integer ID) {
        if (taskList.containsKey(ID)) {
            return taskList.get(ID);
        } else {
            System.out.println("Задача отсутстует");
            return null;
        }
    }

    @Override
    public void deleteByID(Integer ID) {
        if (taskList.containsKey(ID)) {
            System.out.println(taskList.get(ID) + " ID №" + ID + " удалена.");
            if (taskList.get(ID).getType() == TaskType.SUBTASK) {
                Subtask subtask = (Subtask) taskList.get(ID);
                Integer epicUpdate = subtask.getEpicId();
                EpicTask epicTask = (EpicTask) taskList.get(epicUpdate);
                if (epicTask != null) {
                    epicTask.getSubtasksList().remove(subtask);
                    updateTask(taskList.get(epicUpdate));
                }
            }
            if (TaskType.EPIC_TASK == taskList.get(ID).getType()) {
                EpicTask EpicTasks = (EpicTask) taskList.get(ID);
                for (CommonTask task : EpicTasks.getSubtasksList()) {
                    getDefaultHistory.remove(task.getId());
                }
            }
            getDefaultHistory.remove(ID);
            taskList.remove(ID);
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
    public CommonTask createATask(CommonTask task) {
        CommonTask newTask = task;
        if (task.getType() == TaskType.SUBTASK) {
            System.out.println(task.getType() + " не может быть создан без EpicTask");
        } else {
            Integer id = generateID();
            newTask = new CommonTask(task.getName(), task.getDescription());
            taskList.put(id, newTask);
            newTask.setId(id);
            System.out.println(task.getType() + " создана, ID задачи: " + newTask.getId());
        }

        return newTask;
    }

    @Override
    public EpicTask createATask(EpicTask task) {
        Integer id = generateID();
        EpicTask newTask = new EpicTask(task.getName(), task.getDescription());
        taskList.put(id, newTask);
        newTask.setId(id);
        System.out.println(task.getType() + " создана, ID задачи: " + newTask.getId());
        return newTask;
    }

    @Override
    public EpicTask createATask(EpicTask task, Subtask... subtask) {
        EpicTask newEpicTask = new EpicTask(task.getName(), task.getDescription());
        Integer idEpicTask = generateID();
        taskList.put(idEpicTask, newEpicTask);
        newEpicTask.setId(idEpicTask);
        System.out.println(newEpicTask.getType() + " создана, ID задачи: " + newEpicTask.getId());
        for (Subtask i : subtask) {
            Subtask newSubtask = new Subtask(i.getName(), i.getDescription());
            Integer idSubtask = generateID();
            newSubtask.setId(idSubtask);
            newSubtask.setEpicId(idEpicTask);
            newEpicTask.getSubtasksList().add(newSubtask);
            System.out.println(newSubtask.getType() + " создана, ID задачи: " + newSubtask.getId());
            taskList.put(idSubtask, newSubtask);
        }
        return newEpicTask;
    }

    @Override
    public void updateTask(CommonTask task) {

        taskList.put(task.getId(), task);

        if (task.getType() == TaskType.EPIC_TASK) {
            updateStatus((EpicTask) task);
        }
        if (task.getType() == TaskType.SUBTASK) {
            Subtask subtask = (Subtask) task;
            EpicTask epicTask = (EpicTask) taskList.get(subtask.getEpicId());
            updateStatus(epicTask);
        }
    }

    @Override
    public CommonTask getTask(Integer ID) {
        getDefaultHistory.addLast(taskList.get(ID));
        return getByID(ID);
    }

    @Override
    public EpicTask getEpic(Integer ID) {
        getDefaultHistory.addLast(taskList.get(ID));
        return (EpicTask) taskList.get(ID);
    }

    @Override
    public Subtask getSubtask(Integer ID) {
        getDefaultHistory.addLast(taskList.get(ID));
        return (Subtask) taskList.get(ID);
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
        return ++ID;
    }
}
