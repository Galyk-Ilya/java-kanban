import pattern.Managers;
import pattern.TaskManager;
import task.*;

public class Main {
    public static void main(String[] args) {
        TaskManager taskManager = Managers.getDefault();
        EpicTask epicTask1 = new EpicTask("Заголовок №1",
                "Тело задачи №1");
        Subtask subtask1_E1 = new Subtask("Заголовок №2",
                "Тело задачи №2");
        Subtask subtask2_E1 = new Subtask("Заголовок №3",
                "Тело задачи №3");
        Subtask subtask3_E1 = new Subtask("Заголовок №4",
                "Тело задачи №4");
        EpicTask epicTask2 = new EpicTask("Заголовок №5",
                "Тело задачи №6");


        EpicTask createEpic1 = taskManager.createATask(epicTask1, subtask1_E1, subtask2_E1, subtask3_E1);
//
//        taskManager.createATask(epicTask1, subtask1_E1, subtask1_E1, subtask1_E1);
        EpicTask createEpic2 = taskManager.createATask(epicTask2);
//        taskManager.getListSubtasks(eTask1);
//        taskManager.getAllTaskList();

//Функционал HistoryManager
        taskManager.getEpic(createEpic1.getId());
        taskManager.getSubtask(createEpic1.getSubtasksList().get(0).getId());
        taskManager.getSubtask(createEpic1.getSubtasksList().get(1).getId());
        taskManager.getSubtask(createEpic1.getSubtasksList().get(2).getId());
        taskManager.getEpic(createEpic2.getId());
        for (CommonTask task : taskManager.getHistory()) {
            System.out.println(task);
        }

        taskManager.getSubtask(createEpic1.getSubtasksList().get(2).getId());
        taskManager.getEpic(createEpic1.getId());
        for (CommonTask task : taskManager.getHistory()) {
            System.out.println(task);
        }

        System.out.println();
        taskManager.deleteByID(createEpic1.getId());
        for (CommonTask task : taskManager.getHistory()) {
            System.out.println(task);
        }
    }
}
