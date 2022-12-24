package managers;

import enums.TaskType;
import task.CommonTask;
import task.EpicTask;
import task.Subtask;

import java.util.List;
import java.util.Map;
import java.util.TreeSet;

public interface TaskManager {

    void displayTaskList();

    void clearTaskList();

    void deleteById(int id);

    List<Subtask> getListSubtasks(EpicTask task);

    EpicTask createEpicTask(String nameTask, String description, TaskType type, int duration);

    CommonTask createATask(String nameTask, String description, TaskType type, int duration);

    Subtask createASubtask(String nameTask, String description, TaskType type, int epicId, int duration);

    void updateTask(CommonTask task);

    CommonTask getTask(int id);

    EpicTask getEpic(int id);

    Subtask getSubtask(int id);

    List<CommonTask> getHistory();

    Integer generateID();

    Map<Integer, CommonTask> getTaskList();

    TreeSet<CommonTask> getPrioritizedTasks();

}