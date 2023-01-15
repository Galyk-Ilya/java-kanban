package managers.impl;

import managers.TaskManager;
import org.junit.jupiter.api.Test;
import task.CommonTask;
import task.EpicTask;
import task.Subtask;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static enums.StatusType.*;
import static enums.TaskType.*;
import static org.junit.jupiter.api.Assertions.*;

public abstract class TaskManagerTest<T extends TaskManager> {

    protected T taskManager;

    @Test
    void clearTaskListTest() {
        taskManager.clearTaskList();
        assertTrue(taskManager.getTaskList().isEmpty());
        taskManager.createEpicTask("Test addNewTask1", "Test addNewTask1 description",
                EPIC_TASK, 10, LocalDateTime.of(2000, 1, 1, 0, 0));
        assertFalse(taskManager.getTaskList().isEmpty());
        taskManager.clearTaskList();
        assertTrue(taskManager.getTaskList().isEmpty());
    }

    @Test
    void deleteByIdTest() {
        taskManager.clearTaskList();
        taskManager.deleteById(1000);
        CommonTask task2 = taskManager.createATask("Test addNewTask2",
                "Test addNewTask2 description", COMMONTASK, 10,
                LocalDateTime.of(2000, 1, 1, 0, 0));
        assertFalse(taskManager.getTaskList().isEmpty());
        taskManager.deleteById(task2.getId());
        assertTrue(taskManager.getTaskList().isEmpty());
        EpicTask epicTask1 = taskManager.createEpicTask("Test addNewTask1",
                "Test addNewTask1 description", EPIC_TASK, 10,
                LocalDateTime.of(2000, 1, 1, 0, 0));
        Subtask subtask1 = taskManager.createASubtask("Subtask1", "Subtask1 description",
                SUBTASK, epicTask1.getId(), 10,
                LocalDateTime.of(2000, 1, 2, 0, 0));
        taskManager.createASubtask("Subtask2", "Subtask2 description",
                SUBTASK, epicTask1.getId(), 10,
                LocalDateTime.of(2000, 1, 3, 0, 0));
        taskManager.deleteById(subtask1.getId());
        taskManager.deleteById(epicTask1.getId());
        assertTrue(taskManager.getTaskList().isEmpty());
    }

    @Test
    void getListSubtasksTest() {
        EpicTask epicTask = null;
        assertNull(taskManager.getListSubtasks(epicTask));
        EpicTask epicTask1 = taskManager.createEpicTask(" ", " ", EPIC_TASK, 10,
                LocalDateTime.of(2000, 1, 1, 0, 0));
        assertEquals(new ArrayList<>(), taskManager.getListSubtasks(epicTask1));
        Subtask subtask1 = taskManager.createASubtask("Subtask1", "Subtask1 description",
                SUBTASK, epicTask1.getId(), 10,
                LocalDateTime.of(2000, 1, 2, 0, 0));
        List<Subtask> testList = new ArrayList<>();
        testList.add(subtask1);
        assertEquals(testList, taskManager.getListSubtasks(epicTask1));

        Subtask subtask2 = taskManager.createASubtask("Subtask2", "Subtask2 description",
                SUBTASK, 1000, 10,
                LocalDateTime.of(2000, 1, 3, 0, 0));
        assertNull(subtask2);
    }

