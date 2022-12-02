package pattern;

import task.*;

import java.util.List;

public interface HistoryManager {

    void addLast(CommonTask task);

    List<CommonTask> getHistory();

    void remove(int id);
}
