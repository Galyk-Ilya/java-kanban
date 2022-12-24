package managers.impl;

import managers.Managers;
import managers.TaskManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import task.*;

import java.time.Duration;
import java.util.*;

import static enums.StatusType.*;
import static enums.TaskType.*;
import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest extends TaskManagerTest {

    TaskManager taskManager;

    @BeforeEach
    public void newManager() {
        taskManager = Managers.getDefault();
    }

    @Test
    void getTaskTest() {
        assertNull(taskManager.getTask(1));

        CommonTask task1 = taskManager.createATask("Test addNewTask1",
                "Test addNewTASK , 101 description", COMMONTASK, 10);
        assertEquals(task1, taskManager.getTask(task1.getId()));
        assertTrue(taskManager.getHistory().contains(task1));
    }

    @Test
    void getEpicTest() {
        assertNull(taskManager.getEpic(1));

        EpicTask task1 = taskManager.createEpicTask("Test addNewTask1",
                "Test addNewTask1 description", EPIC_TASK, 10);
        assertEquals(task1, taskManager.getEpic(task1.getId()));
        assertTrue(taskManager.getHistory().contains(task1));
    }

    @Test
    void getSubtaskTest() {
        assertNull(taskManager.getSubtask(1));

        EpicTask task1 = taskManager.createEpicTask("Test addNewTask1",
                "Test addNewTask1 description", EPIC_TASK, 10);
        Subtask task2 = taskManager.createASubtask("Test addNewTask2",
                "Test addNewTask2 description", SUBTASK, task1.getId(), 10);
        assertEquals(task2, taskManager.getSubtask(task2.getId()));
        assertTrue(taskManager.getHistory().contains(task2));
    }

    @Test
    void createEpicTaskTest() {
        assertEquals(0, taskManager.getTaskList().size());
        EpicTask task1 = taskManager.createEpicTask("Test addNewTask1",
                "Test addNewTask1 description", EPIC_TASK, 10);

        assertEquals(1, taskManager.getTaskList().size());
        assertNotNull(task1.getId());
        EpicTask task2 = taskManager.createEpicTask("Test addNewTask2",
                "Test addNewTask2 description", EPIC_TASK, 10);
        assertTrue(taskManager.getTaskList().containsValue(task2));

        EpicTask task3 = taskManager.createEpicTask("Test addNewTask3",
                "Test addNewTask3 description", SUBTASK, 10);
        assertNull(task3);
    }

    @Test
    void createATaskTest() {
        assertEquals(0, taskManager.getTaskList().size());
        CommonTask task1 = taskManager.createATask("Test addNewTask1",
                "Test addNewTask1 description", COMMONTASK, 10);

        assertEquals(1, taskManager.getTaskList().size());
        assertNotNull(task1.getId());
        CommonTask task2 = taskManager.createATask("Test addNewTask2",
                "Test addNewTask2 description", COMMONTASK, 10);
        assertTrue(taskManager.getTaskList().containsValue(task2));
        System.out.println(task1.getStartTime() + "\n" + task2.getStartTime());
        CommonTask task3 = taskManager.createATask("Test addNewTask3",
                "Test addNewTask3 description", SUBTASK, 10);
        assertNull(task3);

    }

    @Test
    void createASubtaskTest() {
        assertEquals(0, taskManager.getTaskList().size());

        EpicTask task1 = taskManager.createEpicTask("Test addNewTask1",
                "Test addNewTask1 description", EPIC_TASK, 10);
        assertNotNull(task1.getId());

        Subtask task2 = taskManager.createASubtask("Test addNewTask2",
                "Test addNewTask2 description", SUBTASK, task1.getId(), 10);
        assertEquals(task1.getId(), task2.getEpicId());
        assertTrue(taskManager.getTaskList().containsValue(task2));
        assertTrue(task1.getSubtasksList().contains(task2));
        assertEquals(task2.getEpicId(), task1.getId());
        Subtask task3 = taskManager.createASubtask("Test addNewTask3",
                "Test addNewTask3 description", COMMONTASK, task1.getId(), 10);
        assertNull(task3);

        Subtask task4 = taskManager.createASubtask("Test addNewTask3",
                "Test addNewTask3 description", SUBTASK, 10000, 10);
        assertNull(task4);
    }

    @Test
    void displayTaskListTest() {
        taskManager.displayTaskList();
        EpicTask epicTask = taskManager.createEpicTask("", "", EPIC_TASK, 10);
        taskManager.displayTaskList();
        taskManager.createATask("", "", COMMONTASK, 10);
        taskManager.createASubtask("", "", SUBTASK, epicTask.getId(), 10);
        taskManager.displayTaskList();
    }

    @Test
    void clearTaskListTest() {
        taskManager.clearTaskList();
        assertTrue(taskManager.getTaskList().isEmpty());
        taskManager.createEpicTask("Test addNewTask1", "Test addNewTask1 description", EPIC_TASK, 10);
        assertFalse(taskManager.getTaskList().isEmpty());
        taskManager.clearTaskList();
        assertTrue(taskManager.getTaskList().isEmpty());
    }

    @Test
    void deleteByIdTest() {
        taskManager.deleteById(1000);
        CommonTask task2 = taskManager.createATask("Test addNewTask2",
                "Test addNewTask2 description", COMMONTASK, 10);
        assertFalse(taskManager.getTaskList().isEmpty());
        taskManager.deleteById(task2.getId());
        assertTrue(taskManager.getTaskList().isEmpty());
        EpicTask epicTask1 = taskManager.createEpicTask("Test addNewTask1", "Test addNewTask1 description", EPIC_TASK, 10);
        Subtask subtask1 = taskManager.createASubtask("Subtask1", "Subtask1 description",
                SUBTASK, epicTask1.getId(), 10);
        Subtask subtask2 = taskManager.createASubtask("Subtask2", "Subtask2 description",
                SUBTASK, epicTask1.getId(), 10);
        taskManager.deleteById(subtask1.getId());
        taskManager.deleteById(epicTask1.getId());
        assertTrue(taskManager.getTaskList().isEmpty());
    }

    @Test
    void getListSubtasksTest() {
        EpicTask epicTask = null;
        assertNull(taskManager.getListSubtasks(epicTask));
        EpicTask epicTask1 = taskManager.createEpicTask(" ", " ", EPIC_TASK, 10);
        assertEquals(new ArrayList<>(), taskManager.getListSubtasks(epicTask1));
        Subtask subtask1 = taskManager.createASubtask("Subtask1", "Subtask1 description",
                SUBTASK, epicTask1.getId(), 10);
        List<Subtask> testList = new ArrayList<>();
        testList.add(subtask1);
        assertEquals(testList, taskManager.getListSubtasks(epicTask1));

        Subtask subtask2 = taskManager.createASubtask("Subtask2", "Subtask2 description",
                SUBTASK, 1000, 10);
        assertNull(subtask2);
    }

    @Test
    void updateTaskTest() {
        EpicTask epicTask = taskManager.createEpicTask("EpicTask", "EpicTask description", EPIC_TASK, 10);
        assertTrue(epicTask.getSubtasksList().isEmpty());
        assertEquals(epicTask.getStatus(), NEW);

        Subtask subtask1 = taskManager.createASubtask("Subtask1", "Subtask1 description",
                SUBTASK, epicTask.getId(), 10);
        Subtask subtask2 = taskManager.createASubtask("Subtask2", "Subtask2 description",
                SUBTASK, epicTask.getId(), 10);
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
    void generateIDTest() {
        assertEquals(1, taskManager.generateID());
        assertEquals(1, taskManager.createATask("task", "task description", COMMONTASK, 10).getId());
        assertEquals(2, taskManager.generateID());
    }

    @Test
    void getTaskListTest() {
        Map<Integer, CommonTask> testTaskList = new HashMap<>();
        assertEquals(testTaskList, taskManager.getTaskList());
        testTaskList.put(1, taskManager.createATask("task", "task description", COMMONTASK, 10));
        assertEquals(taskManager.getTaskList(), testTaskList);
    }

    @Test
    void getPrioritizedTasksTest() { // пересечения + класс TreeSet + время
        taskManager.createATask("1", "1", COMMONTASK, 10);
        taskManager.createATask("2", "2", COMMONTASK, 10);
        EpicTask task3 = taskManager.createEpicTask("3", "3", EPIC_TASK, 10);
        Subtask task4 = taskManager.createASubtask("4", "4", SUBTASK, task3.getId(), 10);
        taskManager.createASubtask("5", "5", SUBTASK, task3.getId(), 10);
        CommonTask commonTaskTest = null;
        taskManager.deleteById(task4.getId());
        for (CommonTask commonTask : taskManager.getPrioritizedTasks()) {
            System.out.println(commonTask.getStartTime() + "\n Конец" + commonTask.getEndTime());
            System.out.println(commonTask);
            if (commonTaskTest == null) {
                commonTaskTest = commonTask;
                continue;
            }
            assertTrue(commonTaskTest.getStartTime().isBefore(commonTask.getStartTime()));
            Duration duration = Duration.between(commonTaskTest.getStartTime(), commonTask.getStartTime());
            System.out.println(duration.toMinutes() + "   " + commonTaskTest.getDuration().toMinutes());
            assertTrue(duration.toMinutes() >= commonTaskTest.getDuration().toMinutes());
            commonTaskTest = commonTask;
        }
    }
}