    @Test
    void updateTaskTest() {
        EpicTask epicTask = taskManager.createEpicTask("EpicTask", "EpicTask description",
                EPIC_TASK, 10, LocalDateTime.of(2000, 1, 1, 0, 0));
        assertTrue(epicTask.getSubtasksList().isEmpty());
        assertEquals(epicTask.getStatus(), NEW);

        Subtask subtask1 = taskManager.createASubtask("Subtask1", "Subtask1 description",
                SUBTASK, epicTask.getId(), 10,
                LocalDateTime.of(2000, 1, 2, 0, 0));
        Subtask subtask2 = taskManager.createASubtask("Subtask2", "Subtask2 description",
                SUBTASK, epicTask.getId(), 10,
                LocalDateTime.of(2000, 1, 3, 0, 0));
        assertEquals(subtask1.getStatus(), NEW);
        assertEquals(subtask2.getStatus(), NEW);
        assertEquals(epicTask.getStatus(), NEW);

        subtask1.setStatus(DONE);
        subtask2.setStatus(DONE);
        taskManager.updateTask(subtask1);
        taskManager.updateTask(subtask2);
        assertEquals(epicTask.getStatus(), DONE);

        subtask1.setStatus(NEW);
        taskManager.updateTask(subtask1);
        assertEquals(epicTask.getStatus(), IN_PROGRESS);

        subtask1.setStatus(IN_PROGRESS);
        subtask2.setStatus(IN_PROGRESS);
        taskManager.updateTask(subtask1);
        taskManager.updateTask(subtask2);
        assertEquals(epicTask.getStatus(), IN_PROGRESS);
        taskManager.deleteById(subtask1.getId());
        taskManager.deleteById(subtask2.getId());
        assertEquals(epicTask.getStatus(), NEW);
    }

    @Test
    void getHistoryTest() {
        assertEquals(new ArrayList<CommonTask>(), taskManager.getHistory());
    }

    @Test
    void getTaskListTest() {
        taskManager.clearTaskList();
        Map<Integer, CommonTask> testTaskList = new HashMap<>();
        assertEquals(testTaskList, taskManager.getTaskList());
        testTaskList.put(1, taskManager.createATask("task", "task description", COMMONTASK,
                10, LocalDateTime.of(2000, 1, 3, 0, 0)));
        assertEquals(taskManager.getTaskList(), testTaskList);
    }

    @Test
    void getTaskTest() {
        taskManager.clearTaskList();
        assertNull(taskManager.getTask(1));
        CommonTask task1 = taskManager.createATask("Test addNewTask1",
                "Test addNewTask1 description", COMMONTASK, 10,
                LocalDateTime.of(2000, 1, 1, 0, 0));
        assertEquals(task1, taskManager.getTask(task1.getId()));
        assertTrue(taskManager.getHistory().contains(task1));
    }


    @Test
    void getEpicTest() {
        taskManager.clearTaskList();
        assertNull(taskManager.getEpic(1));
        EpicTask task1 = taskManager.createEpicTask("Test addNewTask1",
                "Test addNewTask1 description", EPIC_TASK, 10,
                LocalDateTime.of(2000, 1, 1, 0, 0));
        assertEquals(task1, taskManager.getEpic(task1.getId()));
        assertTrue(taskManager.getHistory().contains(task1));
    }

    @Test
    void getSubtaskTest() {
        taskManager.clearTaskList();
        assertNull(taskManager.getSubtask(1));

        EpicTask task1 = taskManager.createEpicTask("Test addNewTask1",
                "Test addNewTask1 description", EPIC_TASK, 10,
                LocalDateTime.of(2000, 1, 1, 0, 0));
        Subtask task2 = taskManager.createASubtask("Test addNewTask2",
                "Test addNewTask2 description", SUBTASK, task1.getId(), 10,
                LocalDateTime.of(2000, 1, 2, 0, 0));
        assertEquals(task2, taskManager.getSubtask(task2.getId()));
        assertTrue(taskManager.getHistory().contains(task2));
    }

    @Test
    void createEpicTaskTest() {
        taskManager.clearTaskList();
        assertEquals(0, taskManager.getTaskList().size());
        EpicTask task1 = taskManager.createEpicTask("newTask1",
                "newTask1Description", EPIC_TASK, 10,
                LocalDateTime.of(2000, 1, 1, 0, 0));

        assertEquals(1, taskManager.getTaskList().size());
        assertNotNull(task1.getId());
        EpicTask task2 = taskManager.createEpicTask("newTask2",
                "newTask2Description", EPIC_TASK, 10,
                LocalDateTime.of(2000, 1, 2, 0, 0));
        assertTrue(taskManager.getTaskList().containsValue(task2));

        EpicTask task3 = taskManager.createEpicTask("newTask3",
                "newTask3Description", SUBTASK, 10,
                LocalDateTime.of(2000, 1, 3, 0, 0));
        assertNull(task3);
    }

