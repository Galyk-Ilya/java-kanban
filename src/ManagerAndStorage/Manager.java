package ManagerAndStorage;

import task.CommonTask;
import task.EpicTask;
import task.Subtask;

import java.util.Random;

import static ManagerAndStorage.StatusType.*;

public class Manager {
    private final TaskStorage taskStorage = new TaskStorage();

    public void getAllTaskList() {
        if (taskStorage.getTaskList().size() < 1 || taskStorage.getTaskList() == null) {
            System.out.println("Список задач пуст");
        } else {
            for (CommonTask tool : taskStorage.getTaskList().values()) {
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

    public void clearTaskList() {
        taskStorage.getTaskList().clear();
        System.out.println("Все задачи удалены.");
    }

    public CommonTask getByID(Integer ID) {
        if (taskStorage.getTaskList().containsKey(ID)) {
            return taskStorage.getTaskList().get(ID);
        } else {
            System.out.println("Задача отсутстует");
            return null;
        }
    }

    public void deleteByID(Integer ID) {
        if (taskStorage.getTaskList().containsKey(ID)) {
            if (String.valueOf(taskStorage.getTaskList().get(ID).getClass()).equals("class task.EpicTask")) {
                EpicTask epicTask = (EpicTask)  taskStorage.getTaskList().get(ID);
                System.out.println(epicTask + " ID №" + ID + " удалена.");
            } else {
                System.out.println(taskStorage.getTaskList().get(ID) + " ID №" + ID + " удалена.");
            }

            taskStorage.getTaskList().remove(ID);
            for (CommonTask i : taskStorage.getTaskList().values()) {
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

    public void createATask(CommonTask task) {
        Integer id = generateID();
        taskStorage.getTaskList().put(id, task);
        task.setId(id);
        if (String.valueOf(task.getClass()).equals("class task.Subtask")) {
            System.out.println("Подзадача создана, ID задачи: " + id);
        } else if (String.valueOf(task.getClass()).equals("class task.EpicTask")) {
            System.out.println("Epic_задача создана, ID задачи: " + id);
        } else {
            System.out.println("Задача создана, ID задачи: " + id);
        }
    }

    public void createATask(EpicTask task, Subtask... subtask) {
        Integer idEpicTask = generateID();
        taskStorage.getTaskList().put(idEpicTask, task);
        task.setId(idEpicTask);
        System.out.println("Epic_задача создана, ID задачи: " + idEpicTask);
        for (Subtask i : subtask) {
            Integer idSubtask = generateID();
            System.out.println("Подзадача создана, ID задачи: " + idSubtask);
            taskStorage.getTaskList().put(idSubtask, i);
            task.getSubtasksList().add(i);
            i.setId(idSubtask);
            i.setEpicId(idEpicTask);
        }
    }

    public void updateTask(CommonTask task) {
        for (Integer inventory : taskStorage.getTaskList().keySet())
            if (taskStorage.getTaskList().get(inventory) == task) {
                taskStorage.getTaskList().put(inventory, task);
            }
        for (CommonTask tool : taskStorage.getTaskList().values()) {
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

    public Integer generateID() {
        Random random = new Random();
        Integer newId;
        while (true) {
            newId = random.nextInt(100);
            if (!taskStorage.getTaskList().containsKey(newId))
                return newId;
        }
    }
}
