package pattern;

import task.*;

public interface HistoryManager {

    void add(CommonTask task);

    void getHistory();

    CommonTask getTask(Integer ID);

     Subtask getSubtask(Integer ID);

    EpicTask getEpic(Integer ID);
}
