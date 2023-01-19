package managers.impl;

import exception.ManagerLoadException;
import managers.Managers;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import server.KVServer;
import task.CommonTask;
import task.EpicTask;
import task.Subtask;

import java.io.IOException;
import java.time.LocalDateTime;

import static enums.TaskType.*;
import static org.junit.jupiter.api.Assertions.*;

public class HttpTaskManagerTest extends TaskManagerTest<HttpTaskManager> {

    KVServer kvServer;

    @BeforeEach
    public void createServers() {
        try {
            if (kvServer == null) {
                kvServer = new KVServer();
                kvServer.start();
            }
        } catch (IOException e) {
            throw new ManagerLoadException("Ошибка запуска");
        }
        taskManager = (HttpTaskManager) Managers.getDefault();
        taskManager.createATask("CommonTask1", "description", COMMONTASK,
                10, LocalDateTime.of(2000, 10, 20, 10, 20));
        taskManager.createATask("CommonTask2", "description", COMMONTASK,
                10, LocalDateTime.of(2000, 10, 21, 10, 20));
        taskManager.createATask("CommonTask3", "description", COMMONTASK,
                10, LocalDateTime.of(2000, 10, 21, 10, 20));

        EpicTask epicTask1 = taskManager.createEpicTask("EpicTask1", "description", EPIC_TASK,
                10, LocalDateTime.of(2000, 10, 22, 10, 20));

        taskManager.createASubtask("Subtask1", "description", SUBTASK, epicTask1.getId(),
                10, LocalDateTime.of(2000, 10, 22, 10, 20));
        taskManager.createASubtask("Subtask2", "description", SUBTASK, epicTask1.getId(),
                10, LocalDateTime.of(2000, 10, 23, 10, 20));
    }

    @AfterEach
    public void stopServer() {
        kvServer.stop();
    }

    @Test
    void clearTaskListTest() {
        assertEquals(6, taskManager.taskList.size());
        System.out.println(taskManager.getPrioritizedTasks());
        assertEquals(5, taskManager.prioritizedTasks.size());

        taskManager.getTask(1);
        assertEquals(1, taskManager.getDefaultHistory.getHistory().size());
        taskManager.clearTaskList();
        assertEquals(0, taskManager.taskList.size());
        assertEquals(0, taskManager.prioritizedTasks.size());
        assertEquals(0, taskManager.getDefaultHistory.getHistory().size());
    }

    @Test
    void deleteByIdTest() {
        assertEquals("CommonTask1", taskManager.taskList.get(1).getName());
        assertEquals(5, taskManager.prioritizedTasks.size());
        taskManager.getTask(1);
        assertEquals(1, taskManager.getDefaultHistory.getHistory().size());
        taskManager.deleteById(1);
        assertFalse(taskManager.taskList.containsKey(1));
        assertEquals(0, taskManager.getDefaultHistory.getHistory().size());
        assertEquals(4, taskManager.prioritizedTasks.size());
    }

    @Test
    void getListSubtasksTest() {
        EpicTask epicTask1 = null;
        assertNull(taskManager.getListSubtasks(epicTask1));
        EpicTask epicTask2 = (EpicTask) taskManager.taskList.get(4);
        assertEquals(2, epicTask2.getSubtasksList().size());
        assertEquals("Subtask1", epicTask2.getSubtasksList().get(0).getName());
        taskManager.deleteById(5);
        assertEquals("Subtask2", epicTask2.getSubtasksList().get(0).getName());
    }

    @Test
    void getTaskListTest() {
        assertEquals(6, taskManager.getTaskList().size());
        CommonTask commonTask = taskManager.createATask("CommonTask1 TEST", "description", COMMONTASK,
                10, LocalDateTime.of(1, 10, 20, 10, 20));
        assertEquals(commonTask, taskManager.taskList.get(7));
        assertEquals(7, taskManager.taskList.size());
    }

    @Test
    void createATaskTest() {
        assertEquals(6, taskManager.taskList.size());
        CommonTask taskTest1 = taskManager.createATask("Test addNewTask1",
                "Test addNewTask1 description", COMMONTASK, 10,
                LocalDateTime.of(2000, 1, 1, 0, 0));
        assertEquals(7, taskManager.taskList.size());
        assertTrue(taskManager.taskList.containsValue(taskTest1));
        assertEquals(7, taskManager.taskList.get(7).getId());
        CommonTask taskTest2 = taskManager.createATask("Test addNewTask1",
                "Test addNewTask1 description", SUBTASK, 10,
                LocalDateTime.of(2000, 1, 1, 0, 0));
        assertNull(taskTest2);
    }

    @Test
    void createEpicTaskTest() {
        assertEquals(6, taskManager.taskList.size());
        CommonTask taskTest1 = taskManager.createEpicTask("Test addNewTask1",
                "Test addNewTask1 description", EPIC_TASK, 10,
                LocalDateTime.of(2000, 1, 1, 0, 0));
        assertEquals(7, taskManager.taskList.size());
        assertTrue(taskManager.taskList.containsValue(taskTest1));
        assertEquals(7, taskManager.taskList.get(7).getId());
        CommonTask taskTest2 = taskManager.createEpicTask("Test addNewTask1",
                "Test addNewTask1 description", COMMONTASK, 10,
                LocalDateTime.of(2000, 1, 1, 0, 0));
        assertNull(taskTest2);
    }

    @Test
    void createASubtaskTest() {
        assertEquals(6, taskManager.taskList.size());
        CommonTask taskTest1 = taskManager.createASubtask("Test addNewTask1",
                "Test addNewTask1 description", SUBTASK,4, 10,
                LocalDateTime.of(2000, 1, 1, 0, 0));
        assertEquals(7, taskManager.taskList.size());
        assertTrue(taskManager.taskList.containsValue(taskTest1));
        assertEquals(7, taskManager.taskList.get(7).getId());
        CommonTask taskTest2 = taskManager.createASubtask("Test addNewTask1",
                "Test addNewTask1 description", COMMONTASK, 4, 10,
                LocalDateTime.of(2000, 1, 1, 0, 0));
        CommonTask taskTest3 = taskManager.createASubtask("Test addNewTask1",
                "Test addNewTask1 description", COMMONTASK, 100, 10,
                LocalDateTime.of(2000, 1, 1, 0, 0));
        assertNull(taskTest2);
        assertNull(taskTest3);
    }

    @Test
    void getTaskTest() {
        assertNull(taskManager.getTask(100));
        CommonTask task = taskManager.getTask(1);
        assertEquals(1, task.getId());
        assertEquals("CommonTask1", task.getName());
        assertTrue(taskManager.getDefaultHistory.getHistory().contains(task));

    }

    @Test
    void getEpicTest() {
        assertNull(taskManager.getEpic(100));
        EpicTask epicTask = taskManager.getEpic(4);
        assertEquals(4, epicTask.getId());
        assertEquals("EpicTask1", epicTask.getName());
        assertEquals(2, epicTask.getSubtasksList().size());
        assertTrue(taskManager.getDefaultHistory.getHistory().contains(epicTask));
    }

    @Test
    void getSubtaskTest() {
        assertNull(taskManager.getSubtask(100));
        Subtask subtask = taskManager.getSubtask(5);
        assertEquals(5, subtask.getId());
        assertEquals("Subtask1", subtask.getName());
        assertEquals(subtask.getEpicId(), taskManager.taskList.get(4).getId());
        assertTrue(taskManager.getDefaultHistory.getHistory().contains(subtask));
    }
}
