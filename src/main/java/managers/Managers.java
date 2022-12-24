package managers;

import managers.impl.FileBackedTasksManager;
import managers.impl.InMemoryHistoryManager;

import java.io.File;

public class Managers {
    public static TaskManager getDefault() {
        return new FileBackedTasksManager(new File("src/main/resources/archive.txt"));
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}