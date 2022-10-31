package pattern;

import task.CommonTask;
import task.EpicTask;
import task.Subtask;

public interface TaskManager {
    void getAllTaskList();

    void clearTaskList();

    CommonTask getByID(Integer ID);

    void deleteByID(Integer ID);

    void getSubtask(EpicTask task);

    void createATask(EpicTask epicTask, Subtask... subtask);

    void createATask(CommonTask commonTask);

    void updateTask(CommonTask task);

    Integer generateID();

}

