package pattern;

import task.*;

import java.util.List;

public interface HistoryManager {

    void add(CommonTask task);

    List<CommonTask> getHistory();

    void remove(int id);
}
