package managers.impl;

import exception.ManagerStartDatabaseException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static enums.TaskType.COMMONTASK;
import static org.junit.jupiter.api.Assertions.*;

public class FileBackedTasksManagerTest extends TaskManagerTest<FileBackedTasksManager> {

    @BeforeEach
    public void initTasks() {
        taskManager = FileBackedTasksManager.loadFromFile(new File("src/main/resources/archive.txt"));
    }

    @Test
    public void loadFromFileTest() {
        final ManagerStartDatabaseException exception = assertThrows(ManagerStartDatabaseException.class, () -> {
            File file = new File("неверный путь файла");
            taskManager = FileBackedTasksManager.loadFromFile(file);
            taskManager.createATask("", "", COMMONTASK,
                    10, LocalDateTime.of(2000, 1, 1, 0, 0));
        });
        assertEquals("Error processing existing files", exception.getMessage());

        final ArrayIndexOutOfBoundsException exception2 = assertThrows(ArrayIndexOutOfBoundsException.class, () -> {
            File file = new File("src/main/resources/archiveTest.txt");
            taskManager = FileBackedTasksManager.loadFromFile(file);
        });
        assertEquals("Index 6 out of bounds for length 3", exception2.getMessage());
    }

    @Test
    void taskConverterTest() {
        taskManager.clearTaskList();
        FileBackedTasksManager taskManagerTest = FileBackedTasksManager.taskConverter(new ArrayList<>(), taskManager);
        assertEquals(taskManager.taskList.isEmpty(), taskManagerTest.taskList.isEmpty());

        taskManagerTest.createATask("task", "task description", COMMONTASK,
                10, LocalDateTime.of(2000, 1, 1, 0, 0));
        assertFalse(taskManagerTest.taskList.isEmpty());

        String task = "1, COMMONTASK, task, NEW, task description, -, PT10M, 01.01.2000 00:00, 01.01.2000 00:10";
        ArrayList<String> testList = new ArrayList<>(List.of(" ", task));
        assertEquals(FileBackedTasksManager.taskConverter(testList, taskManager).taskList.size(), 1);
    }
}