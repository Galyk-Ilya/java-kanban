package memoryManagers;

import pattern.*;
import task.*;

import java.util.ArrayList;

public class InMemoryHistoryManager implements HistoryManager {
    private final ArrayList<CommonTask> browsingHistory = new ArrayList<>();
    private final TaskManager taskManager = Managers.getDefault();

    @Override
    public void add(CommonTask task) {
        browsingHistory.add(task);
    }

    @Override
    public void getHistory() {
        ArrayList<CommonTask> resultHistory = new ArrayList<>();
        System.out.println("История просмотренных задач:");
        if (browsingHistory.size() >= 10) {
            for (int i = browsingHistory.size() - 10; i < browsingHistory.size(); i++) {
                System.out.println(browsingHistory.get(i).getName());
                resultHistory.add(browsingHistory.get(i));
            }
        } else {
            for( CommonTask i : browsingHistory){
                System.out.println(i.getName());
            }
        }
    }

    @Override
    public CommonTask getTask(Integer ID) {
        if (taskManager.getTaskList().containsKey(ID)) {
            browsingHistory.add(taskManager.getTaskList().get(ID));
            taskManager.getTaskList().get(ID).setTaskReviewed(true);
            System.out.println(taskManager.getTaskList().get(ID));
            return taskManager.getTaskList().get(ID);
        } else {
            System.out.println("Задача отсутстует");
            return null;
        }
    }

    @Override
    public Subtask getSubtask(Integer ID) {
        if (taskManager.getTaskList().containsKey(ID)) {
            browsingHistory.add(taskManager.getTaskList().get(ID));
            taskManager.getTaskList().get(ID).setTaskReviewed(true);
            System.out.println(taskManager.getTaskList().get(ID));
            return (Subtask) taskManager.getTaskList().get(ID);
        } else {
            System.out.println("Задача отсутстует");
            return null;
        }
    }

    @Override
    public EpicTask getEpic(Integer ID) {
        if (taskManager.getTaskList().containsKey(ID)) {
            browsingHistory.add(taskManager.getTaskList().get(ID));
            taskManager.getTaskList().get(ID).setTaskReviewed(true);
            System.out.println(taskManager.getTaskList().get(ID));
            return (EpicTask) taskManager.getTaskList().get(ID);
        } else {
            System.out.println("Задача отсутстует");
            return null;
        }
    }
}