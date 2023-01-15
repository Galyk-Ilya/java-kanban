package managers.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import task.*;

import java.time.LocalDateTime;

import static enums.StatusType.NEW;
import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {

    @BeforeEach
    public void initTasks() {
        taskManager = new InMemoryTaskManager();
    }

    @Test
    void createDeadlineTest() {
        taskManager.clearTaskList();
        CommonTask task1 = new CommonTask(1, "Task1", NEW, "description1", 10,
                LocalDateTime.of(2000, 1, 1, 0, 0));
        taskManager.getTaskList().put(1, task1);
        EpicTask epicTask = new EpicTask(2, "Epic1", NEW, "description1", 10,
                LocalDateTime.of(2000, 1, 2, 0, 0));
        taskManager.getTaskList().put(2, epicTask);
        Subtask subtask1 = new Subtask(3, "Subtask1", NEW, "description1", 10,
                LocalDateTime.of(2000, 1, 2, 0, 10));
        taskManager.getTaskList().put(3, subtask1);
        subtask1.setEpicId(2);
        Subtask subtask2 = new Subtask(4, "Subtask2", NEW, "description1", 10,
                LocalDateTime.of(2000, 1, 2, 0, 11));
        subtask2.setEpicId(2);
        taskManager.getTaskList().put(4, subtask2);

        taskManager.createDeadline(task1, task1.getStartTime());
        assertTrue(taskManager.getPrioritizedTasks().contains(task1));

        taskManager.createDeadline(epicTask, epicTask.getStartTime());
        epicTask.setEndTime(epicTask.calculateEndTime());
        assertEquals(epicTask.getEndTime(), LocalDateTime.of(2000, 1, 2, 0, 10));

        taskManager.createDeadline(subtask1, subtask1.getStartTime());
        epicTask.getSubtasksList().add(subtask1);
        assertEquals(epicTask.getEndTime(), subtask1.calculateEndTime());
        assertEquals(taskManager.getPrioritizedTasks().size(), 2);

        taskManager.createDeadline(subtask2, subtask2.getStartTime());
        assertEquals(subtask2.calculateEndTime(), LocalDateTime.of(2000, 1, 2, 0, 30, 1));
        assertEquals(epicTask.getStartTime(), subtask1.getStartTime());
        assertEquals(epicTask.getEndTime(), subtask2.calculateEndTime());
    }
}