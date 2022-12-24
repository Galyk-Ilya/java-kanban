package managers.impl;

import exception.ManagerStartDatabaseException;
import managers.Managers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import task.*;

import java.io.File;

import static enums.TaskType.*;
import static org.junit.jupiter.api.Assertions.*;

public class FileBackedTasksManagerTest extends InMemoryTaskManagerTest {

    FileBackedTasksManager manager;

    @BeforeEach
    public void newManager() {
        File file = new File("src/main/resources/archive.txt");
        manager = FileBackedTasksManager.loadFromFile(file);
        taskManager = Managers.getDefault();
    }


    @Test
    void getTaskTest() {
        manager.clearTaskList();
        assertNull(manager.getTask(1));
        CommonTask task1 = manager.createATask("Test addNewTask1",
                "Test addNewTask1 description", COMMONTASK, 10);
        assertEquals(task1, manager.getTask(task1.getId()));
        assertTrue(manager.getHistory().contains(task1));
    }

    @Test
    void getEpicTest() {
        manager.clearTaskList();
        assertNull(manager.getEpic(1));
        EpicTask task1 = manager.createEpicTask("Test addNewTask1",
                "Test addNewTask1 description", EPIC_TASK, 10);
        assertEquals(task1, manager.getEpic(task1.getId()));
        assertTrue(manager.getHistory().contains(task1));
    }

    @Test
    void getSubtaskTest() {
        manager.clearTaskList();
        assertNull(manager.getSubtask(1));

        EpicTask task1 = manager.createEpicTask("Test addNewTask1",
                "Test addNewTask1 description", EPIC_TASK, 10);
        Subtask task2 = manager.createASubtask("Test addNewTask2",
                "Test addNewTask2 description", SUBTASK, task1.getId(), 10);
        assertEquals(task2, manager.getSubtask(task2.getId()));
        assertTrue(manager.getHistory().contains(task2));
    }

    @Test
    void createATaskTest() {
        manager.clearTaskList();
        assertEquals(0, manager.getTaskList().size());
        CommonTask task1 = manager.createATask("Test addNewTask1",
                "Test addNewTask1 description", COMMONTASK, 10);

        assertEquals(1, manager.getTaskList().size());
        assertNotNull(task1.getId());
        CommonTask task2 = manager.createATask("Test addNewTask2",
                "Test addNewTask2 description", COMMONTASK, 10);
        assertTrue(manager.getTaskList().containsValue(task2));

        CommonTask task3 = manager.createATask("Test addNewTask3",
                "Test addNewTask3 description", SUBTASK, 10);
        assertNull(task3);

        final ManagerStartDatabaseException exception = assertThrows(ManagerStartDatabaseException.class, () -> {
            File file = new File("неверный путь файла");
            manager = FileBackedTasksManager.loadFromFile(file);
            manager.createATask("", "", COMMONTASK, 10);
        });
        assertEquals("Error processing existing files", exception.getMessage());

    }

    @Test
    void createEpicTaskTest() {
        manager.clearTaskList();
        assertEquals(0, manager.getTaskList().size());
        EpicTask task1 = manager.createEpicTask("newTask1",
                "newTask1Description", EPIC_TASK, 10);

        assertEquals(1, manager.getTaskList().size());
        assertNotNull(task1.getId());
        EpicTask task2 = manager.createEpicTask("newTask2",
                "newTask2Description", EPIC_TASK, 10);
        assertTrue(manager.getTaskList().containsValue(task2));

        EpicTask task3 = manager.createEpicTask("newTask3",
                "newTask3Description", SUBTASK, 10);
        assertNull(task3);
    }


    @Test
    void createASubtaskTest() {
        manager.clearTaskList();
        assertEquals(0, manager.getTaskList().size());

        EpicTask task1 = manager.createEpicTask("newTask1",
                "newTask1Description", EPIC_TASK, 10);
        assertNotNull(task1.getId());

        Subtask task2 = manager.createASubtask("newTask2",
                "newTask2Description", SUBTASK, task1.getId(), 10);
        assertEquals(task1.getId(), task2.getEpicId());
        assertTrue(manager.getTaskList().containsValue(task2));
        assertTrue(task1.getSubtasksList().contains(task2));
        assertEquals(task2.getEpicId(), task1.getId());
        Subtask task3 = manager.createASubtask("newTask3",
                "newTask3Description", COMMONTASK, task1.getId(), 10);
        assertNull(task3);

        Subtask task4 = manager.createASubtask("newTask3",
                "newTask3Description", SUBTASK, 10000, 10);
        assertNull(task4);
    }


}

// Убрать запятую при записи последней истории в файл (ФайлМенеджер)

//Если останется время все таки разобраться с иерархией тестов, понять,
// и убрать лишний код из файлМенеджера
