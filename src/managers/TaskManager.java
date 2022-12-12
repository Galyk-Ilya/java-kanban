package managers;

import enums.TaskType;
import task.*;

import java.util.List;
import java.util.Map;

public interface TaskManager {

    void getAllTaskList();

    void clearTaskList();

    CommonTask getById(Integer id);

    void deleteById(int id);

    List<Subtask> getListSubtasks(EpicTask task);

    EpicTask createEpicTask(String nameTask, String description, TaskType type);

    CommonTask createATask(String nameTask, String description, TaskType type);

    Subtask createASubtask(String nameTask, String description, TaskType type, int epicId);

    void updateTask(CommonTask task);

    CommonTask getTask(int id);

    EpicTask getEpic(int id);

    Subtask getSubtask(int id);

    List<CommonTask> getHistory();

    Integer generateID();

    Map<Integer, CommonTask> getTaskList();

}