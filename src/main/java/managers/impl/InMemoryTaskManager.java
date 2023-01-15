package managers.impl;

import enums.TaskType;
import managers.*;
import task.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

import static enums.StatusType.*;
import static enums.TaskType.*;

public class InMemoryTaskManager implements TaskManager {

    final Map<Integer, CommonTask> taskList = new HashMap<>();
    final HistoryManager getDefaultHistory = Managers.getDefaultHistory();
    final TreeSet<CommonTask> prioritizedTasks = new TreeSet<>(Comparator.comparing(CommonTask::getStartTime));
    private Integer ID = 1;

    @Override
    public Map<Integer, CommonTask> getTaskList() {
        return taskList;
    }

    @Override
    public void displayTaskList() {
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
        prioritizedTasks.clear();
        System.out.println("Все задачи удалены.");
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
                    taskList.remove(subtask.getId(), subtask);
                    prioritizedTasks.remove(task);
                }
            }
            getDefaultHistory.remove(id);
            System.out.println(taskList.get(id) + " ID №" + id + " удалена.");
            taskList.remove(id);
        } else {
            System.out.println("Задача с таким идентификатором отсутствует");
        }
    }

    @Override
    public List<Subtask> getListSubtasks(EpicTask task) {
        if (task == null) {
            System.out.println("Переданный эпик не существует");
            return null;
        }
        System.out.println(task);
        if (!task.getSubtasksList().isEmpty()) {
            for (Subtask subtask : task.getSubtasksList()) {
                System.out.println(subtask);
            }
        }
        return task.getSubtasksList();
    }

    @Override
    public CommonTask createATask(String nameTask, String description, TaskType type, int duration, LocalDateTime startTime) {
        return createTaskAndPutToTaskList(nameTask, description, type, duration, startTime);
    }

    @Override
    public EpicTask createEpicTask(String nameTask, String description, TaskType type, int duration, LocalDateTime startTime) {
        return createEpicAndPutToTaskList(nameTask, description, type, duration, startTime);
    }

    @Override
    public Subtask createASubtask(String nameTask, String description, TaskType type, int epicId, int duration, LocalDateTime startTime) {
        return createSubtaskAndPutToTaskList(nameTask, description, type, epicId, duration, startTime);
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
        CommonTask task = taskList.get(id);
        if (task != null && !(taskList.get(id) instanceof EpicTask) && !(taskList.get(id) instanceof EpicTask)) {
            getDefaultHistory.addLast(task);
            return task;
        }
        System.out.println("Задача под №" + id + " отсутствует или не является CommonTask");
        return null;
    }

    @Override
    public EpicTask getEpic(int id) {
        if (taskList.get(id) != null && taskList.get(id) instanceof EpicTask) {
            getDefaultHistory.addLast(taskList.get(id));
            return (EpicTask) taskList.get(id);
        }
        System.out.println("Задача под №" + id + " отсутствует или не является EpicTask");
        return null;
    }

    @Override
    public Subtask getSubtask(int id) {
        if (taskList.get(id) != null && taskList.get(id) instanceof Subtask) {
            Subtask subtask = (Subtask) taskList.get(id);
            if (taskList.containsKey(subtask.getEpicId())) {
                getDefaultHistory.addLast(taskList.get(id));
                return (Subtask) taskList.get(id);
            }
        }
        System.out.println("Задача под №" + id + " отсутствует или не является Subtask");
        return null;
    }

    @Override
    public List<CommonTask> getHistory() {
        return getDefaultHistory.getHistory();
    }

    @Override
    public Integer generateID() {
        while (taskList.containsKey(ID)) {
            ++ID;
        }
        return ID;
    }

    @Override
    public TreeSet<CommonTask> getPrioritizedTasks() {
        return prioritizedTasks;
    }

    public void updateStatus(EpicTask epicTask) {
        int newStatus = 0;
        int doneStatus = 0;
        for (Subtask taskTest : epicTask.getSubtasksList()) {
            if (taskTest.getStatus().equals(NEW)) {
                newStatus++;
            }
            if (taskTest.getStatus().equals(DONE)) {
                doneStatus++;
            }
        }
        if (epicTask.getSubtasksList().isEmpty() || newStatus == epicTask.getSubtasksList().size()) {
            epicTask.setStatus(NEW);
        } else if (doneStatus == epicTask.getSubtasksList().size()) {
            epicTask.setStatus(DONE);
        } else {
            epicTask.setStatus(IN_PROGRESS);
        }
    }

    public void createDeadline(CommonTask task, LocalDateTime startTime) {
        if (prioritizedTasks.isEmpty() || prioritizedTasks.last().calculateEndTime().isBefore(startTime)) {
            task.setStartTime(startTime);
        } else if (task instanceof Subtask) {
            EpicTask epicTask = (EpicTask) taskList.get(((Subtask) task).getEpicId());
            if (!(((Subtask) task).getEpicId() == prioritizedTasks.last().getId())) {

                task.setStartTime(prioritizedTasks.last().calculateEndTime().plusSeconds(1));
                epicTask.setEndTime(task.calculateEndTime());
            } else {
                task.setStartTime(startTime);
                epicTask.setEndTime(task.calculateEndTime());
                if (epicTask.getSubtasksList().isEmpty()) {
                    epicTask.setStartTime(task.getStartTime());
                    prioritizedTasks.remove(epicTask);
                }
            }
        } else {
            task.setStartTime(prioritizedTasks.last().calculateEndTime().plusSeconds(1));
        }
        prioritizedTasks.add(task);
    }

    protected CommonTask createTaskAndPutToTaskList(String nameTask, String description, TaskType type, int duration, LocalDateTime startTime) {
        if (type != COMMONTASK) {
            System.out.println("Задача не создана, передан неправильный тип");
            return null;
        }
        int idCommonTask = generateID();
        CommonTask newTask = new CommonTask(nameTask, description);
        newTask.setDuration(Duration.ofMinutes(duration));
        newTask.setId(idCommonTask);
        createDeadline(newTask, startTime);
        taskList.put(idCommonTask, newTask);
        return newTask;
    }


    protected EpicTask createEpicAndPutToTaskList(String nameTask, String description, TaskType type, int duration, LocalDateTime startTime) {
        if (type != EPIC_TASK) {
            System.out.println("Задача не создана, передан неправильный тип");
            return null;
        }
        int idEpicTask = generateID();
        EpicTask newTask = new EpicTask(nameTask, description);
        newTask.setDuration(Duration.ofMinutes(duration));
        newTask.setId(idEpicTask);
        taskList.put(idEpicTask, newTask);
        createDeadline(newTask, startTime);
        return newTask;
    }


    protected Subtask createSubtaskAndPutToTaskList(String nameTask, String description, TaskType type, int epicId, int duration, LocalDateTime startTime) {
        if (type != SUBTASK) {
            System.out.println("Задача не создана, передан неправильный тип");
            return null;
        }
        if (!taskList.containsKey(epicId)) {
            System.out.println("EpicTask для данной Subtask не создана");
            return null;
        } else {
            int idSubtask = generateID();
            Subtask newTask = new Subtask(nameTask, description);
            newTask.setDuration(Duration.ofMinutes(duration));
            newTask.setId(idSubtask);
            newTask.setEpicId(epicId);
            createDeadline(newTask, startTime);
            EpicTask epicTask = (EpicTask) taskList.get(epicId);
            epicTask.setEndTime(newTask.calculateEndTime());

            epicTask.getSubtasksList().add(newTask);
            taskList.put(idSubtask, newTask);
            return newTask;
        }
    }
    protected void addToHistory(int id){
        getDefaultHistory.addLast(taskList.get(id));
    }
    protected void addToHistory(CommonTask task){
        getDefaultHistory.addLast(task);
    }
}