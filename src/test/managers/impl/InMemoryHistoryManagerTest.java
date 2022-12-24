package managers.impl;

import managers.HistoryManager;
import managers.Managers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import task.*;

import static org.junit.jupiter.api.Assertions.*;

public class InMemoryHistoryManagerTest {
    HistoryManager historyManager;

    @BeforeEach
    public void newManager() {
        historyManager = Managers.getDefaultHistory();
    }

    @Test
    public void removeTest() {
        CommonTask commonTask1 = new CommonTask("CommonTask1", "description n1");
        commonTask1.setId(1);
        historyManager.addLast(commonTask1);
        CommonTask commonTask2 = new CommonTask("CommonTask2", "description n2");
        commonTask2.setId(2);
        historyManager.addLast(commonTask2);
        CommonTask commonTask3 = new CommonTask("CommonTask3", "description n3");
        commonTask3.setId(3);
        historyManager.addLast(commonTask3);
        EpicTask epicTask = new EpicTask("EpicTask1", "descriptionEpic1");
        epicTask.setId(4);
        historyManager.addLast(epicTask);
        historyManager.remove(1);
        assertEquals(historyManager.getHistory().size(), 3);
        historyManager.remove(3);
        assertEquals(historyManager.getHistory().size(), 2);
        historyManager.remove(4);
        assertEquals(historyManager.getHistory().size(), 1);
        historyManager.addLast(epicTask);
        assertEquals(historyManager.getHistory().size(), 2);
        historyManager.remove(2);
        assertEquals(historyManager.getHistory().get(0).getId(), 4);
    }

    @Test
    void addLastTest() {
        assertTrue(historyManager.getHistory().isEmpty());
        CommonTask commonTask = new CommonTask("CommonTask1", "description n1");
        commonTask.setId(1);
        historyManager.addLast(commonTask);
        assertEquals(historyManager.getHistory().size(), 1);

        EpicTask epicTask = new EpicTask("EpicTask1", "descriptionEpic1");
        epicTask.setId(2);
        historyManager.addLast(epicTask);
        assertEquals(historyManager.getHistory().size(), 2);

        historyManager.addLast(commonTask);
        assertEquals(historyManager.getHistory().size(), 2);
    }

    @Test
    void getHistoryTest() {
        assertTrue(historyManager.getHistory().isEmpty());
    }
}
