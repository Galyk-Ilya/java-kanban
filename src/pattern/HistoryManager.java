package pattern;

import task.CommonTask;

import java.util.ArrayList;

public interface HistoryManager {
    void add(CommonTask task);
    ArrayList<CommonTask> getHistory();
}
