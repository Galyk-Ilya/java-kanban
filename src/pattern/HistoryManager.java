package pattern;

import memoryManagers.InMemoryHistoryManager;
import task.CommonTask;

import java.util.HashMap;
import java.util.List;

public interface HistoryManager {

    void addLast(CommonTask task);

    List<CommonTask> getHistory();

    void remove(int id);

    public HashMap<Integer, InMemoryHistoryManager.Node> getNodeMap();
}