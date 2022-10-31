package memoryManagers;

import pattern.HistoryManager;
import pattern.Managers;
import task.CommonTask;
import task.EpicTask;
import task.Subtask;

import java.util.ArrayList;

public class InMemoryHistoryManager implements HistoryManager {
    private final ArrayList<CommonTask> browsingHistory = new ArrayList<>();
    private final InMemoryTaskManager taskManager = (InMemoryTaskManager) Managers.getDefault();
// попытался реализовать класс Managers, честно говоря не до конца понял суть задачи, почитал про "Паттерн Factory"
// наша цель - автоматическое создание объекта с помощью метода getDefault() или тут какая-то иная доп. задача?)

    @Override
    public void add(CommonTask task) {
        browsingHistory.add(task);
    }

    @Override
    public ArrayList<CommonTask> getHistory() {
        ArrayList<CommonTask> resultHistory = new ArrayList<>();
        System.out.println("История просмотренных задач:");
        if (browsingHistory.size() >= 10) {
            for (int i = browsingHistory.size() - 10; i < browsingHistory.size(); i++) {
                System.out.println(browsingHistory.get(i).getName());
                resultHistory.add(browsingHistory.get(i));
            }
            return resultHistory;
        } else {
            System.out.println(browsingHistory);
            return browsingHistory;
        }
    }

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