    @Test
    void createATaskTest() {
        taskManager.clearTaskList();
        assertEquals(0, taskManager.getTaskList().size());
        CommonTask task1 = taskManager.createATask("Test addNewTask1",
                "Test addNewTask1 description", COMMONTASK, 10,
                LocalDateTime.of(2000, 1, 1, 0, 0));

        assertEquals(1, taskManager.getTaskList().size());
        assertNotNull(task1.getId());
        CommonTask task2 = taskManager.createATask("Test addNewTask2",
                "Test addNewTask2 description", COMMONTASK, 10,
                LocalDateTime.of(2000, 1, 2, 0, 0));
        assertTrue(taskManager.getTaskList().containsValue(task2));

        CommonTask task3 = taskManager.createATask("Test addNewTask3",
                "Test addNewTask3 description", SUBTASK, 10,
                LocalDateTime.of(2000, 1, 3, 0, 0));
        assertNull(task3);
    }

    @Test
    void createASubtaskTest() {
        taskManager.clearTaskList();
        assertEquals(0, taskManager.getTaskList().size());

        EpicTask task1 = taskManager.createEpicTask("newTask1",
                "newTask1Description", EPIC_TASK, 10,
                LocalDateTime.of(2000, 1, 1, 0, 0));
        assertNotNull(task1.getId());

        Subtask task2 = taskManager.createASubtask("newTask2",
                "newTask2Description", SUBTASK, task1.getId(), 10,
                LocalDateTime.of(2000, 1, 2, 0, 0));
        assertEquals(task1.getId(), task2.getEpicId());
        assertTrue(taskManager.getTaskList().containsValue(task2));
        assertTrue(task1.getSubtasksList().contains(task2));
        assertEquals(task2.getEpicId(), task1.getId());
        Subtask task3 = taskManager.createASubtask("newTask3",
                "newTask3Description", COMMONTASK, task1.getId(), 10,
                LocalDateTime.of(2000, 1, 3, 0, 0));
        assertNull(task3);

        Subtask task4 = taskManager.createASubtask("newTask3",
                "newTask3Description", SUBTASK, 10000, 10,
                LocalDateTime.of(2000, 1, 4, 0, 0));
        assertNull(task4);
    }

    @Test
    void getPrioritizedTasksTest() { // пересечения + класс TreeSet + время
        taskManager.clearTaskList();
        taskManager.createATask("1", "1", COMMONTASK, 10,
                LocalDateTime.of(2000, 1, 1, 0, 0));
        taskManager.createATask("2", "2", COMMONTASK, 10,
                LocalDateTime.of(2000, 1, 2, 0, 0));
        EpicTask task3 = taskManager.createEpicTask("3", "3", EPIC_TASK, 10,
                LocalDateTime.of(2000, 1, 3, 0, 0));
        Subtask task4 = taskManager.createASubtask("4", "4", SUBTASK, task3.getId(), 10,
                LocalDateTime.of(2000, 1, 4, 0, 0));
        taskManager.createASubtask("5", "5", SUBTASK, task3.getId(), 10,
                LocalDateTime.of(2000, 1, 5, 0, 0));

        CommonTask commonTaskTest = null;
        taskManager.deleteById(task3.getId());
        taskManager.deleteById(task4.getId());
        for (CommonTask commonTask : taskManager.getPrioritizedTasks()) {
            if (commonTaskTest == null) {
                commonTaskTest = commonTask;
                continue;
            }
            System.out.println(commonTask.getStartTime() + "\n по " + commonTaskTest.getStartTime());
            assertTrue(commonTaskTest.getStartTime().isBefore(commonTask.getStartTime()));
            Duration duration = Duration.between(commonTaskTest.getStartTime(), commonTask.getStartTime());
            assertTrue(duration.toMinutes() >= commonTaskTest.getDuration().toMinutes());
            commonTaskTest = commonTask;
        }
    }
}