package managers;

import managers.impl.HttpTaskManager;
import managers.impl.InMemoryHistoryManager;

public class Managers {
//    public static TaskManager getDefault() {
//        return new FileBackedTasksManager(new File("src/main/resources/archive.txt"));
//    }

    public static TaskManager getDefault() {
        return new HttpTaskManager("http://localhost:8078");
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}