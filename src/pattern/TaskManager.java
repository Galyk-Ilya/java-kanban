package pattern;

import task.CommonTask;
import task.EpicTask;
import task.Subtask;

import java.util.List;
import java.util.Map;

public interface TaskManager {

    void getAllTaskList();

    void clearTaskList();

    CommonTask getByID(Integer ID);

    void deleteByID(Integer ID);

    List<Subtask> getListSubtasks(EpicTask task);

    EpicTask createEpicTask(EpicTask task);

    CommonTask createATask(CommonTask commonTask);

    EpicTask createEpicAndSubtask(EpicTask task, Subtask... subtask);

    void updateTask(CommonTask task);

    List<CommonTask> getHistory();

    Integer generateID();

    Map<Integer, CommonTask> getTaskList();

    CommonTask getTask(Integer ID);

    EpicTask getEpic(Integer ID);

    Subtask getSubtask(Integer ID);
}