package managers.impl;

import managers.TaskManager;
import org.junit.jupiter.api.Test;

public abstract class TaskManagerTest<T extends TaskManager> {
    //Не до конца понял из тз по поводу иерархии,
    // буду рад комментариям по поводу того что именно ждали по ней)

    protected T taskManager;
    @Test
    abstract void displayTaskListTest();

    @Test
    abstract void clearTaskListTest();

    @Test
    abstract void deleteByIdTest();

    @Test
    abstract void getListSubtasksTest();

    @Test
    abstract void updateTaskTest();

    @Test
    abstract void getHistoryTest();

    @Test
    abstract void generateIDTest();

    @Test
    abstract void getTaskListTest();

    @Test
    abstract void getTaskTest();

    @Test
    abstract void getEpicTest();

    @Test
    abstract void getSubtaskTest();

    @Test
    abstract void createEpicTaskTest();

    @Test
    abstract void createATaskTest();

    @Test
    abstract void createASubtaskTest();

    @Test
    abstract void getPrioritizedTasksTest();
}