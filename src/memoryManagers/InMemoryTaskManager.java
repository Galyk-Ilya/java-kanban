package memoryManagers;

import pattern.TaskManager;
import task.CommonTask;
import task.EpicTask;
import task.Subtask;

import java.util.HashMap;
import java.util.Random;

import static pattern.StatusType.*;

public class InMemoryTaskManager implements TaskManager {

    private static final HashMap<Integer, CommonTask> taskList = new HashMap<>();

    public HashMap<Integer, CommonTask> getTaskList() {
        return taskList;
    }

    @Override
    public void getAllTaskList() {
        if (taskList.size() < 1) {
            System.out.println("Список задач пуст");
        } else {
            for (CommonTask tool : taskList.values()) {
                if (String.valueOf(tool.getClass()).equals("class task.EpicTask")) {
                    EpicTask test = (EpicTask) tool;
                    getSubtask(test);
                    System.out.println();
                } else if (String.valueOf(tool.getClass()).equals("class task.CommonTask")) {
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
            if (String.valueOf(taskList.get(ID).getClass()).equals("class task.EpicTask")) {
                EpicTask epicTask = (EpicTask) taskList.get(ID);
                System.out.println(epicTask + " ID №" + ID + " удалена.");
            } else {
                System.out.println(taskList.get(ID) + " ID №" + ID + " удалена.");
            }

            taskList.remove(ID);
            for (CommonTask i : taskList.values()) {
                if (String.valueOf(i.getClass()).equals("class task.EpicTask")) {
                    EpicTask epicTask = (EpicTask) i;
                    for (Subtask j : epicTask.getSubtasksList()) {
                        if (j.getId().equals(ID)) {
                            epicTask.getSubtasksList().remove(j);
                            break;
                        }
                    }
                }
            }
        } else {
            System.out.println("Задача с таким идентификатором отсутстует");
        }
    }

    @Override
    public void getSubtask(EpicTask task) {
        System.out.println("{EpicTask [name = '" + task.getName() + "', description = '" + task.getDescription()
                + "', status = '" + task.getStatus() + "'.]}");
        if (task.getSubtasksList().size() > 0) {
            for (Subtask subtask : task.getSubtasksList()) {
                System.out.println("{Subtask [name = '" + subtask.getName() + "', description = '" + subtask.getDescription()
                        + "', status = '" + subtask.getStatus() + "'.]}");
            }
        }
    }

    @Override
    public void createATask(CommonTask task) {
        Integer id = generateID();
        taskList.put(id, task);
        task.setId(id);
        if (String.valueOf(task.getClass()).equals("class task.Subtask")) {
            System.out.println("Подзадача создана, ID задачи: " + id);
        } else if (String.valueOf(task.getClass()).equals("class task.EpicTask")) {
            System.out.println("Epic_задача создана, ID задачи: " + id);
        } else {
            System.out.println("Задача создана, ID задачи: " + id);
        }
    }

    @Override
    public void createATask(EpicTask task, Subtask... subtask) {
        Integer idEpicTask = generateID();
        taskList.put(idEpicTask, task);
        task.setId(idEpicTask);
        System.out.println("Epic_задача создана, ID задачи: " + idEpicTask);
        for (Subtask i : subtask) {
            Integer idSubtask = generateID();
            System.out.println("Подзадача создана, ID задачи: " + idSubtask);
            taskList.put(idSubtask, i);
            task.getSubtasksList().add(i);
            i.setId(idSubtask);
            i.setEpicId(idEpicTask);
        }
    }

    @Override
    public void updateTask(CommonTask task) {
        for (Integer inventory : taskList.keySet())
            if (taskList.get(inventory) == task) {
                taskList.put(inventory, task);
            }
        for (CommonTask tool : taskList.values()) {
            if (String.valueOf(tool.getClass()).equals("class task.EpicTask")) {
                EpicTask epicTask = (EpicTask) tool;
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
        }
    }

    @Override
    public Integer generateID() {
        Random random = new Random();
        Integer newId;
        while (true) {
            newId = random.nextInt(100);
            if (!taskList.containsKey(newId))
                return newId;
        }
    }
}